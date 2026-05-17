package com.orvion.reporting.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.reporting.domain.model.ReportFinanceFact;
import com.orvion.reporting.domain.repository.ProcessedEventRepository;
import com.orvion.reporting.domain.repository.ReportFinanceFactRepository;
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
public class FinanceReportingConsumer {
    private static final Logger log = LoggerFactory.getLogger(FinanceReportingConsumer.class);
    private final ReportFinanceFactRepository financeFactRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public FinanceReportingConsumer(ReportFinanceFactRepository financeFactRepository,
                                     ProcessedEventRepository processedEventRepository,
                                     ObjectMapper mapper) {
        this.financeFactRepository = financeFactRepository;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{financeFactsQueue.name}")
    @Transactional
    public void handleFinanceFact(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) return;
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("Finance event {} already processed, skipping", eventId);
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.get("data");
            if (data == null) data = event;

            ReportFinanceFact fact = new ReportFinanceFact();
            fact.setId(UUID.randomUUID());
            fact.setTenantId((String) data.getOrDefault("tenantId", ""));
            fact.setPeriod((String) data.getOrDefault("period", ""));
            fact.setRevenue(new BigDecimal(data.getOrDefault("revenue", "0").toString()));
            fact.setExpenses(new BigDecimal(data.getOrDefault("expenses", "0").toString()));
            fact.setNetProfit(new BigDecimal(data.getOrDefault("netProfit", "0").toString()));
            fact.setGrossMargin(new BigDecimal(data.getOrDefault("grossMargin", "0").toString()));
            fact.setInvoiceCount(data.get("invoiceCount") != null ? Long.valueOf(data.get("invoiceCount").toString()) : 0L);
            financeFactRepository.save(fact);
            processedEventRepository.save(new ProcessedEvent(eventUUID, "FINANCE_FACT"));
            log.info("Processed finance fact for period {}", fact.getPeriod());
        } catch (Exception e) {
            log.error("Error processing finance fact: {}", e.getMessage());
            throw new RuntimeException("Failed to process finance fact: " + e.getMessage());
        }
    }
}
