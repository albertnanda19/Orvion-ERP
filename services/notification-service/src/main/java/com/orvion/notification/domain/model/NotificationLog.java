package com.orvion.notification.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.notification.domain.model.enums.NotificationChannel;
import com.orvion.notification.domain.model.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_logs", indexes = {
    @Index(name = "idx_nl_tenant_recipient", columnList = "tenantId, recipientId"),
    @Index(name = "idx_nl_tenant_event", columnList = "tenantId, eventId"),
    @Index(name = "idx_nl_status", columnList = "status"),
    @Index(name = "idx_nl_sent_at", columnList = "sentAt")
})
@Getter @Setter @NoArgsConstructor
public class NotificationLog extends Auditable {

    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 100)
    private String recipientId;

    @Column(length = 255)
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private NotificationChannel channel;

    @Column(length = 500)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private NotificationStatus status;

    @Column(length = 100)
    private String eventId;

    @Column(length = 100)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Instant sentAt;

    public NotificationLog(String tenantId, String recipientId, String recipientEmail,
                           NotificationChannel channel, String subject, String body,
                           NotificationStatus status, String eventId, String eventType) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.recipientId = recipientId;
        this.recipientEmail = recipientEmail;
        this.channel = channel;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.eventId = eventId;
        this.eventType = eventType;
    }
}
