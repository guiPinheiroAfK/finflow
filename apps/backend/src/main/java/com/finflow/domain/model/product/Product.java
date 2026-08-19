package com.finflow.domain.model.product;

import com.finflow.domain.model.shared.Currency;
import com.finflow.domain.model.supplier.Supplier;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "cost_price", nullable = false)
    private BigDecimal costPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice;

    // Coluna gerada pelo Postgres (V5): (sale_price - cost_price) / cost_price -- nunca escrita pela aplicação.
    @Column(name = "markup_pct", insertable = false, updatable = false)
    private BigDecimal markupPct;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Product create(String name, ProductCategory category, Supplier supplier,
                                  BigDecimal costPrice, Currency currency, BigDecimal salePrice) {
        Product product = new Product();
        product.id = UUID.randomUUID();
        product.name = name;
        product.category = category;
        product.supplier = supplier;
        product.costPrice = costPrice;
        product.currency = currency;
        product.salePrice = salePrice;
        product.active = true;
        product.createdAt = LocalDateTime.now();
        return product;
    }

    public void update(String name, ProductCategory category, Supplier supplier, BigDecimal costPrice,
                        Currency currency, BigDecimal salePrice) {
        this.name = name;
        this.category = category;
        this.supplier = supplier;
        this.costPrice = costPrice;
        this.currency = currency;
        this.salePrice = salePrice;
    }

    public void deactivate() {
        this.active = false;
    }
}
