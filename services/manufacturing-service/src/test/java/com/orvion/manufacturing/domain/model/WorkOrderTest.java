package com.orvion.manufacturing.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.manufacturing.domain.model.enums.WorkOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderTest {

    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        workOrder = new WorkOrder("tenant1", "WO-0001", "PROD-001",
            new BigDecimal("100"), "BOM-001",
            Instant.now(), Instant.now().plusSeconds(86400), "WH-001");
    }

    @Test
    void testCreateWorkOrder() {
        assertNotNull(workOrder.getId());
        assertEquals("tenant1", workOrder.getTenantId());
        assertEquals("WO-0001", workOrder.getOrderNumber());
        assertEquals("PROD-001", workOrder.getProductId());
        assertEquals(0, new BigDecimal("100").compareTo(workOrder.getPlannedQuantity()));
        assertEquals(WorkOrderStatus.PLANNED, workOrder.getStatus());
        assertNull(workOrder.getActualStart());
        assertNull(workOrder.getActualEnd());
    }

    @Test
    void testStartWorkOrder() {
        workOrder.start();
        assertEquals(WorkOrderStatus.IN_PROGRESS, workOrder.getStatus());
        assertNotNull(workOrder.getActualStart());
    }

    @Test
    void testStartAlreadyStarted() {
        workOrder.start();
        assertThrows(BusinessException.class, () -> workOrder.start());
    }

    @Test
    void testStartCompletedWorkOrder() {
        workOrder.start();
        workOrder.complete(new BigDecimal("90"));
        assertThrows(BusinessException.class, () -> workOrder.start());
    }

    @Test
    void testCompleteWorkOrder() {
        workOrder.start();
        workOrder.complete(new BigDecimal("90"));
        assertEquals(WorkOrderStatus.COMPLETED, workOrder.getStatus());
        assertEquals(0, new BigDecimal("90").compareTo(workOrder.getActualQuantity()));
        assertNotNull(workOrder.getActualEnd());
    }

    @Test
    void testCompleteWithoutStarting() {
        assertThrows(BusinessException.class, () -> workOrder.complete(new BigDecimal("90")));
    }

    @Test
    void testCompleteWithZeroQty() {
        workOrder.start();
        assertThrows(BusinessException.class, () -> workOrder.complete(BigDecimal.ZERO));
    }

    @Test
    void testCompleteWithNegativeQty() {
        workOrder.start();
        assertThrows(BusinessException.class, () -> workOrder.complete(new BigDecimal("-10")));
    }

    @Test
    void testCancelPlannedWorkOrder() {
        workOrder.cancel("No longer needed");
        assertEquals(WorkOrderStatus.CANCELLED, workOrder.getStatus());
        assertNotNull(workOrder.getActualEnd());
    }

    @Test
    void testCancelInProgressWorkOrder() {
        workOrder.start();
        workOrder.cancel("Machine breakdown");
        assertEquals(WorkOrderStatus.CANCELLED, workOrder.getStatus());
    }

    @Test
    void testCancelCompletedWorkOrder() {
        workOrder.start();
        workOrder.complete(new BigDecimal("100"));
        assertThrows(BusinessException.class, () -> workOrder.cancel("Change of mind"));
    }

    @Test
    void testReportProgress() {
        workOrder.start();
        workOrder.reportProgress(new BigDecimal("50"));
        assertEquals(0, new BigDecimal("50").compareTo(workOrder.getActualQuantity()));
    }

    @Test
    void testReportProgressBeforeStart() {
        assertThrows(BusinessException.class, () -> workOrder.reportProgress(new BigDecimal("50")));
    }

    @Test
    void testReportProgressNegativeQty() {
        workOrder.start();
        assertThrows(BusinessException.class, () -> workOrder.reportProgress(new BigDecimal("-5")));
    }
}
