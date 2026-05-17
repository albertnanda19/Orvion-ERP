package com.orvion.finance.infrastructure.persistence.outbox;

import com.orvion.finance.domain.repository.OutboxEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEvent, UUID>, OutboxEventRepository {

    @Override
    @Query("SELECT e FROM OutboxEvent e WHERE e.published = false ORDER BY e.createdAt ASC")
    List<OutboxEvent> findUnpublished();

    @Override
    @Transactional
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.published = true, e.publishedAt = :publishedAt WHERE e.id = :id")
    void markAsPublished(@Param("id") UUID eventId);

    @Override
    @Transactional
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.retryCount = e.retryCount + 1, e.errorMessage = :errorMsg WHERE e.id = :id")
    void incrementRetryCount(@Param("id") UUID eventId, @Param("errorMsg") String errorMessage);
}
