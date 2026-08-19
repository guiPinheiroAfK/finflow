package com.finflow.application.usecase.banktransaction;

import com.finflow.domain.model.banktransaction.BankTransaction;
import com.finflow.domain.model.banktransaction.MatchedBy;
import com.finflow.domain.repository.BankTransactionRepository;
import com.finflow.domain.service.ReconciliationScorer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** ADR-0004 §2: só concilia sozinho o candidato inequívoco -- ambíguo vai para revisão humana. */
@Service
public class AutoReconcileUseCase {

    private final BankTransactionRepository bankTransactionRepository;
    private final ReconciliationCandidateFinder candidateFinder;

    public AutoReconcileUseCase(BankTransactionRepository bankTransactionRepository,
                                 ReconciliationCandidateFinder candidateFinder) {
        this.bankTransactionRepository = bankTransactionRepository;
        this.candidateFinder = candidateFinder;
    }

    public record Result(int autoReconciled, int pendingReview) {
    }

    @Transactional
    public Result execute(UUID bankAccountId) {
        List<BankTransaction> unreconciled = bankTransactionRepository
                .findByBankAccountIdAndReconciledFalse(bankAccountId);

        int autoReconciled = 0;
        for (BankTransaction tx : unreconciled) {
            if (tryReconcile(tx)) {
                autoReconciled++;
            }
        }
        return new Result(autoReconciled, unreconciled.size() - autoReconciled);
    }

    private boolean tryReconcile(BankTransaction tx) {
        List<ScoredCandidate> candidates = candidateFinder.find(tx);
        if (candidates.isEmpty()) {
            return false;
        }

        double bestScore = candidates.get(0).breakdown().total();
        double secondBestScore = candidates.size() > 1 ? candidates.get(1).breakdown().total() : 0.0;

        if (!ReconciliationScorer.isAutoReconcilable(bestScore, secondBestScore)) {
            return false;
        }

        ScoredCandidate best = candidates.get(0);
        double margin = bestScore - secondBestScore;
        LocalDateTime now = LocalDateTime.now();

        if (best.receivable() != null) {
            best.receivable().pay(best.receivable().getAmount(), now);
            tx.reconcileWithReceivable(best.receivable(), MatchedBy.AUTO, bestScore, margin);
        } else {
            best.payable().pay(now);
            tx.reconcileWithPayable(best.payable(), MatchedBy.AUTO, bestScore, margin);
        }
        return true;
    }
}
