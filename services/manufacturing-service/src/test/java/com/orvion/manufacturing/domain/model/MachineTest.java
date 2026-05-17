package com.orvion.manufacturing.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.manufacturing.domain.model.enums.MachineStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MachineTest {

    private Machine machine;

    @BeforeEach
    void setUp() {
        machine = new Machine("tenant1", "MCH-001", "CNC Machine", "CNC");
    }

    @Test
    void testCreateMachine() {
        assertNotNull(machine.getId());
        assertEquals("tenant1", machine.getTenantId());
        assertEquals("MCH-001", machine.getMachineId());
        assertEquals("CNC Machine", machine.getName());
        assertEquals("CNC", machine.getType());
        assertEquals(MachineStatus.IDLE, machine.getStatus());
    }

    @Test
    void testStartOperation() {
        machine.startOperation();
        assertEquals(MachineStatus.RUNNING, machine.getStatus());
    }

    @Test
    void testStopOperation() {
        machine.startOperation();
        machine.stopOperation();
        assertEquals(MachineStatus.IDLE, machine.getStatus());
    }

    @Test
    void testStopWhenNotRunning() {
        assertThrows(BusinessException.class, () -> machine.stopOperation());
    }

    @Test
    void testMarkMaintenance() {
        machine.markMaintenance();
        assertEquals(MachineStatus.MAINTENANCE, machine.getStatus());
    }

    @Test
    void testStartWhenInMaintenance() {
        machine.markMaintenance();
        assertThrows(BusinessException.class, () -> machine.startOperation());
    }

    @Test
    void testMarkBreakdown() {
        machine.markBreakdown();
        assertEquals(MachineStatus.BREAKDOWN, machine.getStatus());
    }

    @Test
    void testStartWhenInBreakdown() {
        machine.markBreakdown();
        assertThrows(BusinessException.class, () -> machine.startOperation());
    }

    @Test
    void testCompleteMaintenance() {
        machine.markMaintenance();
        Instant nextMaint = Instant.now().plusSeconds(30 * 24 * 3600);
        machine.completeMaintenance(nextMaint);
        assertEquals(MachineStatus.IDLE, machine.getStatus());
        assertNotNull(machine.getLastMaintenanceDate());
        assertEquals(nextMaint, machine.getNextMaintenanceDate());
    }
}
