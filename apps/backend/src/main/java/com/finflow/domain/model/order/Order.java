package com.finflow.domain.model.order;

import com.finflow.domain.model.customer.Customer;
import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.model.shared.Currency;
import com.finflow.domain.model.shared.PaymentMethod;
import com.finflow.domain.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", unique = true)
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private int installments;

    @Column(name = "total_sale", nullable = false)
    private BigDecimal totalSale;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "gross_margin", nullable = false)
    private BigDecimal grossMargin;

    @Column(name = "commission_pct", nullable = false)
    private BigDecimal commissionPct;

    @Column(name = "commission_value", nullable = false)
    private BigDecimal commissionValue;

    @Column(name = "confirmed_at", nullable = false)
    private LocalDateTime confirmedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<OrderItem> items = new ArrayList<>();

    /**
     * ADR-0003 §1: snapshot congelado -- itens, preços e câmbio copiados do
     * orçamento no momento da aprovação, nunca referenciados ao vivo depois.
     *
     * @param exchangeRateResolver resolve a cotação do dia para uma moeda estrangeira;
     *                              não é chamado para itens já em BRL.
     */
    public static Order fromQuote(String orderNumber, Quote quote, PaymentMethod paymentMethod,
                                   int installments, Function<Currency, BigDecimal> exchangeRateResolver) {
        Order order = new Order();
        order.id = UUID.randomUUID();
        order.orderNumber = orderNumber;
        order.quote = quote;
        order.customer = quote.getCustomer();
        order.seller = quote.getSeller();
        order.status = OrderStatus.CONFIRMED;
        order.paymentMethod = paymentMethod;
        order.installments = installments;
        order.confirmedAt = LocalDateTime.now();
        order.createdAt = LocalDateTime.now();

        Map<Currency, BigDecimal> resolvedRates = new EnumMap<>(Currency.class);
        quote.items().forEach(quoteItem -> {
            Currency currency = quoteItem.getProduct().getCurrency();
            BigDecimal rate = currency == Currency.BRL ? null
                    : resolvedRates.computeIfAbsent(currency, exchangeRateResolver);
            order.items.add(OrderItem.fromQuoteItem(order, quoteItem, rate));
        });

        BigDecimal totalCost = order.items.stream()
                .map(OrderItem::totalCostBrl).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalSale = order.items.stream()
                .map(OrderItem::totalSale).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        order.totalCost = totalCost;
        order.totalSale = totalSale;
        order.grossMargin = totalSale.subtract(totalCost);
        order.commissionPct = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        order.commissionValue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return order;
    }

    public void issue() {
        if (status != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStateException(id, status, "emitir", OrderStatus.CONFIRMED);
        }
        this.status = OrderStatus.ISSUED;
    }

    public void cancel() {
        if (status == OrderStatus.CANCELLED || status == OrderStatus.COMPLETED) {
            throw new InvalidOrderStateException(id, status, "cancelar", OrderStatus.CONFIRMED, OrderStatus.ISSUED);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public List<OrderItem> items() {
        return List.copyOf(items);
    }
}
