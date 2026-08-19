package com.finflow.application.usecase.customer;

import com.finflow.application.dto.customer.CustomerRequest;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.application.mapper.CustomerMapper;
import com.finflow.domain.model.customer.Customer;
import com.finflow.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    public UpdateCustomerUseCase(CustomerRepository customerRepository, CustomerMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Customer execute(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));

        customer.update(
                request.name(),
                request.email(),
                request.phone(),
                mapper.toEntity(request.address()),
                request.tags() == null ? List.of() : request.tags());

        return customer;
    }
}
