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
public class MfgEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(MfgEventConsumer.class);
    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public MfgEventConsumer(NotificationService notificationService,
                             ProcessedEventRepository processedEventRepository,
                             ObjectMapper mapper) {
        this.notificationService = notificationService;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{workOrderCompletedQueue.name}")
    @Transactional
    public void handleWorkOrderCompleted(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) {
                log.warn("Received work order completed event without eventId, skipping");
                return;
            }
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("Work order completed event {} already processed, skipping", eventId);
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.getOrDefault("data", event);
            String tenantId = (String) data.getOrDefault("tenantId", "default");
            String recipientEmail = (String) data.getOrDefault("recipientEmail", "");
            String recipientId = (String) data.getOrDefault("recipientId", "");

            Map<String, Object> templateData = new HashMap<>(data);

            notificationService.sendEmailNotification(tenantId, recipientEmail, recipientId,
                    "orvion.manufacturing.work.order.completed", eventId, templateData);

            processedEventRepository.save(new ProcessedEvent(eventUUID, "orvion.manufacturing.work.order.completed"));
            log.info("Processed work order completed event {}", eventId);
        } catch (Exception e) {
            log.error("Error processing work order completed event: {}", e.getMessage());
            throw new RuntimeException("Failed to process work order completed event: " + e.getMessage(), e);
        }
    }
}
