package com.finflow.domain.model.receivable;

import com.finflow.domain.model.order.Order;
import com.finflow.domain.model.shared.Installments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** ADR-0003: gera as parcelas do cliente na aprovação -- soma bate com order.totalSale (ADR-0001 §5). */
public final class ReceivableFactory {

    private ReceivableFactory() {
    }

    public static List<Receivable> generate(Order order) {
        List<BigDecimal> installmentAmounts = Installments.split(order.getTotalSale(), order.getInstallments());
        LocalDate baseDate = order.getConfirmedAt().toLocalDate();

        List<Receivable> receivables = new ArrayList<>();
        for (int i = 0; i < installmentAmounts.size(); i++) {
            String description = "%s -- parcela %d/%d".formatted(order.getOrderNumber(), i + 1, installmentAmounts.size());
            LocalDate dueDate = baseDate.plusMonths(i + 1L);
            receivables.add(Receivable.create(order, order.getCustomer(), description, installmentAmounts.get(i), dueDate));
        }
        return receivables;
    }
}
