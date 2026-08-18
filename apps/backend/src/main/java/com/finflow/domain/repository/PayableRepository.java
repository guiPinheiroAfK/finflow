package com.finflow.domain.repository;

import com.finflow.domain.model.payable.Payable;
import com.finflow.domain.model.payable.PayableStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PayableRepository extends JpaRepository<Payable, UUID> {
    List<Payable> findByOrderId(UUID orderId);

    Page<Payable> findByStatus(PayableStatus status, Pageable pageable);

    /** ADR-0004 §1: candidatos de conciliação -- em aberto, dentro da janela de busca de datas. */
    List<Payable> findByStatusAndDueDateBetween(PayableStatus status, LocalDate from, LocalDate to);

    long countByStatus(PayableStatus status);

    @Query("select coalesce(sum(p.amountBrl), 0) from Payable p where p.status = :status")
    BigDecimal sumAmountBrlByStatus(PayableStatus status);
}
