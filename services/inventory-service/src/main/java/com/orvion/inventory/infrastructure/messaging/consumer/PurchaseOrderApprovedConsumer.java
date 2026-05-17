package com.orvion.inventory.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.inventory.domain.model.PurchaseOrder;
import com.orvion.inventory.domain.repository.PurchaseOrderRepository;
import com.orvion.inventory.domain.repository.ProcessedEventRepository;
import com.orvion.inventory.infrastructure.persistence.outbox.ProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

@Component
public class PurchaseOrderApprovedConsumer {
    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderApprovedConsumer.class);
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public PurchaseOrderApprovedConsumer(PurchaseOrderRepository purchaseOrderRepository,
                                          ProcessedEventRepository processedEventRepository,
                                          ObjectMapper mapper) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{poApprovedQueue.name}")
    @Transactional
    public void handlePurchaseOrderApproved(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) return;
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("Event {} already processed, skipping", eventId);
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.get("data");
            if (data == null) data = event;
            String poNumber = (String) data.get("poNumber");
            if (poNumber != null) {
                String tenantId = (String) data.getOrDefault("tenantId", "");
                PurchaseOrder po = purchaseOrderRepository.findByTenantIdAndPoNumber(tenantId, poNumber).orElse(null);
                if (po != null) {
                    po.approve((String) data.get("approvedBy"));
                    purchaseOrderRepository.save(po);
                    log.info("Updated local PO {} to APPROVED via cross-service event", poNumber);
                }
            }
            processedEventRepository.save(new ProcessedEvent(eventUUID, "PURCHASE_ORDER_APPROVED"));
        } catch (Exception e) {
            log.error("Error processing PO approved event: {}", e.getMessage());
            throw new RuntimeException("Failed to process PO approved event: " + e.getMessage());
        }
    }
}
