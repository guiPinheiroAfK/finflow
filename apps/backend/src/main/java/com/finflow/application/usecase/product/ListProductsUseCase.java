package com.finflow.application.usecase.product;

import com.finflow.domain.model.product.Product;
import com.finflow.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListProductsUseCase {

    private final ProductRepository productRepository;

    public ListProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> execute(String nameFilter, Pageable pageable) {
        if (nameFilter == null || nameFilter.isBlank()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
    }
}
