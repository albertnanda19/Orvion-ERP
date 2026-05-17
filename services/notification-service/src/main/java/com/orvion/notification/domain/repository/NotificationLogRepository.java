package com.orvion.notification.domain.repository;

import com.orvion.notification.domain.model.NotificationLog;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationLogRepository {
    NotificationLog save(NotificationLog log);
    Optional<NotificationLog> findById(UUID id);
    List<NotificationLog> findByTenantId(String tenantId);
    List<NotificationLog> findByTenantIdAndRecipientId(String tenantId, String recipientId);
    List<NotificationLog> findByTenantIdAndRecipientIdAndStatus(String tenantId, String recipientId,
                                                                  com.orvion.notification.domain.model.enums.NotificationStatus status);
    long countByTenantIdAndRecipientIdAndStatus(String tenantId, String recipientId,
                                                 com.orvion.notification.domain.model.enums.NotificationStatus status);
    void deleteById(UUID id);
    List<NotificationLog> findByTenantIdAndRecipientIdAndCreatedAtAfter(String tenantId, String recipientId, Instant after);
}
