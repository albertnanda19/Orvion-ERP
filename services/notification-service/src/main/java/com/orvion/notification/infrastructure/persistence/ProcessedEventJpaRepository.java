package com.orvion.notification.infrastructure.persistence;

import com.orvion.notification.domain.model.ProcessedEvent;
import com.orvion.notification.domain.repository.ProcessedEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, UUID>, ProcessedEventRepository {
}
