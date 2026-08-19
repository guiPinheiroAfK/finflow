package com.finflow.domain.repository;

import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.model.quote.QuoteStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {

    /** ADR-0003 §2: SELECT ... FOR UPDATE -- fecha a janela de corrida entre leitura do status e commit. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from Quote q where q.id = :id")
    Optional<Quote> findByIdForUpdate(@Param("id") UUID id);

    Page<Quote> findByStatus(QuoteStatus status, Pageable pageable);

    Page<Quote> findByCustomerId(UUID customerId, Pageable pageable);

    long countByStatusIn(List<QuoteStatus> statuses);
}
