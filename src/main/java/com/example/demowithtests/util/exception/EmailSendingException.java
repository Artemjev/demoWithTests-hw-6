package com.example.demowithtests.util.exception;

public class EmailSendingException extends RuntimeException{
    public EmailSendingException(String message) {
        super(message);
    }
}
