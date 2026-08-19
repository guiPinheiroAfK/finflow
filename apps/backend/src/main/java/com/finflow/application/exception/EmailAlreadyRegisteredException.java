package com.finflow.application.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends BusinessException {
    public EmailAlreadyRegisteredException(String email) {
        super("E-mail já cadastrado: " + email, HttpStatus.CONFLICT);
    }
}
