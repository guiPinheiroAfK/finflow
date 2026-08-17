package com.finflow.domain.model.order;

import com.finflow.domain.model.product.Product;
import com.finflow.domain.model.quote.QuoteItem;
import com.finflow.domain.model.shared.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * ADR-0003 §1: snapshot imutável -- copiado de {@link QuoteItem} no momento da
 * aprovação, nunca recalculado a partir do {@link Product} atual.
 */
@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String description;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_cost", nullable = false)
    private BigDecimal unitCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_cost_currency", nullable = false)
    private Currency unitCostCurrency;

    @Column(name = "unit_cost_exchange_rate")
    private BigDecimal unitCostExchangeRate;

    @Column(name = "unit_cost_brl", nullable = false)
    private BigDecimal unitCostBrl;

    @Column(name = "unit_sale", nullable = false)
    private BigDecimal unitSale;

    @Column(name = "travel_date")
    private LocalDate travelDate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "passenger_names", columnDefinition = "text[]")
    private List<String> passengerNames;

    /**
     * @param exchangeRate cotação congelada, ou {@code null} se o produto já é BRL (ADR-0001 §4).
     */
    static OrderItem fromQuoteItem(Order order, QuoteItem quoteItem, BigDecimal exchangeRate) {
        Currency currency = quoteItem.getProduct().getCurrency();
        BigDecimal unitCostBrl = currency == Currency.BRL
                ? quoteItem.getUnitCost()
                : quoteItem.getUnitCost().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        OrderItem item = new OrderItem();
        item.id = UUID.randomUUID();
        item.order = order;
        item.product = quoteItem.getProduct();
        item.description = quoteItem.getDescription();
        item.quantity = quoteItem.getQuantity();
        item.unitCost = quoteItem.getUnitCost();
        item.unitCostCurrency = currency;
        item.unitCostExchangeRate = currency == Currency.BRL ? null : exchangeRate;
        item.unitCostBrl = unitCostBrl;
        item.unitSale = quoteItem.getUnitSale();
        item.travelDate = quoteItem.getTravelDate();
        item.passengerNames = quoteItem.getPassengerNames();
        return item;
    }

    public BigDecimal totalCostBrl() {
        return unitCostBrl.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal totalSale() {
        return unitSale.multiply(BigDecimal.valueOf(quantity));
    }
}
