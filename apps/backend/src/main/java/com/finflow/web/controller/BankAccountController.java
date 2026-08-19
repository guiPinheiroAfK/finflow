package com.finflow.web.controller;

import com.finflow.application.dto.bankaccount.BankAccountRequest;
import com.finflow.application.dto.bankaccount.BankAccountResponse;
import com.finflow.application.mapper.BankAccountMapper;
import com.finflow.application.usecase.bankaccount.CreateBankAccountUseCase;
import com.finflow.application.usecase.bankaccount.ListBankAccountsUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bank-accounts")
public class BankAccountController {

    private final CreateBankAccountUseCase createBankAccountUseCase;
    private final ListBankAccountsUseCase listBankAccountsUseCase;
    private final BankAccountMapper mapper;

    public BankAccountController(CreateBankAccountUseCase createBankAccountUseCase,
                                  ListBankAccountsUseCase listBankAccountsUseCase, BankAccountMapper mapper) {
        this.createBankAccountUseCase = createBankAccountUseCase;
        this.listBankAccountsUseCase = listBankAccountsUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> create(@Valid @RequestBody BankAccountRequest request) {
        var account = createBankAccountUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(account));
    }

    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> list() {
        return ResponseEntity.ok(listBankAccountsUseCase.execute().stream().map(mapper::toResponse).toList());
    }
}
