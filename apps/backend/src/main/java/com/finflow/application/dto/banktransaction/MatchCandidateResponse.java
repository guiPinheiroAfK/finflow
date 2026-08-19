package com.finflow.application.dto.banktransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** ADR-0004 §3: a fila de revisão mostra o mesmo detalhamento por sinal que o algoritmo usou. */
public record MatchCandidateResponse(
        String targetType, // "RECEIVABLE" | "PAYABLE"
        UUID targetId,
        String description,
        BigDecimal amount,
        LocalDate dueDate,
        double score,
        double valueScore,
        double dateScore,
        double documentScore
) {
}
