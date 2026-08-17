package com.finflow.application.exception;

import org.springframework.http.HttpStatus;

/** Base para exceções de regra de negócio que devem virar uma resposta HTTP previsível. */
public abstract class BusinessException extends RuntimeException {

    private final HttpStatus status;

    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}
