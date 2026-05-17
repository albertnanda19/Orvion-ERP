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
public class SalesEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(SalesEventConsumer.class);
    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public SalesEventConsumer(NotificationService notificationService,
                               ProcessedEventRepository processedEventRepository,
                               ObjectMapper mapper) {
        this.notificationService = notificationService;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{orderConfirmedQueue.name}")
    @Transactional
    public void handleOrderConfirmed(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) {
                log.warn("Received order confirmed event without eventId, skipping");
                return;
            }
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("Order confirmed event {} already processed, skipping", eventId);
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.getOrDefault("data", event);
            String tenantId = (String) data.getOrDefault("tenantId", "default");
            String recipientEmail = (String) data.getOrDefault("recipientEmail", "");
            String recipientId = (String) data.getOrDefault("recipientId", "");

            Map<String, Object> templateData = new HashMap<>(data);

            notificationService.sendEmailNotification(tenantId, recipientEmail, recipientId,
                    "orvion.sales.order.confirmed", eventId, templateData);

            processedEventRepository.save(new ProcessedEvent(eventUUID, "orvion.sales.order.confirmed"));
            log.info("Processed order confirmed event {}", eventId);
        } catch (Exception e) {
            log.error("Error processing order confirmed event: {}", e.getMessage());
            throw new RuntimeException("Failed to process order confirmed event: " + e.getMessage(), e);
        }
    }
}
