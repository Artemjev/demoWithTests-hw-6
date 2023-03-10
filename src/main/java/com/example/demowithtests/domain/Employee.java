package com.example.demowithtests.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Employee {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String country;
    private String email;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id")
    private Set<Address> addresses = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Boolean isDeleted = Boolean.FALSE;

    //    не бизнесовое поле Ярослава. Если true, данные клиента скрыты.
    private Boolean isPrivate = Boolean.FALSE;

    //    мое не бизнесовое поле.
    //    Его суть: после заполнении данных о работнике, письмо с подверждением  данных
    //    отправляется на указанный email. Пока работник их не подтвердит, его учетная запись будет неактивна в системе
    //    (хз чего, может быть какого-нибудь завода)
    private Boolean isConfirmed = Boolean.FALSE;

}
