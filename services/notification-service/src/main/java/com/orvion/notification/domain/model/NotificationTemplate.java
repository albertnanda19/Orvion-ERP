package com.orvion.notification.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.notification.domain.model.enums.NotificationChannel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "notification_templates", indexes = {
    @Index(name = "idx_nt_tpl_tenant_code", columnList = "tenantId, templateCode", unique = true),
    @Index(name = "idx_nt_tpl_event", columnList = "eventType"),
    @Index(name = "idx_nt_tpl_active", columnList = "active")
})
@Getter @Setter @NoArgsConstructor
public class NotificationTemplate extends Auditable {

    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 100, nullable = false)
    private String templateCode;

    @Column(length = 500, nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private NotificationChannel channel;

    @Column(length = 100, nullable = false)
    private String eventType;

    @Column(length = 10, nullable = false)
    private String language = "en";

    @Column(nullable = false)
    private boolean active = true;

    public NotificationTemplate(String tenantId, String templateCode, String subject, String body,
                                NotificationChannel channel, String eventType, String language) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.templateCode = templateCode;
        this.subject = subject;
        this.body = body;
        this.channel = channel;
        this.eventType = eventType;
        this.language = language;
        this.active = true;
    }
}
