package com.orvion.notification.domain.repository;

import com.orvion.notification.domain.model.NotificationTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationTemplateRepository {
    NotificationTemplate save(NotificationTemplate template);
    Optional<NotificationTemplate> findById(UUID id);
    List<NotificationTemplate> findByTenantId(String tenantId);
    Optional<NotificationTemplate> findByTenantIdAndTemplateCode(String tenantId, String templateCode);
    Optional<NotificationTemplate> findByTenantIdAndEventTypeAndLanguageAndChannel(
            String tenantId, String eventType, String language,
            com.orvion.notification.domain.model.enums.NotificationChannel channel);
    List<NotificationTemplate> findByEventType(String eventType);
    void deleteById(UUID id);
    boolean existsByTenantIdAndTemplateCode(String tenantId, String templateCode);
}
