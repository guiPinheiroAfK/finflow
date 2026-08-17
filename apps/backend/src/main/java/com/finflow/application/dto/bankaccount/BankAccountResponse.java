package com.finflow.application.dto.bankaccount;

import com.finflow.domain.model.shared.Currency;

import java.time.LocalDateTime;
import java.util.UUID;

public record BankAccountResponse(
        UUID id,
        String name,
        String bankName,
        String agency,
        String accountNumber,
        Currency currency,
        boolean active,
        LocalDateTime createdAt
) {
}
