package com.finflow.domain.model.order;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/** Violação de invariante da máquina de estados de {@link Order}. Domínio puro, sem HTTP. */
public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(UUID orderId, OrderStatus current, String action, OrderStatus... expected) {
        super("Venda %s está em %s; não é possível %s (esperado: %s)".formatted(
                orderId, current, action,
                Arrays.stream(expected).map(Enum::name).collect(Collectors.joining(" ou "))));
    }
}
