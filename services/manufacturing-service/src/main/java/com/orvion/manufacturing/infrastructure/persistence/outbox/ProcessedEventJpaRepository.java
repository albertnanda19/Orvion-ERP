package com.orvion.manufacturing.infrastructure.persistence.outbox;

import com.orvion.manufacturing.domain.repository.ProcessedEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, UUID>, ProcessedEventRepository {
}
