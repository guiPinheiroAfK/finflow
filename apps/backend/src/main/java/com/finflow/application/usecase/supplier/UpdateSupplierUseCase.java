package com.finflow.application.usecase.supplier;

import com.finflow.application.dto.supplier.SupplierRequest;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.supplier.Supplier;
import com.finflow.domain.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateSupplierUseCase {

    private final SupplierRepository supplierRepository;

    public UpdateSupplierUseCase(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Supplier execute(UUID id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));

        supplier.update(request.name(), request.category(), request.contactName(),
                request.email(), request.paymentTermDays(), request.currency());

        return supplier;
    }
}
