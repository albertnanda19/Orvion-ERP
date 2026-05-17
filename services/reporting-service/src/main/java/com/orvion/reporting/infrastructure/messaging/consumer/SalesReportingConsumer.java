package com.orvion.reporting.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.reporting.domain.model.ReportSalesFact;
import com.orvion.reporting.domain.repository.ProcessedEventRepository;
import com.orvion.reporting.domain.repository.ReportSalesFactRepository;
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
public class SalesReportingConsumer {
    private static final Logger log = LoggerFactory.getLogger(SalesReportingConsumer.class);
    private final ReportSalesFactRepository salesFactRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public SalesReportingConsumer(ReportSalesFactRepository salesFactRepository,
                                   ProcessedEventRepository processedEventRepository,
                                   ObjectMapper mapper) {
        this.salesFactRepository = salesFactRepository;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{salesFactsQueue.name}")
    @Transactional
    public void handleSalesFact(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) return;
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("Sales event {} already processed, skipping", eventId);
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.get("data");
            if (data == null) data = event;

            ReportSalesFact fact = new ReportSalesFact();
            fact.setId(UUID.randomUUID());
            fact.setTenantId((String) data.getOrDefault("tenantId", ""));
            fact.setPeriod((String) data.getOrDefault("period", ""));
            fact.setTotalOrders(data.get("totalOrders") != null ? Long.valueOf(data.get("totalOrders").toString()) : 0L);
            fact.setTotalRevenue(new BigDecimal(data.getOrDefault("totalRevenue", "0").toString()));
            fact.setConversionRate(new BigDecimal(data.getOrDefault("conversionRate", "0").toString()));
            fact.setAvgOrderValue(new BigDecimal(data.getOrDefault("avgOrderValue", "0").toString()));
            salesFactRepository.save(fact);
            processedEventRepository.save(new ProcessedEvent(eventUUID, "SALES_FACT"));
            log.info("Processed sales fact for period {}", fact.getPeriod());
        } catch (Exception e) {
            log.error("Error processing sales fact: {}", e.getMessage());
            throw new RuntimeException("Failed to process sales fact: " + e.getMessage());
        }
    }
}
