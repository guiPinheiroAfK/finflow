package com.finflow.application.usecase.payable;

import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.payable.Payable;
import com.finflow.domain.repository.PayableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PayPayableUseCase {

    private final PayableRepository payableRepository;

    public PayPayableUseCase(PayableRepository payableRepository) {
        this.payableRepository = payableRepository;
    }

    @Transactional
    public Payable execute(UUID id) {
        Payable payable = payableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagável", id));
        payable.pay(LocalDateTime.now());
        return payable;
    }
}
