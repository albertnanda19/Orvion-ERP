package com.orvion.sales.domain.repository;

import com.orvion.sales.domain.model.Opportunity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OpportunityRepository {
    Opportunity save(Opportunity opportunity);
    Optional<Opportunity> findById(UUID id);
    List<Opportunity> findAllByTenantId(String tenantId);
    List<Opportunity> findByTenantIdAndStage(String tenantId, String stage);
    List<Opportunity> findByTenantIdAndAssignedTo(String tenantId, String assignedTo);
    long countByTenantId(String tenantId);
    long countByTenantIdAndStage(String tenantId, String stage);
    void delete(Opportunity opportunity);
}
