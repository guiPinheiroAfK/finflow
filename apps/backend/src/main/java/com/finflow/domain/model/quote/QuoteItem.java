package com.finflow.domain.model.quote;

import com.finflow.domain.model.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quote_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuoteItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String description;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_cost", nullable = false)
    private BigDecimal unitCost;

    @Column(name = "unit_sale", nullable = false)
    private BigDecimal unitSale;

    @Column(name = "travel_date")
    private LocalDate travelDate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "passenger_names", columnDefinition = "text[]")
    private List<String> passengerNames;

    static QuoteItem create(Quote quote, Product product, String description, int quantity,
                             BigDecimal unitCost, BigDecimal unitSale, LocalDate travelDate,
                             List<String> passengerNames) {
        QuoteItem item = new QuoteItem();
        item.id = UUID.randomUUID();
        item.quote = quote;
        item.product = product;
        item.description = description;
        item.quantity = quantity;
        item.unitCost = unitCost;
        item.unitSale = unitSale;
        item.travelDate = travelDate;
        item.passengerNames = passengerNames;
        return item;
    }

    public BigDecimal totalCost() {
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal totalSale() {
        return unitSale.multiply(BigDecimal.valueOf(quantity));
    }
}
