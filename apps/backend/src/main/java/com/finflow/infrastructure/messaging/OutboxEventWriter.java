package com.finflow.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finflow.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * ADR-0002 §2: materializa o evento de domínio na outbox ANTES do commit --
 * mesma transação do estado de negócio que o originou. É o ponto que garante
 * atomicidade entre "mudou o estado" e "há intenção de publicar".
 */
@Slf4j
@Component
public class OutboxEventWriter {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventWriter(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onDomainEvent(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxRepository.save(OutboxEvent.of(
                    event.aggregateType(), event.aggregateId(), event.eventType(), payload));
        } catch (JsonProcessingException e) {
            // Falhar aqui reverte a transação inteira -- correto: melhor abortar
            // a operação de negócio do que perder o evento silenciosamente.
            throw new IllegalStateException("Falha ao serializar evento de domínio: " + event.eventType(), e);
        }
    }
}
