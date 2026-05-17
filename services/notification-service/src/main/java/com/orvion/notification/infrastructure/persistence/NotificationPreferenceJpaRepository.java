package com.orvion.notification.infrastructure.persistence;

import com.orvion.notification.domain.model.NotificationPreference;
import com.orvion.notification.domain.repository.NotificationPreferenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationPreferenceJpaRepository
        extends JpaRepository<NotificationPreference, UUID>, NotificationPreferenceRepository {

    List<NotificationPreference> findByUserId(String userId);

    List<NotificationPreference> findByTenantId(String tenantId);

    Optional<NotificationPreference> findByUserIdAndEventType(String userId, String eventType);
}
