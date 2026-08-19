package com.finflow.web.controller;

import com.finflow.application.dto.product.ProductRequest;
import com.finflow.application.dto.product.ProductResponse;
import com.finflow.application.mapper.ProductMapper;
import com.finflow.application.usecase.product.CreateProductUseCase;
import com.finflow.application.usecase.product.GetProductUseCase;
import com.finflow.application.usecase.product.ListProductsUseCase;
import com.finflow.application.usecase.product.UpdateProductUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final ProductMapper mapper;

    public ProductController(CreateProductUseCase createProductUseCase,
                              UpdateProductUseCase updateProductUseCase,
                              GetProductUseCase getProductUseCase,
                              ListProductsUseCase listProductsUseCase,
                              ProductMapper mapper) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        var product = createProductUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(product));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> list(@RequestParam(required = false) String name,
                                                        Pageable pageable) {
        return ResponseEntity.ok(listProductsUseCase.execute(name, pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getProductUseCase.execute(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(mapper.toResponse(updateProductUseCase.execute(id, request)));
    }
}
