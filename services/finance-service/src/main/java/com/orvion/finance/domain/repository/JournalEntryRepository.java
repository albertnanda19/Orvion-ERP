package com.orvion.finance.domain.repository;

import com.orvion.finance.domain.model.JournalEntry;
import com.orvion.finance.domain.model.enums.JournalEntryStatus;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JournalEntryRepository {

    JournalEntry save(JournalEntry entry);

    Optional<JournalEntry> findById(UUID id);

    List<JournalEntry> findByTenantIdAndPeriod(String tenantId, int year, int month);

    List<JournalEntry> findByTenantIdAndStatus(String tenantId, JournalEntryStatus status);

    Optional<JournalEntry> findByReference(String reference);

    List<JournalEntry> findAllByTenantIdOrderByEntryDateDesc(String tenantId, Pageable pageable);

    long countByTenantId(String tenantId);
}
