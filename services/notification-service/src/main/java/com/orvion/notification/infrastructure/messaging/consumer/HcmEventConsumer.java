package com.orvion.notification.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.notification.application.service.NotificationService;
import com.orvion.notification.domain.model.ProcessedEvent;
import com.orvion.notification.domain.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class HcmEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(HcmEventConsumer.class);
    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public HcmEventConsumer(NotificationService notificationService,
                             ProcessedEventRepository processedEventRepository,
                             ObjectMapper mapper) {
        this.notificationService = notificationService;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{employeeOnboardedQueue.name}")
    @Transactional
    public void handleEmployeeOnboarded(String message) {
        processEvent(message, "orvion.hcm.employee.onboarded");
    }

    @RabbitListener(queues = "#{payrollProcessedQueue.name}")
    @Transactional
    public void handlePayrollProcessed(String message) {
        processEvent(message, "orvion.hcm.payroll.processed");
    }

    private void processEvent(String message, String eventType) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) {
                log.warn("Received HCM event without eventId, skipping");
                return;
            }
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("HCM event {} already processed, skipping", eventId);
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.getOrDefault("data", event);
            String tenantId = (String) data.getOrDefault("tenantId", "default");
            String recipientEmail = (String) data.getOrDefault("recipientEmail", "");
            String recipientId = (String) data.getOrDefault("recipientId", "");

            Map<String, Object> templateData = new HashMap<>(data);

            notificationService.sendEmailNotification(tenantId, recipientEmail, recipientId,
                    eventType, eventId, templateData);

            processedEventRepository.save(new ProcessedEvent(eventUUID, eventType));
            log.info("Processed HCM event {} type={}", eventId, eventType);
        } catch (Exception e) {
            log.error("Error processing HCM event type={}: {}", eventType, e.getMessage());
            throw new RuntimeException("Failed to process HCM event: " + e.getMessage(), e);
        }
    }
}
