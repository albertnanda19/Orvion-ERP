package com.orvion.notification.infrastructure.persistence;

import com.orvion.notification.domain.model.NotificationTemplate;
import com.orvion.notification.domain.model.enums.NotificationChannel;
import com.orvion.notification.domain.repository.NotificationTemplateRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationTemplateJpaRepository
        extends JpaRepository<NotificationTemplate, UUID>, NotificationTemplateRepository {

    List<NotificationTemplate> findByTenantId(String tenantId);

    Optional<NotificationTemplate> findByTenantIdAndTemplateCode(String tenantId, String templateCode);

    Optional<NotificationTemplate> findByTenantIdAndEventTypeAndLanguageAndChannel(
            String tenantId, String eventType, String language, NotificationChannel channel);

    List<NotificationTemplate> findByEventType(String eventType);

    boolean existsByTenantIdAndTemplateCode(String tenantId, String templateCode);
}
