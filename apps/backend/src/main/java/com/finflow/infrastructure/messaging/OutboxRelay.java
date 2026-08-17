package com.finflow.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ADR-0002 §4: poller que lê a outbox e publica no Kafka. Entrega
 * at-least-once -- se cair entre publicar e marcar published_at, a próxima
 * passada publica de novo (consumidores devem ser idempotentes, ADR-0002 §5).
 *
 * Baseline in-app; caminho de upgrade documentado no ADR é trocar por
 * Debezium (CDC) sem mudar o schema da outbox.
 */
@Slf4j
@Component
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final int batchSize;
    private final int maxAttempts;

    public OutboxRelay(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${finflow.outbox.relay-batch-size}") int batchSize,
            @Value("${finflow.outbox.max-attempts}") int maxAttempts) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.batchSize = batchSize;
        this.maxAttempts = maxAttempts;
    }

    @Scheduled(fixedDelayString = "${finflow.outbox.relay-fixed-delay-ms}")
    @Transactional
    public void relay() {
        List<OutboxEvent> batch = outboxRepository.lockUnpublishedBatch(batchSize, maxAttempts);
        for (OutboxEvent event : batch) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {
        try {
            // chave = aggregateId -> eventos do mesmo agregado caem na mesma partição (ordem preservada)
            kafkaTemplate.send(event.getEventType(), event.getAggregateId().toString(), event.getPayload()).get();
            event.markPublished();
        } catch (Exception e) {
            event.incrementAttempts();
            log.warn("Falha ao publicar evento outbox id={} type={} attempts={}",
                    event.getId(), event.getEventType(), event.getAttempts(), e);
        }
    }
}
