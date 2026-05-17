package com.orvion.hcm.presentation.controller;

import com.orvion.hcm.application.dto.request.SubmitLeaveRequest;
import com.orvion.hcm.application.dto.response.LeaveBalanceResponse;
import com.orvion.hcm.application.dto.response.LeaveRequestResponse;
import com.orvion.hcm.application.usecase.LeaveUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hcm/leaves")
@Tag(name = "Leaves", description = "Leave management endpoints")
public class LeaveController extends BaseController {
    private final LeaveUseCase leaveUseCase;

    public LeaveController(LeaveUseCase leaveUseCase) { this.leaveUseCase = leaveUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a leave request")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN')")
    public LeaveRequestResponse submitLeave(@Valid @RequestBody SubmitLeaveRequest request) {
        return leaveUseCase.submitLeaveRequest(extractTenantId(), request);
    }

    @PostMapping("/{leaveId}/approve")
    @Operation(summary = "Approve a leave request")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public LeaveRequestResponse approveLeave(@PathVariable UUID leaveId, @RequestParam String managerId) {
        return leaveUseCase.approveLeave(extractTenantId(), leaveId, managerId);
    }

    @PostMapping("/{leaveId}/reject")
    @Operation(summary = "Reject a leave request")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'SUPER_ADMIN')")
    public LeaveRequestResponse rejectLeave(@PathVariable UUID leaveId, @RequestParam String reason) {
        return leaveUseCase.rejectLeave(extractTenantId(), leaveId, reason);
    }

    @PostMapping("/{leaveId}/cancel")
    @Operation(summary = "Cancel a leave request")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN')")
    public LeaveRequestResponse cancelLeave(@PathVariable UUID leaveId) {
        return leaveUseCase.cancelLeave(extractTenantId(), leaveId);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get leave requests by employee")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<LeaveRequestResponse> getEmployeeLeaves(@PathVariable UUID employeeId) {
        return leaveUseCase.getEmployeeLeaves(extractTenantId(), employeeId);
    }

    @GetMapping("/balance")
    @Operation(summary = "Get leave balance")
    @PreAuthorize("hasAnyRole('HCM_MANAGER', 'HCM_STAFF', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public LeaveBalanceResponse getLeaveBalance(@RequestParam UUID employeeId, @RequestParam int year,
                                                 @RequestParam String leaveType) {
        return leaveUseCase.getLeaveBalance(employeeId, year, leaveType);
    }
}
