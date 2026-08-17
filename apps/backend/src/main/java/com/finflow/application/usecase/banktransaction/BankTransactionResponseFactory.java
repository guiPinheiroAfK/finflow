package com.finflow.application.usecase.banktransaction;

import com.finflow.application.dto.banktransaction.BankTransactionResponse;
import com.finflow.application.dto.banktransaction.MatchCandidateResponse;
import com.finflow.domain.model.banktransaction.BankTransaction;
import com.finflow.domain.model.payable.Payable;
import com.finflow.domain.model.receivable.Receivable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ADR-0004 §3: transações não conciliadas carregam os top-3 candidatos com o
 * detalhamento por sinal -- a UI mostra a mesma informação que o algoritmo usou.
 */
@Component
public class BankTransactionResponseFactory {

    private static final int TOP_N_CANDIDATES = 3;

    private final ReconciliationCandidateFinder candidateFinder;

    public BankTransactionResponseFactory(ReconciliationCandidateFinder candidateFinder) {
        this.candidateFinder = candidateFinder;
    }

    public BankTransactionResponse toResponse(BankTransaction tx) {
        List<MatchCandidateResponse> candidates = tx.isReconciled()
                ? List.of()
                : candidateFinder.find(tx).stream().limit(TOP_N_CANDIDATES).map(this::toCandidateResponse).toList();

        Receivable receivable = tx.getReceivable();
        Payable payable = tx.getPayable();

        return new BankTransactionResponse(
                tx.getId(), tx.getBankAccount().getId(), tx.getDate(), tx.getDescription(), tx.getAmount(),
                tx.getType(), tx.isReconciled(),
                receivable == null ? null : receivable.getId(),
                payable == null ? null : payable.getId(),
                tx.getMatchedBy(), tx.getMatchedScore(), tx.getMatchMargin(),
                candidates, tx.getCreatedAt());
    }

    private MatchCandidateResponse toCandidateResponse(ScoredCandidate candidate) {
        var breakdown = candidate.breakdown();
        return new MatchCandidateResponse(
                candidate.targetType(), candidate.targetId(), candidate.description(), candidate.amount(),
                candidate.dueDate(), breakdown.total(), breakdown.valueScore(), breakdown.dateScore(),
                breakdown.documentScore());
    }
}
