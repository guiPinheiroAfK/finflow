package com.finflow.application.dto.receivable;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PayReceivableRequest(@NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal amount) {
}
