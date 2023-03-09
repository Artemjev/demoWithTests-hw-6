package com.example.demowithtests.service;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.repository.EmployeeRepository;
import com.example.demowithtests.util.exception.ResourceNotFoundException;
import com.example.demowithtests.util.mail.SmtpMailer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class EmployeeServiceBean implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final SmtpMailer smtpMailer;

    @Override
    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAll() {
        log.info("getAll() Service - start:");
        var employees = employeeRepository.findAll();
        employees.stream().forEach(employee -> {
            if (employee.getIsPrivate() == Boolean.TRUE || employee.getIsPrivate() == null)
                // добавил в параметры значение, которое нужно установить для гибкости (оно же может 
                // менять значение), что бы потом не дублировать код.
                hideEmployeeDetails(employee);
        });
        log.info("setEmployeePrivateFields() Service - end:  = size {}", employees.size());
        return employees;
    }

    private void hideEmployeeDetails(Employee employee) {
        log.debug("setEmployeePrivateFields() Service - start: id = {}", employee.getId());
        employee.setName("is hidden");
        employee.setEmail("is hidden");
        employee.setCountry("is hidden");
        employee.setAddresses(null);
        employee.setGender(null);
        log.debug("setEmployeePrivateFields() Service - end:  = employee {}", employee);
    }

    @Override
    public Page<Employee> getAllWithPagination(Pageable pageable) {
        log.debug("getAllWithPagination() - start: pageable = {}", pageable);
        Page<Employee> list = employeeRepository.findAll(pageable);
        list.stream().forEach(employee -> {
            if (employee.getIsPrivate() == Boolean.TRUE || employee.getIsPrivate() == null)
                hideEmployeeDetails(employee);
        });
        log.debug("getAllWithPagination() - end: list = {}", list);
        return list;
    }

    @Override
    public Employee getById(Integer id) {
        log.info("getById(Integer id) Service - start: id = {}", id);
        var employee = employeeRepository.findById(id)
                // .orElseThrow(() -> new EntityNotFoundException("Employee not found with id = " + id));
                .orElseThrow(ResourceNotFoundException::new);

//        Код Ярослава, относится к более поздним дз:
//        changeVisibleStatus(employee);
//        changePrivateStatus(employee);
//        if (!employee.getIsVisible()) throw new ResourceNotVisibleException();
//        if (employee.getIsPrivate()) throw new ResourceIsPrivateException();
//        log.info("getById(Integer id) Service - end:  = employee {}", employee);
        return employee;
    }

//    private void changePrivateStatus(Employee employee) {
//        log.info("changePrivateStatus() Service - start: id = {}", employee.getId());
//        if (employee.getIsPrivate() == null) {
//            employee.setIsPrivate(Boolean.TRUE);
//            employeeRepository.save(employee);
//        }
//        log.info("changePrivateStatus() Service - end: IsPrivate = {}", employee.getIsPrivate());
//    }
//
//    private void changeVisibleStatus(Employee employee) {
//        log.info("changeVisibleStatus() Service - start: id = {}", employee.getId());
//        if (employee.getIsVisible() == null) {
//            employee.setIsVisible(Boolean.TRUE);
//            employeeRepository.save(employee);
//        }
//        log.info("changeVisibleStatus() Service - end: isVisible = {}", employee.getIsVisible());
//    }

    @Override
    public Employee updateById(Integer id, Employee employee) {
        return employeeRepository.findById(id)
                .map(entity -> {
                    entity.setName(employee.getName());
                    entity.setEmail(employee.getEmail());
                    entity.setCountry(employee.getCountry());
                    entity.setIsDeleted(employee.getIsDeleted());
                    return employeeRepository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id = " + id));
    }

    @Override
    public void removeById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        employee.setIsDeleted(Boolean.FALSE);
        employeeRepository.save(employee);
    }

    @Override
    public void removeAll() {
        employeeRepository.deleteAll();
    }

    @Override
    public Page<Employee> findByCountryContaining(String country, int page, int size, List<String> sortList, String
            sortOrder) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sortList, sortOrder)));
        return employeeRepository.findByCountryContaining(country, pageable);
    }

    private List<Sort.Order> createSortOrder(List<String> sortList, String sortDirection) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort.Direction direction;
        for (String sort : sortList) {
            if (sortDirection != null) {
                direction = Sort.Direction.fromString(sortDirection);
            } else {
                direction = Sort.Direction.DESC;
            }
            sorts.add(new Sort.Order(direction, sort));
        }
        return sorts;
    }

    @Override
    public List<String> getAllEmployeeCountry() {
        log.info("getAllEmployeeCountry() - start:");
        List<Employee> employeeList = employeeRepository.findAll();
        List<String> countries = employeeList.stream()
                .map(country -> country.getCountry())
                .collect(Collectors.toList());
        log.info("getAllEmployeeCountry() - end: countries = {}", countries);
        return countries;
    }

    @Override
    public List<String> getSortCountry() {
        List<Employee> employeeList = employeeRepository.findAll();
        return employeeList.stream()
                .map(Employee::getCountry)
                .filter(c -> c.startsWith("U"))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<String> findEmails() {
        var employeeList = employeeRepository.findAll();

        var emails = employeeList.stream()
                .map(Employee::getEmail)
                .collect(Collectors.toList());

        var opt = emails.stream()
                .filter(s -> s.endsWith(".com"))
                .findFirst()
                .orElse("error?");
        return Optional.ofNullable(opt);
    }

    //    Делали в классе (но это не точно!)
    @Override
    public List<Employee> getByGender(Gender gender, String country) {
        /*System.err.println("service getByGender start: gender: " + gender + " country: " + country);*/
        var employees = employeeRepository.findByGender(gender.toString(), country);
        /*System.err.println("service getByGender end: " + employees.toString());*/
        return employees;
    }

    // Метод Ярослава (hw-3), который ищет активные адреса.
    // Передаем страну и получаем список работников, у которых активный адрес в этой стране.
    @Override
    public Page<Employee> getActiveAddressesByCountry(String country, Pageable pageable) {
        return employeeRepository.findAllWhereIsActiveAddressByCountry(country, pageable);
    }

    @Override
    public List<Employee> getWhereIsDeletedIsNull() {
        var employees = employeeRepository.queryEmployeeByIsDeletedIsNull();
        for (Employee employee : employees) employee.setIsDeleted(Boolean.FALSE);
        employeeRepository.saveAll(employees);
        return employeeRepository.queryEmployeeByIsDeletedIsNull();
    }

    @Override
    public List<Employee> getEmployeeByIsPrivateIsNull() {
        var employees = employeeRepository.queryEmployeeByIsPrivateIsNull();
        employees.forEach(employee -> employee.setIsPrivate(Boolean.FALSE));
        employeeRepository.saveAll(employees);
        return employeeRepository.queryEmployeeByIsPrivateIsNull();
    }

    //---------------------------------------------------------------------------------------
    //    My hw-3
    @Override
    public Page<Employee> getAllActive(Pageable pageable) {
        return employeeRepository.findAllActive(pageable);
    }

    @Override
    public Page<Employee> getAllDeleted(Pageable pageable) {
        return employeeRepository.findAllDeleted(pageable);
    }

    @Override
    public void sendConfirm(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        smtpMailer.send(employee);

    }

    @Override
    public void confirm(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        employee.setIsConfirmed(Boolean.TRUE);
        employeeRepository.save(employee);
    }
}



