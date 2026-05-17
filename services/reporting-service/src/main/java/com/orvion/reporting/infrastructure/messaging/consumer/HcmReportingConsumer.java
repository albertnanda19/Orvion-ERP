package com.orvion.reporting.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.reporting.domain.model.ReportHcmFact;
import com.orvion.reporting.domain.repository.ProcessedEventRepository;
import com.orvion.reporting.domain.repository.ReportHcmFactRepository;
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
public class HcmReportingConsumer {
    private static final Logger log = LoggerFactory.getLogger(HcmReportingConsumer.class);
    private final ReportHcmFactRepository hcmFactRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public HcmReportingConsumer(ReportHcmFactRepository hcmFactRepository,
                                 ProcessedEventRepository processedEventRepository,
                                 ObjectMapper mapper) {
        this.hcmFactRepository = hcmFactRepository;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{hcmFactsQueue.name}")
    @Transactional
    public void handleHcmFact(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null) return;
            UUID eventUUID = UUID.fromString(eventId);
            if (processedEventRepository.existsByEventId(eventUUID)) {
                log.info("HCM event {} already processed, skipping", eventId);
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) event.get("data");
            if (data == null) data = event;

            ReportHcmFact fact = new ReportHcmFact();
            fact.setId(UUID.randomUUID());
            fact.setTenantId((String) data.getOrDefault("tenantId", ""));
            fact.setPeriod((String) data.getOrDefault("period", ""));
            fact.setTotalEmployees(data.get("totalEmployees") != null ? Long.valueOf(data.get("totalEmployees").toString()) : 0L);
            fact.setTotalPayroll(new BigDecimal(data.getOrDefault("totalPayroll", "0").toString()));
            fact.setDeptCounts(data.get("deptCounts") != null ? data.get("deptCounts").toString() : "{}");
            hcmFactRepository.save(fact);
            processedEventRepository.save(new ProcessedEvent(eventUUID, "HCM_FACT"));
            log.info("Processed HCM fact for period {}", fact.getPeriod());
        } catch (Exception e) {
            log.error("Error processing HCM fact: {}", e.getMessage());
            throw new RuntimeException("Failed to process HCM fact: " + e.getMessage());
        }
    }
}
