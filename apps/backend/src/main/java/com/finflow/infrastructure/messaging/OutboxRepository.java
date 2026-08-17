package com.finflow.infrastructure.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * ADR-0002 §4: SKIP LOCKED permite escalar o relay horizontalmente sem
     * publicar o mesmo evento duas vezes a partir de instâncias concorrentes.
     */
    @Query(value = """
            SELECT * FROM outbox
            WHERE published_at IS NULL AND attempts < :maxAttempts
            ORDER BY occurred_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> lockUnpublishedBatch(@Param("limit") int limit, @Param("maxAttempts") int maxAttempts);
}
