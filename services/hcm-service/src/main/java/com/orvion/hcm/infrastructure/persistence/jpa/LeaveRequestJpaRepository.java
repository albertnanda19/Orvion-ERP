package com.orvion.hcm.infrastructure.persistence.jpa;

import com.orvion.hcm.domain.model.LeaveRequest;
import com.orvion.hcm.domain.repository.LeaveRequestRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestJpaRepository extends JpaRepository<LeaveRequest, UUID>, LeaveRequestRepository {
    @Override
    List<LeaveRequest> findByTenantIdAndEmployeeId(String tenantId, UUID employeeId);

    @Override
    List<LeaveRequest> findByTenantId(String tenantId);
}
