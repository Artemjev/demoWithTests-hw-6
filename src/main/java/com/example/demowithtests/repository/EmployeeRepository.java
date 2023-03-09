package com.example.demowithtests.repository;

import com.example.demowithtests.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Employee findByName(String name);

    @NotNull
    Page<Employee> findAll(Pageable pageable);

    Page<Employee> findByName(String name, Pageable pageable);

    Page<Employee> findByCountryContaining(String country, Pageable pageable);

    @Query(value = "select * from users join addresses on users.id = addresses.employee_id " +
            "where users.gender = :gender and addresses.country = :country", nativeQuery = true)
    List<Employee> findByGender(String gender, String country);

    @Query("select e from Employee e join e.addresses a where a.addressHasActive = true and a.country = :country")
    Page<Employee> findAllWhereIsActiveAddressByCountry(String country, Pageable pageable);

    //---------------------------------------------------------------
    //    My hw-5
    @Query("select e from Employee e where e.isDeleted = false")
    Page<Employee> findAllActive(Pageable pageable);

    @Query("select e from Employee e where e.isDeleted = true")
    Page<Employee> findAllDeleted(Pageable pageable);

    //---------------------------------------------------------------
    List<Employee> queryEmployeeByIsDeletedIsNull();

    List<Employee> queryEmployeeByIsPrivateIsNull();

    List<Employee> queryEmployeeByIsConfirmedNull();


}
