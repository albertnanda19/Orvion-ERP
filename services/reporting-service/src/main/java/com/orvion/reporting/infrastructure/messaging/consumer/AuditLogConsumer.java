package com.orvion.reporting.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.reporting.domain.model.ReportAuditLog;
import com.orvion.reporting.domain.repository.ProcessedEventRepository;
import com.orvion.reporting.domain.repository.ReportAuditLogRepository;
import com.orvion.reporting.infrastructure.elasticsearch.ElasticsearchService;
import com.orvion.reporting.infrastructure.persistence.outbox.ProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditLogConsumer {
    private static final Logger log = LoggerFactory.getLogger(AuditLogConsumer.class);
    private final ReportAuditLogRepository auditLogRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ElasticsearchService elasticsearchService;
    private final ObjectMapper mapper;

    public AuditLogConsumer(ReportAuditLogRepository auditLogRepository,
                             ProcessedEventRepository processedEventRepository,
                             ElasticsearchService elasticsearchService,
                             ObjectMapper mapper) {
        this.auditLogRepository = auditLogRepository;
        this.processedEventRepository = processedEventRepository;
        this.elasticsearchService = elasticsearchService;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{auditLogQueue.name}")
    @Transactional
    public void handleAuditLog(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) return;
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("Audit log event {} already processed, skipping", eventId);
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.get("data");
            if (data == null) data = event;

            ReportAuditLog auditLog = new ReportAuditLog();
            auditLog.setId(UUID.randomUUID());
            auditLog.setTenantId((String) data.getOrDefault("tenantId", ""));
            auditLog.setAction((String) data.getOrDefault("action", ""));
            auditLog.setDetails((String) data.getOrDefault("details", ""));
            auditLog.setUserId((String) data.getOrDefault("userId", ""));
            auditLog.setServiceName((String) data.getOrDefault("serviceName", ""));
            auditLog.setTraceId((String) data.getOrDefault("traceId", ""));
            auditLog.setTimestamp(data.get("timestamp") != null ? Instant.parse(data.get("timestamp").toString()) : Instant.now());
            auditLogRepository.save(auditLog);

            Map<String, Object> esDoc = new HashMap<>();
            esDoc.put("tenantId", auditLog.getTenantId());
            esDoc.put("action", auditLog.getAction());
            esDoc.put("details", auditLog.getDetails());
            esDoc.put("userId", auditLog.getUserId());
            esDoc.put("serviceName", auditLog.getServiceName());
            esDoc.put("traceId", auditLog.getTraceId());
            esDoc.put("timestamp", auditLog.getTimestamp().toString());
            elasticsearchService.indexAuditLog(eventId, esDoc);

            processedEventRepository.save(new ProcessedEvent(eventUUID, "AUDIT_LOG"));
            log.info("Processed audit log: {} (indexed to ES)", auditLog.getAction());
        } catch (Exception e) {
            log.error("Error processing audit log: {}", e.getMessage());
            throw new RuntimeException("Failed to process audit log: " + e.getMessage());
        }
    }
}
