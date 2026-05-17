package com.orvion.finance.infrastructure.persistence.repository;

import com.orvion.finance.domain.model.JournalEntry;
import com.orvion.finance.domain.model.enums.JournalEntryStatus;
import com.orvion.finance.domain.repository.JournalEntryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalEntryJpaRepository extends JpaRepository<JournalEntry, UUID>, JournalEntryRepository {

    @Override
    Optional<JournalEntry> findById(UUID id);

    @Override
    @Query("SELECT j FROM JournalEntry j WHERE j.tenantId = :tenantId AND j.year = :year AND j.month = :month")
    List<JournalEntry> findByTenantIdAndPeriod(@Param("tenantId") String tenantId,
                                               @Param("year") int year, @Param("month") int month);

    @Override
    @Query("SELECT j FROM JournalEntry j WHERE j.tenantId = :tenantId AND j.status = :status")
    List<JournalEntry> findByTenantIdAndStatus(@Param("tenantId") String tenantId,
                                               @Param("status") JournalEntryStatus status);

    @Override
    @Query("SELECT j FROM JournalEntry j WHERE j.reference = :ref")
    Optional<JournalEntry> findByReference(@Param("ref") String reference);

    @Override
    @Query("SELECT j FROM JournalEntry j WHERE j.tenantId = :tenantId ORDER BY j.entryDate DESC")
    List<JournalEntry> findAllByTenantIdOrderByEntryDateDesc(@Param("tenantId") String tenantId,
                                                               Pageable pageable);

    @Override
    @Query("SELECT COUNT(j) FROM JournalEntry j WHERE j.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
}
