package com.orvion.reporting.domain.repository;

import com.orvion.reporting.infrastructure.persistence.outbox.ProcessedEvent;
import java.util.Optional;
import java.util.UUID;

public interface ProcessedEventRepository {
    ProcessedEvent save(ProcessedEvent event);
    Optional<ProcessedEvent> findByEventId(UUID eventId);
    boolean existsByEventId(UUID eventId);
}
