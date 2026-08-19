package com.finflow.application.usecase.banktransaction;

import com.finflow.application.exception.InvalidCsvException;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.bankaccount.BankAccount;
import com.finflow.domain.model.banktransaction.BankTransaction;
import com.finflow.domain.repository.BankAccountRepository;
import com.finflow.domain.repository.BankTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Upload de extrato em CSV: {@code data,descricao,valor} (uma linha de
 * cabeçalho opcional). Valor positivo = crédito, negativo = débito.
 * OFX fica para uma evolução futura -- o schema de {@link BankTransaction}
 * não depende do formato de origem, só do resultado já parseado.
 */
@Service
public class UploadBankTransactionsUseCase {

    private final BankAccountRepository bankAccountRepository;
    private final BankTransactionRepository bankTransactionRepository;

    public UploadBankTransactionsUseCase(BankAccountRepository bankAccountRepository,
                                          BankTransactionRepository bankTransactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Transactional
    public List<BankTransaction> execute(UUID bankAccountId, MultipartFile file) {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária", bankAccountId));

        List<BankTransaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || isHeader(lineNumber, line)) {
                    continue;
                }
                transactions.add(parseLine(bankAccount, lineNumber, line));
            }
        } catch (IOException e) {
            throw new InvalidCsvException(0, "não foi possível ler o arquivo: " + e.getMessage());
        }

        return bankTransactionRepository.saveAll(transactions);
    }

    private boolean isHeader(int lineNumber, String line) {
        return lineNumber == 1 && !Character.isDigit(line.trim().charAt(0));
    }

    private BankTransaction parseLine(BankAccount bankAccount, int lineNumber, String line) {
        String[] parts = line.split(",", 3);
        if (parts.length != 3) {
            throw new InvalidCsvException(lineNumber, "esperado 3 colunas (data,descricao,valor)");
        }
        try {
            LocalDate date = LocalDate.parse(parts[0].trim());
            String description = parts[1].trim();
            BigDecimal amount = new BigDecimal(parts[2].trim());
            return BankTransaction.create(bankAccount, date, description, amount);
        } catch (DateTimeParseException e) {
            throw new InvalidCsvException(lineNumber, "data inválida (esperado AAAA-MM-DD): " + parts[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCsvException(lineNumber, "valor inválido: " + parts[2]);
        }
    }
}
