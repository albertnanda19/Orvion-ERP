package com.orvion.hcm.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.hcm.domain.model.PayrollRecord;
import com.orvion.hcm.domain.repository.PayrollRecordRepository;
import com.orvion.hcm.domain.repository.ProcessedEventRepository;
import com.orvion.hcm.infrastructure.persistence.outbox.ProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class PaymentProcessedConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentProcessedConsumer.class);
    private final PayrollRecordRepository payrollRecordRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper;

    public PaymentProcessedConsumer(PayrollRecordRepository payrollRecordRepository,
                                     ProcessedEventRepository processedEventRepository,
                                     ObjectMapper mapper) {
        this.payrollRecordRepository = payrollRecordRepository;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "#{financePaymentProcessedQueue.name}")
    @Transactional
    public void handlePaymentProcessed(String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = mapper.readValue(message, Map.class);
            String eventId = (String) event.get("eventId");
            if (eventId == null || eventId.isEmpty()) {
                log.warn("Received payment processed event without eventId, skipping");
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

            String payrollIdStr = (String) payload.getOrDefault("payrollId", "");
            if (!payrollIdStr.isEmpty()) {
                PayrollRecord record = payrollRecordRepository.findById(UUID.fromString(payrollIdStr)).orElse(null);
                if (record != null) {
                    record.markPaid();
                    payrollRecordRepository.save(record);
                    log.info("Marked payroll {} as paid from finance payment event", payrollIdStr);
                }
            }

            processedEventRepository.save(new ProcessedEvent(eventUUID, "FINANCE_PAYMENT_PROCESSED"));
            log.info("Successfully processed finance payment event: {}", eventId);
        } catch (Exception e) {
            log.error("Error processing finance payment event, rejecting: {}", e.getMessage());
            throw new RuntimeException("Failed to process finance payment event: " + e.getMessage());
        }
    }
}
