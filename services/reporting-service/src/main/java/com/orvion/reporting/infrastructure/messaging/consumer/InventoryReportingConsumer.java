package com.orvion.reporting.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.reporting.domain.model.ReportInventoryFact;
import com.orvion.reporting.domain.repository.ProcessedEventRepository;
import com.orvion.reporting.domain.repository.ReportInventoryFactRepository;
import com.orvion.reporting.infrastructure.persistence.outbox.ProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class InventoryReportingConsumer {
    private static final Logger log = LoggerFactory.getLogger(InventoryReportingConsumer.class);
    private final ReportInventoryFactRepository inventoryFactRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public InventoryReportingConsumer(ReportInventoryFactRepository inventoryFactRepository,
                                       ProcessedEventRepository processedEventRepository,
                                       ObjectMapper mapper) {
        this.inventoryFactRepository = inventoryFactRepository;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{inventoryFactsQueue.name}")
    @Transactional
    public void handleInventoryFact(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) return;
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("Inventory event {} already processed, skipping", eventId);
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.get("data");
            if (data == null) data = event;

            ReportInventoryFact fact = new ReportInventoryFact();
            fact.setId(UUID.randomUUID());
            fact.setTenantId((String) data.getOrDefault("tenantId", ""));
            fact.setPeriod((String) data.getOrDefault("period", ""));
            fact.setTotalProducts(data.get("totalProducts") != null ? Long.valueOf(data.get("totalProducts").toString()) : 0L);
            fact.setTotalStockValue(new BigDecimal(data.getOrDefault("totalStockValue", "0").toString()));
            fact.setLowStockCount(data.get("lowStockCount") != null ? Long.valueOf(data.get("lowStockCount").toString()) : 0L);
            fact.setTurnoverRate(new BigDecimal(data.getOrDefault("turnoverRate", "0").toString()));
            inventoryFactRepository.save(fact);
            processedEventRepository.save(new ProcessedEvent(eventUUID, "INVENTORY_FACT"));
            log.info("Processed inventory fact for period {}", fact.getPeriod());
        } catch (Exception e) {
            log.error("Error processing inventory fact: {}", e.getMessage());
            throw new RuntimeException("Failed to process inventory fact: " + e.getMessage());
        }
    }
}
