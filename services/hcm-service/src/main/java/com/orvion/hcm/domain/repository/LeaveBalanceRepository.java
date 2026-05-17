package com.orvion.hcm.domain.repository;

import com.orvion.hcm.domain.model.LeaveBalance;
import java.util.Optional;
import java.util.UUID;

public interface LeaveBalanceRepository {
    LeaveBalance save(LeaveBalance balance);
    Optional<LeaveBalance> findById(UUID id);
    Optional<LeaveBalance> findByEmployeeIdAndYearAndLeaveType(UUID employeeId, int year, String leaveType);
}
