package com.finflow.web.controller;

import com.finflow.application.dto.customer.CustomerRequest;
import com.finflow.application.dto.customer.CustomerResponse;
import com.finflow.application.mapper.CustomerMapper;
import com.finflow.application.usecase.customer.CreateCustomerUseCase;
import com.finflow.application.usecase.customer.GetCustomerUseCase;
import com.finflow.application.usecase.customer.ListCustomersUseCase;
import com.finflow.application.usecase.customer.UpdateCustomerUseCase;
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
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final CustomerMapper mapper;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase,
                               UpdateCustomerUseCase updateCustomerUseCase,
                               GetCustomerUseCase getCustomerUseCase,
                               ListCustomersUseCase listCustomersUseCase,
                               CustomerMapper mapper) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        var customer = createCustomerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(customer));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> list(@RequestParam(required = false) String name,
                                                         Pageable pageable) {
        return ResponseEntity.ok(listCustomersUseCase.execute(name, pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getCustomerUseCase.execute(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(mapper.toResponse(updateCustomerUseCase.execute(id, request)));
    }
}
