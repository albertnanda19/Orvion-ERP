package com.orvion.reporting.infrastructure.persistence.outbox;

import com.orvion.reporting.domain.repository.ProcessedEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, UUID>, ProcessedEventRepository {
}
