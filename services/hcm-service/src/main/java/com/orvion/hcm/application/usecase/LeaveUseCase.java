package com.orvion.hcm.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.hcm.application.dto.request.SubmitLeaveRequest;
import com.orvion.hcm.application.dto.response.LeaveBalanceResponse;
import com.orvion.hcm.application.dto.response.LeaveRequestResponse;
import com.orvion.hcm.application.mapper.HcmMapper;
import com.orvion.hcm.domain.model.Employee;
import com.orvion.hcm.domain.model.LeaveBalance;
import com.orvion.hcm.domain.model.LeaveRequest;
import com.orvion.hcm.domain.model.enums.LeaveType;
import com.orvion.hcm.domain.repository.EmployeeRepository;
import com.orvion.hcm.domain.repository.LeaveBalanceRepository;
import com.orvion.hcm.domain.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LeaveUseCase {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final HcmMapper mapper;

    public LeaveUseCase(LeaveRequestRepository leaveRequestRepository,
                        LeaveBalanceRepository leaveBalanceRepository,
                        EmployeeRepository employeeRepository,
                        HcmMapper mapper) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    public LeaveRequestResponse submitLeaveRequest(String tenantId, SubmitLeaveRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId().toString()));
        if (!employee.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Employee does not belong to tenant");

        LeaveType leaveType;
        try {
            leaveType = LeaveType.valueOf(request.getLeaveType());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("INVALID_LEAVE_TYPE", "Invalid leave type: " + request.getLeaveType());
        }

        LeaveRequest leaveRequest = new LeaveRequest(tenantId, request.getEmployeeId(),
            leaveType, request.getStartDate(), request.getEndDate());
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        return mapper.toLeaveRequestResponse(leaveRequest);
    }

    public LeaveRequestResponse approveLeave(String tenantId, UUID leaveId, String managerId) {
        LeaveRequest request = leaveRequestRepository.findById(leaveId)
            .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", leaveId.toString()));
        if (!request.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Leave request does not belong to tenant");
        request.approve(managerId);
        request = leaveRequestRepository.save(request);
        return mapper.toLeaveRequestResponse(request);
    }

    public LeaveRequestResponse rejectLeave(String tenantId, UUID leaveId, String reason) {
        LeaveRequest request = leaveRequestRepository.findById(leaveId)
            .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", leaveId.toString()));
        if (!request.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Leave request does not belong to tenant");
        request.reject(reason);
        request = leaveRequestRepository.save(request);
        return mapper.toLeaveRequestResponse(request);
    }

    public LeaveRequestResponse cancelLeave(String tenantId, UUID leaveId) {
        LeaveRequest request = leaveRequestRepository.findById(leaveId)
            .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", leaveId.toString()));
        if (!request.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Leave request does not belong to tenant");
        request.cancel();
        request = leaveRequestRepository.save(request);
        return mapper.toLeaveRequestResponse(request);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getEmployeeLeaves(String tenantId, UUID employeeId) {
        return mapper.toLeaveRequestResponseList(
            leaveRequestRepository.findByTenantIdAndEmployeeId(tenantId, employeeId));
    }

    @Transactional(readOnly = true)
    public LeaveBalanceResponse getLeaveBalance(UUID employeeId, int year, String leaveType) {
        LeaveBalance balance = leaveBalanceRepository
            .findByEmployeeIdAndYearAndLeaveType(employeeId, year, leaveType)
            .orElseThrow(() -> new ResourceNotFoundException("LeaveBalance", "employeeId", employeeId.toString()));
        return mapper.toLeaveBalanceResponse(balance);
    }
}
