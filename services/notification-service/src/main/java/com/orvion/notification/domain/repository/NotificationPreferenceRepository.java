package com.orvion.notification.domain.repository;

import com.orvion.notification.domain.model.NotificationPreference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository {
    NotificationPreference save(NotificationPreference preference);
    Optional<NotificationPreference> findById(UUID id);
    List<NotificationPreference> findByUserId(String userId);
    List<NotificationPreference> findByTenantId(String tenantId);
    Optional<NotificationPreference> findByUserIdAndEventType(String userId, String eventType);
    void deleteById(UUID id);
}
