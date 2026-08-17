package com.finflow.domain.model.shared;

/**
 * Violação de invariante de máquina de estados de um agregado (Quote, Order,
 * Receivable, Payable...). Domínio puro, sem HTTP -- GlobalExceptionHandler
 * mapeia toda a família para 409 Conflict com um único handler.
 */
public abstract class DomainStateConflictException extends RuntimeException {
    protected DomainStateConflictException(String message) {
        super(message);
    }
}
