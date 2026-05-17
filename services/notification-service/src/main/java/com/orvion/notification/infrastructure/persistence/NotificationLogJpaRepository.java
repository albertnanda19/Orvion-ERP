package com.orvion.notification.infrastructure.persistence;

import com.orvion.notification.domain.model.NotificationLog;
import com.orvion.notification.domain.model.enums.NotificationStatus;
import com.orvion.notification.domain.repository.NotificationLogRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogJpaRepository
        extends JpaRepository<NotificationLog, UUID>, NotificationLogRepository {

    List<NotificationLog> findByTenantId(String tenantId);

    List<NotificationLog> findByTenantIdAndRecipientId(String tenantId, String recipientId);

    List<NotificationLog> findByTenantIdAndRecipientIdAndStatus(String tenantId, String recipientId,
                                                                  NotificationStatus status);

    long countByTenantIdAndRecipientIdAndStatus(String tenantId, String recipientId, NotificationStatus status);

    List<NotificationLog> findByTenantIdAndRecipientIdAndCreatedAtAfter(String tenantId, String recipientId, Instant after);
}
