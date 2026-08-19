package com.finflow.domain.model.payable;

import com.finflow.domain.model.order.Order;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** Pagável por fornecedor gerado na aprovação do orçamento (ADR-0003), um por fornecedor envolvido na venda. */
@Entity
@Table(name = "payables")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payable {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "amount_brl", nullable = false)
    private BigDecimal amountBrl;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayableStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Payable create(Order order, Supplier supplier, String description, BigDecimal amount,
                                  Currency currency, BigDecimal exchangeRate, BigDecimal amountBrl,
                                  LocalDate dueDate) {
        Payable payable = new Payable();
        payable.id = UUID.randomUUID();
        payable.order = order;
        payable.supplier = supplier;
        payable.description = description;
        payable.amount = amount;
        payable.currency = currency;
        payable.exchangeRate = exchangeRate;
        payable.amountBrl = amountBrl;
        payable.dueDate = dueDate;
        payable.status = PayableStatus.PENDING;
        payable.createdAt = LocalDateTime.now();
        return payable;
    }

    public void pay(LocalDateTime paidAt) {
        if (status != PayableStatus.PENDING) {
            throw new InvalidPayableStateException(id, status, "baixar");
        }
        this.status = PayableStatus.PAID;
        this.paidAt = paidAt;
    }
}
