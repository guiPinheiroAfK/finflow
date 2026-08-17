package com.finflow.application.usecase.receivable;

import com.finflow.domain.model.receivable.Receivable;
import com.finflow.domain.model.receivable.ReceivableStatus;
import com.finflow.domain.repository.ReceivableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListReceivablesUseCase {

    private final ReceivableRepository receivableRepository;

    public ListReceivablesUseCase(ReceivableRepository receivableRepository) {
        this.receivableRepository = receivableRepository;
    }

    @Transactional(readOnly = true)
    public Page<Receivable> execute(ReceivableStatus status, Pageable pageable) {
        if (status != null) {
            return receivableRepository.findByStatus(status, pageable);
        }
        return receivableRepository.findAll(pageable);
    }
}
