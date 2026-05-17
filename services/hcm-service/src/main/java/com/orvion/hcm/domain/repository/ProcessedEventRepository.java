package com.orvion.hcm.domain.repository;

import com.orvion.hcm.infrastructure.persistence.outbox.ProcessedEvent;
import java.util.Optional;
import java.util.UUID;

public interface ProcessedEventRepository {
    ProcessedEvent save(ProcessedEvent event);
    Optional<ProcessedEvent> findByEventId(UUID eventId);
    boolean existsByEventId(UUID eventId);
}
