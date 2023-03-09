package com.example.demowithtests.util.mail;

import com.example.demowithtests.domain.Employee;

import java.util.function.Function;

public interface Mailer /*extends Function*/ {
    //    void apply(Object o);
    void send(Employee e);
}




