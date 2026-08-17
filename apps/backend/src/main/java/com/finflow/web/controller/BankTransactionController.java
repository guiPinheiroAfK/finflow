package com.finflow.web.controller;

import com.finflow.application.dto.banktransaction.BankTransactionResponse;
import com.finflow.application.dto.banktransaction.ManualReconcileRequest;
import com.finflow.application.usecase.banktransaction.AutoReconcileUseCase;
import com.finflow.application.usecase.banktransaction.BankTransactionResponseFactory;
import com.finflow.application.usecase.banktransaction.ListBankTransactionsUseCase;
import com.finflow.application.usecase.banktransaction.ManualReconcileUseCase;
import com.finflow.application.usecase.banktransaction.UploadBankTransactionsUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bank-transactions")
public class BankTransactionController {

    private final UploadBankTransactionsUseCase uploadBankTransactionsUseCase;
    private final ListBankTransactionsUseCase listBankTransactionsUseCase;
    private final ManualReconcileUseCase manualReconcileUseCase;
    private final AutoReconcileUseCase autoReconcileUseCase;
    private final BankTransactionResponseFactory responseFactory;

    public BankTransactionController(UploadBankTransactionsUseCase uploadBankTransactionsUseCase,
                                      ListBankTransactionsUseCase listBankTransactionsUseCase,
                                      ManualReconcileUseCase manualReconcileUseCase,
                                      AutoReconcileUseCase autoReconcileUseCase,
                                      BankTransactionResponseFactory responseFactory) {
        this.uploadBankTransactionsUseCase = uploadBankTransactionsUseCase;
        this.listBankTransactionsUseCase = listBankTransactionsUseCase;
        this.manualReconcileUseCase = manualReconcileUseCase;
        this.autoReconcileUseCase = autoReconcileUseCase;
        this.responseFactory = responseFactory;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<BankTransactionResponse>> upload(
            @RequestParam UUID bankAccountId, @RequestPart MultipartFile file) {
        var transactions = uploadBankTransactionsUseCase.execute(bankAccountId, file);
        var response = transactions.stream().map(responseFactory::toResponse).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<BankTransactionResponse>> list(@RequestParam(required = false) Boolean reconciled,
                                                                Pageable pageable) {
        return ResponseEntity.ok(listBankTransactionsUseCase.execute(reconciled, pageable).map(responseFactory::toResponse));
    }

    @PostMapping("/{id}/reconcile")
    public ResponseEntity<BankTransactionResponse> reconcile(@PathVariable UUID id,
                                                               @Valid @RequestBody ManualReconcileRequest request) {
        var tx = manualReconcileUseCase.execute(id, request);
        return ResponseEntity.ok(responseFactory.toResponse(tx));
    }

    @PostMapping("/auto-reconcile")
    public ResponseEntity<AutoReconcileUseCase.Result> autoReconcile(@RequestParam UUID bankAccountId) {
        return ResponseEntity.ok(autoReconcileUseCase.execute(bankAccountId));
    }
}
