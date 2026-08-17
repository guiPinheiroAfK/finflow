package com.finflow.domain.repository;

import com.finflow.domain.model.receivable.Receivable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReceivableRepository extends JpaRepository<Receivable, UUID> {
    List<Receivable> findByOrderId(UUID orderId);
}
