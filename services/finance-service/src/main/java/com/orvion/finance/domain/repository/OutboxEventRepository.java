package com.orvion.finance.domain.repository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository {

    List<com.orvion.finance.infrastructure.persistence.outbox.OutboxEvent> findUnpublished();

    void markAsPublished(UUID eventId);

    void incrementRetryCount(UUID eventId, String errorMessage);
}
