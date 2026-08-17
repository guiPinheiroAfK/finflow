package com.finflow.application.usecase.banktransaction;

import com.finflow.domain.model.banktransaction.BankTransaction;
import com.finflow.domain.model.banktransaction.TransactionType;
import com.finflow.domain.model.payable.Payable;
import com.finflow.domain.model.payable.PayableStatus;
import com.finflow.domain.model.receivable.Receivable;
import com.finflow.domain.model.receivable.ReceivableStatus;
import com.finflow.domain.repository.PayableRepository;
import com.finflow.domain.repository.ReceivableRepository;
import com.finflow.domain.service.ReconciliationScorer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * ADR-0004 §1: busca candidatos em aberto na janela de ±2 dias e pontua cada
 * um. Compartilhado entre a fila de revisão (mostra os candidatos) e o
 * auto-reconcile (decide sozinho com base neles) -- mesma fonte de verdade.
 */
@Service
public class ReconciliationCandidateFinder {

    private final ReceivableRepository receivableRepository;
    private final PayableRepository payableRepository;

    public ReconciliationCandidateFinder(ReceivableRepository receivableRepository,
                                          PayableRepository payableRepository) {
        this.receivableRepository = receivableRepository;
        this.payableRepository = payableRepository;
    }

    /** Ordenado por score decrescente -- o primeiro é sempre o melhor candidato. */
    public List<ScoredCandidate> find(BankTransaction tx) {
        LocalDate from = tx.getDate().minusDays(ReconciliationScorer.CANDIDATE_SEARCH_WINDOW_DAYS);
        LocalDate to = tx.getDate().plusDays(ReconciliationScorer.CANDIDATE_SEARCH_WINDOW_DAYS);

        List<ScoredCandidate> candidates = tx.getType() == TransactionType.CREDIT
                ? receivableCandidates(tx, from, to)
                : payableCandidates(tx, from, to);

        return candidates.stream()
                .sorted(Comparator.comparingDouble((ScoredCandidate c) -> c.breakdown().total()).reversed())
                .toList();
    }

    private List<ScoredCandidate> receivableCandidates(BankTransaction tx, LocalDate from, LocalDate to) {
        List<Receivable> openReceivables = receivableRepository.findByStatusInAndDueDateBetween(
                List.of(ReceivableStatus.PENDING, ReceivableStatus.PARTIAL, ReceivableStatus.OVERDUE), from, to);

        return openReceivables.stream().map(r -> {
            var breakdown = ReconciliationScorer.breakdown(tx.getAmount(), tx.getDate(), tx.getDescription(),
                    r.getAmount(), r.getDueDate(), r.getCustomer().getDocument());
            return new ScoredCandidate("RECEIVABLE", r.getId(), r.getDescription(), r.getAmount(),
                    r.getDueDate(), breakdown, r, null);
        }).toList();
    }

    private List<ScoredCandidate> payableCandidates(BankTransaction tx, LocalDate from, LocalDate to) {
        List<Payable> openPayables = payableRepository.findByStatusAndDueDateBetween(
                PayableStatus.PENDING, from, to);

        return openPayables.stream().map(p -> {
            var breakdown = ReconciliationScorer.breakdown(tx.getAmount(), tx.getDate(), tx.getDescription(),
                    p.getAmountBrl(), p.getDueDate(), p.getSupplier().getDocument());
            return new ScoredCandidate("PAYABLE", p.getId(), p.getDescription(), p.getAmountBrl(),
                    p.getDueDate(), breakdown, null, p);
        }).toList();
    }
}
