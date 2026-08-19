package com.finflow.application.usecase.supplier;

import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.supplier.Supplier;
import com.finflow.domain.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetSupplierUseCase {

    private final SupplierRepository supplierRepository;

    public GetSupplierUseCase(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public Supplier execute(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));
    }
}
