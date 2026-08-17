package com.finflow.application.usecase.banktransaction;

import com.finflow.domain.model.banktransaction.BankTransaction;
import com.finflow.domain.repository.BankTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListBankTransactionsUseCase {

    private final BankTransactionRepository bankTransactionRepository;

    public ListBankTransactionsUseCase(BankTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Transactional(readOnly = true)
    public Page<BankTransaction> execute(Boolean reconciled, Pageable pageable) {
        if (reconciled != null) {
            return bankTransactionRepository.findByReconciled(reconciled, pageable);
        }
        return bankTransactionRepository.findAll(pageable);
    }
}
