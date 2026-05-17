package com.orvion.finance.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orvion.common.event.DomainEvent;
import com.orvion.finance.domain.event.InvoiceCreatedEvent;
import com.orvion.finance.domain.event.PaymentProcessedEvent;
import com.orvion.finance.infrastructure.persistence.outbox.OutboxEvent;
import com.orvion.finance.infrastructure.persistence.outbox.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FinanceEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(FinanceEventPublisher.class);
    private static final String EXCHANGE = "orvion.finance.exchange";

    private final RabbitTemplate rabbitTemplate;
    private final OutboxEventJpaRepository outboxRepository;
    private final ObjectMapper mapper;

    public FinanceEventPublisher(RabbitTemplate rabbitTemplate,
                                  OutboxEventJpaRepository outboxRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.outboxRepository = outboxRepository;
        this.mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    }

    public void publishInvoiceCreated(InvoiceCreatedEvent event) {
        try {
            String payload = mapper.writeValueAsString(event);
            String routingKey = "orvion.finance.invoice.created";
            saveToOutbox(event, payload);
            sendWithConfirm(event.getEventId().toString(), payload, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish InvoiceCreatedEvent: {}", e.getMessage(), e);
        }
    }

    public void publishPaymentProcessed(PaymentProcessedEvent event) {
        try {
            String payload = mapper.writeValueAsString(event);
            String routingKey = "orvion.finance.payment.processed";
            saveToOutbox(event, payload);
            sendWithConfirm(event.getEventId().toString(), payload, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish PaymentProcessedEvent: {}", e.getMessage(), e);
        }
    }

    public void publishEvent(String eventType, String aggregateType, String aggregateId,
                              String tenantId, String payload, String routingKey) {
        try {
            OutboxEvent outbox = new OutboxEvent(eventType, aggregateType, aggregateId, tenantId, payload);
            outboxRepository.save(outbox);
            sendWithConfirm(outbox.getId().toString(), payload, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish event {}: {}", eventType, e.getMessage(), e);
        }
    }

    private void saveToOutbox(DomainEvent event, String payload) {
        OutboxEvent outbox = new OutboxEvent(
            event.getEventType(),
            event.getAggregateType(),
            event.getAggregateId(),
            event.getTenantId(),
            payload
        );
        outboxRepository.save(outbox);
    }

    private void sendWithConfirm(String correlationId, String payload, String routingKey) {
        CorrelationData correlationData = new CorrelationData(correlationId);
        correlationData.getFuture().whenComplete((confirm, ex) -> {
            if (confirm != null && confirm.isAck()) {
                log.debug("Message confirmed for routingKey={}, id={}", routingKey, correlationId);
            } else {
                log.warn("Message NACK for routingKey={}, id={}, reason={}",
                    routingKey, correlationId,
                    confirm != null ? confirm.getReason() : "unknown");
            }
        });
        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, payload, correlationData);
    }
}
