package com.finflow.application.usecase.banktransaction;

import com.finflow.domain.model.payable.Payable;
import com.finflow.domain.model.receivable.Receivable;
import com.finflow.domain.service.ReconciliationScorer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Candidato pontuado -- carrega a entidade real para permitir a baixa se escolhido. */
public record ScoredCandidate(
        String targetType, // "RECEIVABLE" | "PAYABLE"
        UUID targetId,
        String description,
        BigDecimal amount,
        LocalDate dueDate,
        ReconciliationScorer.ScoreBreakdown breakdown,
        Receivable receivable,
        Payable payable
) {
}
