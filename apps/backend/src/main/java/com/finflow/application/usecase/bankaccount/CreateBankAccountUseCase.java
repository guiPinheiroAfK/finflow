package com.finflow.application.usecase.bankaccount;

import com.finflow.application.dto.bankaccount.BankAccountRequest;
import com.finflow.domain.model.bankaccount.BankAccount;
import com.finflow.domain.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBankAccountUseCase {

    private final BankAccountRepository bankAccountRepository;

    public CreateBankAccountUseCase(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public BankAccount execute(BankAccountRequest request) {
        BankAccount account = BankAccount.create(request.name(), request.bankName(),
                request.agency(), request.accountNumber(), request.currency());
        return bankAccountRepository.save(account);
    }
}
