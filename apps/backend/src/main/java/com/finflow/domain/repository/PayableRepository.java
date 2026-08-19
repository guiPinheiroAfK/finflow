package com.finflow.domain.repository;

import com.finflow.domain.model.payable.Payable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PayableRepository extends JpaRepository<Payable, UUID> {
    List<Payable> findByOrderId(UUID orderId);
}
