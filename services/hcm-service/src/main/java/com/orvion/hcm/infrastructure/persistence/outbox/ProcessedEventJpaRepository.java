package com.orvion.hcm.infrastructure.persistence.outbox;

import com.orvion.hcm.domain.repository.ProcessedEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, UUID>, ProcessedEventRepository {
}
