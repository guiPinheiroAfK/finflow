package com.finflow.application.exception;

import org.springframework.http.HttpStatus;

public class DocumentAlreadyRegisteredException extends BusinessException {
    public DocumentAlreadyRegisteredException(String document) {
        super("Documento já cadastrado: " + document, HttpStatus.CONFLICT);
    }
}
