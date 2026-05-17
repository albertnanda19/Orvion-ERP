package com.orvion.finance.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orvion.finance.infrastructure.persistence.outbox.OutboxEvent;
import com.orvion.finance.infrastructure.persistence.outbox.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPublisherScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPublisherScheduler.class);
    private static final String EXCHANGE = "orvion.finance.exchange";
    private static final int MAX_RETRIES = 5;

    private final OutboxEventJpaRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;

    public OutboxEventPublisherScheduler(OutboxEventJpaRepository outboxRepository,
                                          RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findUnpublished();

        for (OutboxEvent event : pendingEvents) {
            if (event.getRetryCount() >= MAX_RETRIES) {
                log.warn("Outbox event {} has exceeded max retries ({}). Skipping.", event.getId(), MAX_RETRIES);
                continue;
            }

            try {
                String routingKey = resolveRoutingKey(event.getEventType());
                CorrelationData correlationData = new CorrelationData(event.getId().toString());
                rabbitTemplate.convertAndSend(EXCHANGE, routingKey, event.getPayload(), correlationData);
                outboxRepository.markAsPublished(event.getId());
                log.info("Published outbox event {} (type={}) to exchange {} with routingKey={}",
                    event.getId(), event.getEventType(), EXCHANGE, routingKey);
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}: {}", event.getId(), e.getMessage());
                outboxRepository.incrementRetryCount(event.getId(), e.getMessage());
            }
        }
    }

    private String resolveRoutingKey(String eventType) {
        return switch (eventType) {
            case "INVOICE_CREATED" -> "orvion.finance.invoice.created";
            case "INVOICE_APPROVED" -> "orvion.finance.invoice.approved";
            case "PAYMENT_PROCESSED" -> "orvion.finance.payment.processed";
            case "JOURNAL_ENTRY_POSTED" -> "orvion.finance.journal.posted";
            case "BUDGET_EXCEEDED" -> "orvion.finance.budget.exceeded";
            default -> "orvion.finance.event.generic";
        };
    }
}
