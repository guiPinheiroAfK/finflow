package com.finflow.domain.repository;

import com.finflow.domain.model.supplier.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
