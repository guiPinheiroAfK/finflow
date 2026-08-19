package com.finflow.application.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, Object id) {
        super("%s não encontrado: %s".formatted(resource, id), HttpStatus.NOT_FOUND);
    }
}
