package com.finflow.application.usecase.product;

import com.finflow.application.dto.product.ProductRequest;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.product.Product;
import com.finflow.domain.model.supplier.Supplier;
import com.finflow.domain.repository.ProductRepository;
import com.finflow.domain.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public UpdateProductUseCase(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Product execute(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", id));

        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", request.supplierId()));

        product.update(request.name(), request.category(), supplier,
                request.costPrice(), request.currency(), request.salePrice());

        return product;
    }
}
