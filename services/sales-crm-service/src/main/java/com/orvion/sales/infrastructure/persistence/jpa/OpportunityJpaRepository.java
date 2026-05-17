package com.orvion.sales.infrastructure.persistence.jpa;

import com.orvion.sales.domain.model.Opportunity;
import com.orvion.sales.domain.repository.OpportunityRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface OpportunityJpaRepository extends JpaRepository<Opportunity, UUID>, OpportunityRepository {
    @Override
    List<Opportunity> findAllByTenantId(String tenantId);

    @Override
    List<Opportunity> findByTenantIdAndStage(String tenantId, String stage);

    @Override
    List<Opportunity> findByTenantIdAndAssignedTo(String tenantId, String assignedTo);

    @Override
    @Query("SELECT COUNT(o) FROM Opportunity o WHERE o.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);

    @Override
    @Query("SELECT COUNT(o) FROM Opportunity o WHERE o.tenantId = :tenantId AND o.stage = :stage")
    long countByTenantIdAndStage(@Param("tenantId") String tenantId, @Param("stage") String stage);
}
