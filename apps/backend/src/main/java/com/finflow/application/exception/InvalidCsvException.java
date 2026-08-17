package com.finflow.application.exception;

import org.springframework.http.HttpStatus;

public class InvalidCsvException extends BusinessException {
    public InvalidCsvException(int lineNumber, String reason) {
        super("Linha %d do CSV inválida: %s".formatted(lineNumber, reason), HttpStatus.BAD_REQUEST);
    }
}
