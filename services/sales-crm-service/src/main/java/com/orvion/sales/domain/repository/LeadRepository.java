package com.orvion.sales.domain.repository;

import com.orvion.sales.domain.model.Lead;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository {
    Lead save(Lead lead);
    Optional<Lead> findById(UUID id);
    List<Lead> findAllByTenantId(String tenantId);
    List<Lead> findByTenantIdAndStatus(String tenantId, String status);
    List<Lead> findByTenantIdAndAssignedTo(String tenantId, String assignedTo);
    long countByTenantId(String tenantId);
    void delete(Lead lead);
}
