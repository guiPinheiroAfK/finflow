package com.finflow.application.usecase.payable;

import com.finflow.domain.model.payable.Payable;
import com.finflow.domain.model.payable.PayableStatus;
import com.finflow.domain.repository.PayableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListPayablesUseCase {

    private final PayableRepository payableRepository;

    public ListPayablesUseCase(PayableRepository payableRepository) {
        this.payableRepository = payableRepository;
    }

    @Transactional(readOnly = true)
    public Page<Payable> execute(PayableStatus status, Pageable pageable) {
        if (status != null) {
            return payableRepository.findByStatus(status, pageable);
        }
        return payableRepository.findAll(pageable);
    }
}
