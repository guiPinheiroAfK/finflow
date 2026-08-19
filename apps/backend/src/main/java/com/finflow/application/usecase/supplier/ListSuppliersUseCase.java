package com.finflow.application.usecase.supplier;

import com.finflow.domain.model.supplier.Supplier;
import com.finflow.domain.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListSuppliersUseCase {

    private final SupplierRepository supplierRepository;

    public ListSuppliersUseCase(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public Page<Supplier> execute(String nameFilter, Pageable pageable) {
        if (nameFilter == null || nameFilter.isBlank()) {
            return supplierRepository.findAll(pageable);
        }
        return supplierRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
    }
}
