package com.orvion.hcm.domain.repository;

import com.orvion.hcm.domain.model.LeaveRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveRequestRepository {
    LeaveRequest save(LeaveRequest request);
    Optional<LeaveRequest> findById(UUID id);
    List<LeaveRequest> findByTenantIdAndEmployeeId(String tenantId, UUID employeeId);
    List<LeaveRequest> findByTenantId(String tenantId);
}
