package com.finflow.web.controller;

import com.finflow.application.dto.supplier.SupplierRequest;
import com.finflow.application.dto.supplier.SupplierResponse;
import com.finflow.application.mapper.SupplierMapper;
import com.finflow.application.usecase.supplier.CreateSupplierUseCase;
import com.finflow.application.usecase.supplier.GetSupplierUseCase;
import com.finflow.application.usecase.supplier.ListSuppliersUseCase;
import com.finflow.application.usecase.supplier.UpdateSupplierUseCase;
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
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final CreateSupplierUseCase createSupplierUseCase;
    private final UpdateSupplierUseCase updateSupplierUseCase;
    private final GetSupplierUseCase getSupplierUseCase;
    private final ListSuppliersUseCase listSuppliersUseCase;
    private final SupplierMapper mapper;

    public SupplierController(CreateSupplierUseCase createSupplierUseCase,
                               UpdateSupplierUseCase updateSupplierUseCase,
                               GetSupplierUseCase getSupplierUseCase,
                               ListSuppliersUseCase listSuppliersUseCase,
                               SupplierMapper mapper) {
        this.createSupplierUseCase = createSupplierUseCase;
        this.updateSupplierUseCase = updateSupplierUseCase;
        this.getSupplierUseCase = getSupplierUseCase;
        this.listSuppliersUseCase = listSuppliersUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        var supplier = createSupplierUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(supplier));
    }

    @GetMapping
    public ResponseEntity<Page<SupplierResponse>> list(@RequestParam(required = false) String name,
                                                         Pageable pageable) {
        return ResponseEntity.ok(listSuppliersUseCase.execute(name, pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getSupplierUseCase.execute(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> update(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(mapper.toResponse(updateSupplierUseCase.execute(id, request)));
    }
}
