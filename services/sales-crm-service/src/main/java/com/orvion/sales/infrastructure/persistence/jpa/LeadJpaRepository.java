package com.orvion.sales.infrastructure.persistence.jpa;

import com.orvion.sales.domain.model.Lead;
import com.orvion.sales.domain.repository.LeadRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeadJpaRepository extends JpaRepository<Lead, UUID>, LeadRepository {
    @Override
    List<Lead> findAllByTenantId(String tenantId);

    @Override
    List<Lead> findByTenantIdAndStatus(String tenantId, String status);

    @Override
    List<Lead> findByTenantIdAndAssignedTo(String tenantId, String assignedTo);

    @Override
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
}
