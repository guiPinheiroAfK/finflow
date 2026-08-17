package com.finflow.domain.model.receivable;

import com.finflow.domain.model.customer.Customer;
import com.finflow.domain.model.order.Order;
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

/**
 * Parcela do cliente gerada na aprovação do orçamento (ADR-0003). A soma das
 * parcelas de uma Order bate exatamente com {@code order.totalSale} --
 * garantido por {@code Installments.split} (ADR-0001 §5).
 */
@Entity
@Table(name = "receivables")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receivable {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceivableStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Receivable create(Order order, Customer customer, String description,
                                     BigDecimal amount, LocalDate dueDate) {
        Receivable receivable = new Receivable();
        receivable.id = UUID.randomUUID();
        receivable.order = order;
        receivable.customer = customer;
        receivable.description = description;
        receivable.amount = amount;
        receivable.dueDate = dueDate;
        receivable.status = ReceivableStatus.PENDING;
        receivable.createdAt = LocalDateTime.now();
        return receivable;
    }
}
