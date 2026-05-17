package com.orvion.notification.domain.repository;

import com.orvion.notification.domain.model.ProcessedEvent;
import java.util.Optional;
import java.util.UUID;

public interface ProcessedEventRepository {
    ProcessedEvent save(ProcessedEvent event);
    Optional<ProcessedEvent> findByEventId(UUID eventId);
    boolean existsByEventId(UUID eventId);
}
