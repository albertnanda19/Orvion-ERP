package com.orvion.notification.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.orvion.notification.domain.model.enums.NotificationChannel;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences", indexes = {
    @Index(name = "idx_np_user_event", columnList = "userId, eventType", unique = true),
    @Index(name = "idx_np_tenant", columnList = "tenantId")
})
@Getter @Setter @NoArgsConstructor
public class NotificationPreference {

    @Id
    private UUID id;

    @Column(length = 100, nullable = false)
    private String userId;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 100, nullable = false)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private NotificationChannel channel;

    @Column(nullable = false)
    private boolean enabled = true;

    public NotificationPreference(String userId, String tenantId, String eventType,
                                  NotificationChannel channel, boolean enabled) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.tenantId = tenantId;
        this.eventType = eventType;
        this.channel = channel;
        this.enabled = enabled;
    }
}
