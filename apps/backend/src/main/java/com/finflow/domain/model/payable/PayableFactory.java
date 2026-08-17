package com.finflow.domain.model.payable;

import com.finflow.domain.model.order.Order;
import com.finflow.domain.model.order.OrderItem;
import com.finflow.domain.model.shared.Currency;
import com.finflow.domain.model.supplier.Supplier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ADR-0003: um Payable por (fornecedor, moeda) envolvido na venda -- um item
 * de produto em USD e outro em BRL do mesmo fornecedor não se misturam num
 * único valor "amount", que é sempre denominado numa única moeda.
 */
public final class PayableFactory {

    private PayableFactory() {
    }

    public static List<Payable> generateBySupplier(Order order) {
        Map<GroupKey, List<OrderItem>> groups = new LinkedHashMap<>();
        for (OrderItem item : order.items()) {
            GroupKey key = new GroupKey(item.getProduct().getSupplier(), item.getUnitCostCurrency());
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }

        LocalDate baseDate = order.getConfirmedAt().toLocalDate();
        List<Payable> payables = new ArrayList<>();

        groups.forEach((key, items) -> {
            BigDecimal amount = items.stream()
                    .map(i -> i.getUnitCost().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal amountBrl = items.stream()
                    .map(OrderItem::totalCostBrl)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal exchangeRate = items.get(0).getUnitCostExchangeRate();

            LocalDate dueDate = baseDate.plusDays(key.supplier().getPaymentTermDays());
            String description = "%s -- %s".formatted(order.getOrderNumber(), key.supplier().getName());

            payables.add(Payable.create(order, key.supplier(), description, amount,
                    key.currency(), exchangeRate, amountBrl, dueDate));
        });

        return payables;
    }

    private record GroupKey(Supplier supplier, Currency currency) {
    }
}
