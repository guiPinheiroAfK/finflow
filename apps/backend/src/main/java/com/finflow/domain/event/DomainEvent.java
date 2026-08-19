package com.finflow.domain.event;

import java.util.UUID;

/**
 * Marcador para eventos de domínio que devem virar mensagens Kafka via
 * outbox transacional (ADR-0002). O domínio publica via
 * {@link org.springframework.context.ApplicationEventPublisher}; a
 * infraestrutura de mensageria materializa na tabela outbox.
 */
public interface DomainEvent {
    String aggregateType();
    UUID aggregateId();
    /** Nome do tópico Kafka, ex.: "sales.order.confirmed". */
    String eventType();
}
