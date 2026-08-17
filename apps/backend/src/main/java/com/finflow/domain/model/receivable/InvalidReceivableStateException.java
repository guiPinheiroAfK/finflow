package com.finflow.domain.model.receivable;

import com.finflow.domain.model.shared.DomainStateConflictException;

import java.util.UUID;

public class InvalidReceivableStateException extends DomainStateConflictException {
    public InvalidReceivableStateException(UUID id, ReceivableStatus current, String action) {
        super("Recebível %s está em %s; não é possível %s".formatted(id, current, action));
    }
}
