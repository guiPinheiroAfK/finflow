package com.finflow.application.dto.banktransaction;

import com.finflow.domain.model.banktransaction.MatchedBy;
import com.finflow.domain.model.banktransaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BankTransactionResponse(
        UUID id,
        UUID bankAccountId,
        LocalDate date,
        String description,
        BigDecimal amount,
        TransactionType type,
        boolean reconciled,
        UUID receivableId,
        UUID payableId,
        MatchedBy matchedBy,
        BigDecimal matchedScore,
        BigDecimal matchMargin,
        List<MatchCandidateResponse> candidates,
        LocalDateTime createdAt
) {
}
