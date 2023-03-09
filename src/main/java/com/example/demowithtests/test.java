package com.example.demowithtests;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.util.mail.SmtpMailer;

public class test {
    public static void main(String[] args) throws Exception {

        new SmtpMailer().send(new Employee().builder()
                .id(777)
                .name("me")
                .email("artemjev.mih@gmail.com")
                .build());
    }
}
