package com.finflow.domain.model.banktransaction;

import com.finflow.domain.model.bankaccount.BankAccount;
import com.finflow.domain.model.payable.Payable;
import com.finflow.domain.model.receivable.Receivable;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** ADR-0004: toda transação de extrato termina em AUTO_RECONCILED ou PENDING_REVIEW -- nunca "ignorada". */
@Entity
@Table(name = "bank_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankTransaction {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private boolean reconciled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivable_id")
    private Receivable receivable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payable_id")
    private Payable payable;

    @Enumerated(EnumType.STRING)
    @Column(name = "matched_by")
    private MatchedBy matchedBy;

    @Column(name = "matched_score")
    private BigDecimal matchedScore;

    @Column(name = "match_margin")
    private BigDecimal matchMargin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static BankTransaction create(BankAccount bankAccount, LocalDate date, String description,
                                          BigDecimal amount) {
        BankTransaction tx = new BankTransaction();
        tx.id = UUID.randomUUID();
        tx.bankAccount = bankAccount;
        tx.date = date;
        tx.description = description;
        tx.amount = amount;
        tx.type = amount.signum() >= 0 ? TransactionType.CREDIT : TransactionType.DEBIT;
        tx.reconciled = false;
        tx.createdAt = LocalDateTime.now();
        return tx;
    }

    public void reconcileWithReceivable(Receivable receivable, MatchedBy matchedBy, double score, double margin) {
        this.receivable = receivable;
        this.reconciled = true;
        this.matchedBy = matchedBy;
        this.matchedScore = toScoreScale(score);
        this.matchMargin = toScoreScale(margin);
    }

    public void reconcileWithPayable(Payable payable, MatchedBy matchedBy, double score, double margin) {
        this.payable = payable;
        this.reconciled = true;
        this.matchedBy = matchedBy;
        this.matchedScore = toScoreScale(score);
        this.matchMargin = toScoreScale(margin);
    }

    private static BigDecimal toScoreScale(double value) {
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP);
    }
}
