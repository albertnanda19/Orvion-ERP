package com.orvion.sales.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.sales.domain.model.enums.ActivityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter @Setter @NoArgsConstructor
public class Activity extends Auditable {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ActivityType type;

    private Instant scheduledAt;

    private Instant completedAt;

    @Column(columnDefinition = "TEXT")
    private String outcome;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    public Activity(ActivityType type, Instant scheduledAt, String notes) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.scheduledAt = scheduledAt;
        this.notes = notes;
    }

    public void complete(String outcome) {
        this.completedAt = Instant.now();
        this.outcome = outcome;
    }
}
