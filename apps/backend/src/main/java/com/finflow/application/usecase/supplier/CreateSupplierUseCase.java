package com.finflow.application.usecase.supplier;

import com.finflow.application.dto.supplier.SupplierRequest;
import com.finflow.domain.model.supplier.Supplier;
import com.finflow.domain.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSupplierUseCase {

    private final SupplierRepository supplierRepository;

    public CreateSupplierUseCase(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Supplier execute(SupplierRequest request) {
        Supplier supplier = Supplier.create(
                request.name(), request.category(), request.document(),
                request.contactName(), request.email(), request.paymentTermDays(), request.currency());
        return supplierRepository.save(supplier);
    }
}
