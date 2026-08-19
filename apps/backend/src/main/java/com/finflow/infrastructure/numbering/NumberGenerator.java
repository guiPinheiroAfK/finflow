package com.finflow.infrastructure.numbering;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.time.Year;

/**
 * ADR-0003: números de negócio (ORC-2026-000123, VND-2026-000045) gerados a
 * partir de sequences do Postgres -- garantia de unicidade sob concorrência
 * real, ao contrário de "count(*) + 1" em memória.
 */
@Component
public class NumberGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    public String nextQuoteNumber() {
        return format("ORC", nextValue("quote_number_seq"));
    }

    public String nextOrderNumber() {
        return format("VND", nextValue("order_number_seq"));
    }

    private long nextValue(String sequenceName) {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('%s')".formatted(sequenceName))
                .getSingleResult()).longValue();
    }

    private String format(String prefix, long value) {
        return "%s-%d-%06d".formatted(prefix, Year.now().getValue(), value);
    }
}
