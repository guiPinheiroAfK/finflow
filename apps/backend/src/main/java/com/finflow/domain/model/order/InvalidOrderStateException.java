package com.finflow.domain.model.order;

import com.finflow.domain.model.shared.DomainStateConflictException;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/** Violação de invariante da máquina de estados de {@link Order}. */
public class InvalidOrderStateException extends DomainStateConflictException {
    public InvalidOrderStateException(UUID orderId, OrderStatus current, String action, OrderStatus... expected) {
        super("Venda %s está em %s; não é possível %s (esperado: %s)".formatted(
                orderId, current, action,
                Arrays.stream(expected).map(Enum::name).collect(Collectors.joining(" ou "))));
    }
}
