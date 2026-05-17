package com.orvion.inventory.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.inventory.domain.model.Product;
import com.orvion.inventory.domain.repository.ProductRepository;
import com.orvion.inventory.domain.repository.ProcessedEventRepository;
import com.orvion.inventory.infrastructure.persistence.outbox.ProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class SalesOrderConfirmedConsumer {
    private static final Logger log = LoggerFactory.getLogger(SalesOrderConfirmedConsumer.class);
    private final ProductRepository productRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public SalesOrderConfirmedConsumer(ProductRepository productRepository,
                                        ProcessedEventRepository processedEventRepository,
                                        ObjectMapper mapper) {
        this.productRepository = productRepository;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{salesOrderConfirmedQueue.name}")
    @Transactional
    public void handleOrderConfirmed(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null || eventId.isEmpty()) {
                log.warn("Received order confirmed event without eventId, skipping");
                return;
            }
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("Event {} already processed, skipping", eventId);
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) event.get("data");
            if (payload == null) payload = event;
            String tenantId = (String) payload.getOrDefault("tenantId", "");
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> items = (java.util.List<Map<String, Object>>) payload.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    String productId = (String) item.get("productId");
                    String quantity = (String) item.get("quantity");
                    if (productId != null && quantity != null) {
                        Product product = productRepository.findById(UUID.fromString(productId)).orElse(null);
                        if (product != null) {
                            product.reserveStock(new BigDecimal(quantity), "SALES_ORDER_" + eventId);
                            productRepository.save(product);
                            log.info("Reserved {} of product {} for order event {}", quantity, productId, eventId);
                        }
                    }
                }
            }
            processedEventRepository.save(new ProcessedEvent(eventUUID, "SALES_ORDER_CONFIRMED"));
            log.info("Successfully processed order confirmed event: {}", eventId);
        } catch (Exception e) {
            log.error("Business error processing order confirmed event, rejecting: {}", e.getMessage());
            throw new RuntimeException("Failed to process order confirmed event: " + e.getMessage());
        }
    }
}
