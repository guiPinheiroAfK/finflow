package com.finflow.domain.model.payable;

import com.finflow.domain.model.shared.DomainStateConflictException;

import java.util.UUID;

public class InvalidPayableStateException extends DomainStateConflictException {
    public InvalidPayableStateException(UUID id, PayableStatus current, String action) {
        super("Pagável %s está em %s; não é possível %s".formatted(id, current, action));
    }
}
