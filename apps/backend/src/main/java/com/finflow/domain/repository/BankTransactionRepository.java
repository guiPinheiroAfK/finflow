package com.finflow.domain.repository;

import com.finflow.domain.model.banktransaction.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {
    Page<BankTransaction> findByReconciled(boolean reconciled, Pageable pageable);
    List<BankTransaction> findByBankAccountIdAndReconciledFalse(UUID bankAccountId);
}
