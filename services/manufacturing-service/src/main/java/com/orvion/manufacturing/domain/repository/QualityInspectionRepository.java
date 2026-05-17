package com.orvion.manufacturing.domain.repository;

import com.orvion.manufacturing.domain.model.QualityInspection;
import com.orvion.manufacturing.domain.model.enums.QualityStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QualityInspectionRepository {
    QualityInspection save(QualityInspection inspection);
    Optional<QualityInspection> findById(UUID id);
    List<QualityInspection> findAllByTenantId(String tenantId);
    List<QualityInspection> findByTenantIdAndWorkOrderId(String tenantId, String workOrderId);
    List<QualityInspection> findByTenantIdAndStatus(String tenantId, QualityStatus status);
    void delete(QualityInspection inspection);
}
