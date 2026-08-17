package com.finflow.application.usecase.bankaccount;

import com.finflow.domain.model.bankaccount.BankAccount;
import com.finflow.domain.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListBankAccountsUseCase {

    private final BankAccountRepository bankAccountRepository;

    public ListBankAccountsUseCase(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<BankAccount> execute() {
        return bankAccountRepository.findAll();
    }
}
