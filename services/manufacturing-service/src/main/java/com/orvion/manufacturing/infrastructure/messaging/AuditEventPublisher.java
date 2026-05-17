package com.orvion.manufacturing.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AuditEventPublisher.class);
    private static final String REPORTING_EXCHANGE = "orvion.reporting.exchange";
    private static final String AUDIT_ROUTING_KEY = "orvion.audit.log";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;

    public AuditEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper mapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = mapper;
    }

    public void publishAuditEvent(String tenantId, String userId, String action, String details, String serviceName) {
        try {
            String eventId = UUID.randomUUID().toString();
            Map<String, Object> data = new HashMap<>();
            data.put("tenantId", tenantId != null ? tenantId : "");
            data.put("userId", userId != null ? userId : "anonymous");
            data.put("action", action);
            data.put("details", details != null ? details : "");
            data.put("serviceName", serviceName);
            data.put("traceId", UUID.randomUUID().toString());
            data.put("timestamp", Instant.now().toString());

            Map<String, Object> event = new HashMap<>();
            event.put("eventId", eventId);
            event.put("eventType", "AUDIT_LOG");
            event.put("occurredAt", Instant.now().toString());
            event.put("data", data);

            String payload = mapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(REPORTING_EXCHANGE, AUDIT_ROUTING_KEY, payload);
            log.debug("Published audit event: {} - {}", action, eventId);
        } catch (Exception e) {
            log.error("Failed to publish audit event: {}", e.getMessage());
        }
    }
}
