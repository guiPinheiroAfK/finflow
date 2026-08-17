package com.finflow.application.usecase.banktransaction;

import com.finflow.application.dto.banktransaction.ManualReconcileRequest;
import com.finflow.application.exception.BusinessException;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.banktransaction.BankTransaction;
import com.finflow.domain.model.banktransaction.MatchedBy;
import com.finflow.domain.repository.BankTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** ADR-0004 §3: confirmação manual sempre grava score/margem -- dado de auditoria, não decisão perdida. */
@Service
public class ManualReconcileUseCase {

    private final BankTransactionRepository bankTransactionRepository;
    private final ReconciliationCandidateFinder candidateFinder;

    public ManualReconcileUseCase(BankTransactionRepository bankTransactionRepository,
                                   ReconciliationCandidateFinder candidateFinder) {
        this.bankTransactionRepository = bankTransactionRepository;
        this.candidateFinder = candidateFinder;
    }

    @Transactional
    public BankTransaction execute(UUID bankTransactionId, ManualReconcileRequest request) {
        if ((request.receivableId() == null) == (request.payableId() == null)) {
            throw new InvalidReconcileRequestException();
        }

        BankTransaction tx = bankTransactionRepository.findById(bankTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação bancária", bankTransactionId));

        List<ScoredCandidate> candidates = candidateFinder.find(tx);
        UUID chosenId = request.receivableId() != null ? request.receivableId() : request.payableId();
        ScoredCandidate chosen = candidates.stream()
                .filter(c -> c.targetId().equals(chosenId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidato de conciliação (fora da janela de ±2 dias ou já baixado)", chosenId));

        double score = chosen.breakdown().total();
        double bestOther = candidates.stream()
                .filter(c -> !c.targetId().equals(chosenId))
                .mapToDouble(c -> c.breakdown().total())
                .max().orElse(0.0);
        double margin = score - bestOther;

        LocalDateTime now = LocalDateTime.now();
        if (chosen.receivable() != null) {
            chosen.receivable().pay(chosen.receivable().getAmount(), now);
            tx.reconcileWithReceivable(chosen.receivable(), MatchedBy.MANUAL, score, margin);
        } else {
            chosen.payable().pay(now);
            tx.reconcileWithPayable(chosen.payable(), MatchedBy.MANUAL, score, margin);
        }
        return tx;
    }

    private static class InvalidReconcileRequestException extends BusinessException {
        InvalidReconcileRequestException() {
            super("Informe exatamente um de receivableId ou payableId", HttpStatus.BAD_REQUEST);
        }
    }
}
