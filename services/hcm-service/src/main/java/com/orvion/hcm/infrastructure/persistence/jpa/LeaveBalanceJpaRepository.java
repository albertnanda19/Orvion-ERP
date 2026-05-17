package com.orvion.hcm.infrastructure.persistence.jpa;

import com.orvion.hcm.domain.model.LeaveBalance;
import com.orvion.hcm.domain.repository.LeaveBalanceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveBalanceJpaRepository extends JpaRepository<LeaveBalance, UUID>, LeaveBalanceRepository {
    @Override
    Optional<LeaveBalance> findByEmployeeIdAndYearAndLeaveType(UUID employeeId, int year, String leaveType);
}
