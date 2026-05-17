package com.orvion.manufacturing.infrastructure.persistence.jpa;

import com.orvion.manufacturing.domain.model.QualityInspection;
import com.orvion.manufacturing.domain.model.enums.QualityStatus;
import com.orvion.manufacturing.domain.repository.QualityInspectionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QualityInspectionJpaRepository extends JpaRepository<QualityInspection, UUID>, QualityInspectionRepository {
    @Override
    List<QualityInspection> findAllByTenantId(String tenantId);

    @Override
    List<QualityInspection> findByTenantIdAndWorkOrderId(String tenantId, String workOrderId);

    @Override
    List<QualityInspection> findByTenantIdAndStatus(String tenantId, QualityStatus status);
}
