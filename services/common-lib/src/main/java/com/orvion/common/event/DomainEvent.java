package com.orvion.common.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {

    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String tenantId;
    private final String aggregateId;
    private final String aggregateType;

    protected DomainEvent(String eventType, String tenantId, String aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID();
        this.eventType = eventType;
        this.occurredAt = Instant.now();
        this.tenantId = tenantId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }

    protected DomainEvent(String eventType, String tenantId, String aggregateId, String aggregateType, UUID eventId, Instant occurredAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.tenantId = tenantId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }
}
