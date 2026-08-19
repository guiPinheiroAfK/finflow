package com.finflow.domain.repository;

import com.finflow.domain.model.receivable.Receivable;
import com.finflow.domain.model.receivable.ReceivableStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReceivableRepository extends JpaRepository<Receivable, UUID> {
    List<Receivable> findByOrderId(UUID orderId);

    Page<Receivable> findByStatus(ReceivableStatus status, Pageable pageable);

    /** ADR-0004 §1: candidatos de conciliação -- em aberto, dentro da janela de busca de datas. */
    List<Receivable> findByStatusInAndDueDateBetween(
            List<ReceivableStatus> statuses, LocalDate from, LocalDate to);
}
