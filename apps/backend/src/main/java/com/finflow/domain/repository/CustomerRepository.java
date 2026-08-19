package com.finflow.domain.repository;

import com.finflow.domain.model.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByDocument(String document);
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
