package com.finflow.application.dto.dashboard;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        BigDecimal openReceivablesAmount,
        long openReceivablesCount,
        long overdueReceivablesCount,
        BigDecimal openPayablesAmount,
        long openPayablesCount,
        long pendingQuotesCount,
        long confirmedOrdersCount
) {
}
