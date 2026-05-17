package com.orvion.hcm.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.hcm.domain.model.enums.LeaveStatus;
import com.orvion.hcm.domain.model.enums.LeaveType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeaveRequestTest {

    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        Instant start = Instant.parse("2024-07-01T00:00:00Z");
        Instant end = Instant.parse("2024-07-05T00:00:00Z");
        leaveRequest = new LeaveRequest("tenant1", UUID.randomUUID(), LeaveType.ANNUAL, start, end);
    }

    @Test
    void testCreateLeaveRequest() {
        assertNotNull(leaveRequest.getId());
        assertEquals(LeaveType.ANNUAL, leaveRequest.getLeaveType());
        assertEquals(LeaveStatus.PENDING, leaveRequest.getStatus());
        assertEquals(5, leaveRequest.getDurationDays());
    }

    @Test
    void testApproveLeave() {
        leaveRequest.approve("manager1");
        assertEquals(LeaveStatus.APPROVED, leaveRequest.getStatus());
        assertEquals("manager1", leaveRequest.getApprovedBy());
    }

    @Test
    void testApproveAlreadyApproved() {
        leaveRequest.approve("manager1");
        assertThrows(BusinessException.class, () -> leaveRequest.approve("manager2"));
    }

    @Test
    void testRejectLeave() {
        leaveRequest.reject("Insufficient leave balance");
        assertEquals(LeaveStatus.REJECTED, leaveRequest.getStatus());
        assertEquals("Insufficient leave balance", leaveRequest.getRejectionReason());
    }

    @Test
    void testRejectAlreadyApproved() {
        leaveRequest.approve("manager1");
        assertThrows(BusinessException.class, () -> leaveRequest.reject("Other reason"));
    }

    @Test
    void testCancelPending() {
        leaveRequest.cancel();
        assertEquals(LeaveStatus.CANCELLED, leaveRequest.getStatus());
    }

    @Test
    void testCancelApproved() {
        leaveRequest.approve("manager1");
        leaveRequest.cancel();
        assertEquals(LeaveStatus.CANCELLED, leaveRequest.getStatus());
    }

    @Test
    void testCancelRejectedFails() {
        leaveRequest.reject("Not eligible");
        assertThrows(BusinessException.class, () -> leaveRequest.cancel());
    }

    @Test
    void testInvalidDateRange() {
        Instant start = Instant.parse("2024-07-10T00:00:00Z");
        Instant end = Instant.parse("2024-07-05T00:00:00Z");
        assertThrows(BusinessException.class, () ->
            new LeaveRequest("tenant1", UUID.randomUUID(), LeaveType.SICK, start, end));
    }
}
