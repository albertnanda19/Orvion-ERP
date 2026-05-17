package com.orvion.hcm.infrastructure.persistence.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
@Getter @NoArgsConstructor
public class ProcessedEvent {
    @Id
    private UUID eventId;

    @Column(length = 100, nullable = false)
    private String eventType;

    @Column(nullable = false)
    private Instant processedAt;

    public ProcessedEvent(UUID eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = Instant.now();
    }
}
