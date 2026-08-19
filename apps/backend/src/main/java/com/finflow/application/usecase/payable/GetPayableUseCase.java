package com.finflow.application.usecase.payable;

import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.payable.Payable;
import com.finflow.domain.repository.PayableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetPayableUseCase {

    private final PayableRepository payableRepository;

    public GetPayableUseCase(PayableRepository payableRepository) {
        this.payableRepository = payableRepository;
    }

    @Transactional(readOnly = true)
    public Payable execute(UUID id) {
        return payableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagável", id));
    }
}
