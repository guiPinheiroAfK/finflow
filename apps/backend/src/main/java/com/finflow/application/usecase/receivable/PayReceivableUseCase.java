package com.finflow.application.usecase.receivable;

import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.receivable.Receivable;
import com.finflow.domain.repository.ReceivableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PayReceivableUseCase {

    private final ReceivableRepository receivableRepository;

    public PayReceivableUseCase(ReceivableRepository receivableRepository) {
        this.receivableRepository = receivableRepository;
    }

    @Transactional
    public Receivable execute(UUID id, BigDecimal amount) {
        Receivable receivable = receivableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recebível", id));
        receivable.pay(amount, LocalDateTime.now());
        return receivable;
    }
}
