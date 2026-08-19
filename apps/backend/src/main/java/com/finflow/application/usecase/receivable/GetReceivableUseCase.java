package com.finflow.application.usecase.receivable;

import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.receivable.Receivable;
import com.finflow.domain.repository.ReceivableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetReceivableUseCase {

    private final ReceivableRepository receivableRepository;

    public GetReceivableUseCase(ReceivableRepository receivableRepository) {
        this.receivableRepository = receivableRepository;
    }

    @Transactional(readOnly = true)
    public Receivable execute(UUID id) {
        return receivableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recebível", id));
    }
}
