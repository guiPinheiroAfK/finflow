package com.finflow.application.dto.bankaccount;

import com.finflow.domain.model.shared.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BankAccountRequest(
        @NotBlank String name,
        @NotBlank String bankName,
        String agency,
        String accountNumber,
        @NotNull Currency currency
) {
}
