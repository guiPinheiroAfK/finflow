package com.finflow.domain.model.quote;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Violação de invariante da máquina de estados de {@link Quote} (ADR-0003 §2).
 * Puramente de domínio -- não conhece HTTP; {@code GlobalExceptionHandler}
 * mapeia para 409 Conflict.
 */
public class InvalidQuoteStateException extends RuntimeException {
    public InvalidQuoteStateException(UUID quoteId, QuoteStatus current, String action, QuoteStatus... expected) {
        super("Orçamento %s está em %s; não é possível %s (esperado: %s)".formatted(
                quoteId, current, action,
                Arrays.stream(expected).map(Enum::name).collect(Collectors.joining(" ou "))));
    }
}
