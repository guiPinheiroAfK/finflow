package com.finflow.domain.model.quote;

import com.finflow.domain.model.customer.Customer;
import com.finflow.domain.model.product.Product;
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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quotes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote {

    @Id
    private UUID id;

    @Column(name = "quote_number", nullable = false, unique = true)
    private String quoteNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteStatus status;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    private String notes;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "total_sale", nullable = false)
    private BigDecimal totalSale;

    @Column(nullable = false)
    private BigDecimal margin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<QuoteItem> items = new ArrayList<>();

    public static Quote create(String quoteNumber, Customer customer, User seller,
                                LocalDate validUntil, String notes) {
        Quote quote = new Quote();
        quote.id = UUID.randomUUID();
        quote.quoteNumber = quoteNumber;
        quote.customer = customer;
        quote.seller = seller;
        quote.status = QuoteStatus.DRAFT;
        quote.validUntil = validUntil;
        quote.notes = notes;
        quote.totalCost = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        quote.totalSale = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        quote.margin = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        quote.createdAt = LocalDateTime.now();
        return quote;
    }

    public void addItem(Product product, String description, int quantity, BigDecimal unitCost,
                         BigDecimal unitSale, LocalDate travelDate, List<String> passengerNames) {
        requireEditable();
        items.add(QuoteItem.create(this, product, description, quantity, unitCost, unitSale,
                travelDate, passengerNames == null ? List.of() : passengerNames));
        recalculateTotals();
    }

    public void clearItems() {
        requireEditable();
        items.clear();
        recalculateTotals();
    }

    public void updateHeader(LocalDate validUntil, String notes) {
        requireEditable();
        this.validUntil = validUntil;
        this.notes = notes;
    }

    private void recalculateTotals() {
        BigDecimal cost = items.stream().map(QuoteItem::totalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sale = items.stream().map(QuoteItem::totalSale).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalCost = cost.setScale(2, RoundingMode.HALF_UP);
        this.totalSale = sale.setScale(2, RoundingMode.HALF_UP);
        this.margin = this.totalSale.subtract(this.totalCost);
    }

    private void requireEditable() {
        if (status != QuoteStatus.DRAFT) {
            throw new InvalidQuoteStateException(id, status, "editar", QuoteStatus.DRAFT);
        }
    }

    public void markSent() {
        if (status != QuoteStatus.DRAFT) {
            throw new InvalidQuoteStateException(id, status, "enviar", QuoteStatus.DRAFT);
        }
        this.status = QuoteStatus.SENT;
    }

    /** ADR-0003 §2: guarda de estado -- só aprova a partir de SENT/DRAFT. */
    public void requireApprovable() {
        if (status != QuoteStatus.SENT && status != QuoteStatus.DRAFT) {
            throw new InvalidQuoteStateException(id, status, "aprovar", QuoteStatus.DRAFT, QuoteStatus.SENT);
        }
    }

    public void markApproved() {
        this.status = QuoteStatus.APPROVED;
    }

    public boolean isApproved() {
        return status == QuoteStatus.APPROVED;
    }

    public List<QuoteItem> items() {
        return List.copyOf(items);
    }
}
