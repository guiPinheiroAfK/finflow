package com.finflow.application.usecase.customer;

import com.finflow.application.dto.customer.CustomerRequest;
import com.finflow.application.exception.DocumentAlreadyRegisteredException;
import com.finflow.application.mapper.CustomerMapper;
import com.finflow.domain.model.customer.Customer;
import com.finflow.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    public CreateCustomerUseCase(CustomerRepository customerRepository, CustomerMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Customer execute(CustomerRequest request) {
        if (customerRepository.existsByDocument(request.document())) {
            throw new DocumentAlreadyRegisteredException(request.document());
        }
        Customer customer = Customer.create(
                request.type(),
                request.name(),
                request.document(),
                request.email(),
                request.phone(),
                mapper.toEntity(request.address()),
                request.tags() == null ? java.util.List.of() : request.tags());
        return customerRepository.save(customer);
    }
}
