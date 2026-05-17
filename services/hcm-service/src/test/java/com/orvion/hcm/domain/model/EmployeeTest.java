package com.orvion.hcm.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.hcm.domain.model.enums.EmploymentStatus;
import com.orvion.hcm.domain.model.enums.EmploymentType;
import com.orvion.hcm.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee("tenant1", "John", "Doe", "john@test.com",
            EmploymentType.FULL_TIME, "Engineering", "Developer");
    }

    @Test
    void testCreateEmployee() {
        assertNotNull(employee.getId());
        assertEquals("tenant1", employee.getTenantId());
        assertTrue(employee.getEmployeeId().startsWith("EMP-"));
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals(EmploymentType.FULL_TIME, employee.getEmploymentType());
        assertEquals(EmploymentStatus.PROBATION, employee.getEmploymentStatus());
        assertTrue(employee.isActive());
        assertNotNull(employee.getJoinDate());
    }

    @Test
    void testTerminateEmployee() {
        employee.terminate("Resigned", Instant.now());
        assertEquals(EmploymentStatus.TERMINATED, employee.getEmploymentStatus());
        assertFalse(employee.isActive());
        assertNotNull(employee.getTerminationDate());
    }

    @Test
    void testTerminateAlreadyTerminated() {
        employee.terminate("Resigned", Instant.now());
        assertThrows(BusinessException.class, () ->
            employee.terminate("Other reason", Instant.now()));
    }

    @Test
    void testPromoteEmployee() {
        Money newSalary = new Money(new BigDecimal("30000000"), "IDR");
        employee.promote("Senior Developer", newSalary);
        assertEquals("Senior Developer", employee.getPosition());
        assertEquals(0, new BigDecimal("30000000.0000").compareTo(employee.getBasicSalary().getAmount()));
    }

    @Test
    void testPromoteTerminatedEmployee() {
        employee.terminate("Resigned", Instant.now());
        assertThrows(BusinessException.class, () ->
            employee.promote("Senior Developer", null));
    }

    @Test
    void testSuspendEmployee() {
        employee.suspend("Policy violation");
        assertEquals(EmploymentStatus.SUSPENDED, employee.getEmploymentStatus());
    }

    @Test
    void testSuspendTerminatedEmployee() {
        employee.terminate("Resigned", Instant.now());
        assertThrows(BusinessException.class, () ->
            employee.suspend("Policy violation"));
    }

    @Test
    void testCompleteProbation() {
        employee.completeProbation();
        assertEquals(EmploymentStatus.ACTIVE, employee.getEmploymentStatus());
    }

    @Test
    void testCompleteProbationNotInProbation() {
        employee.completeProbation();
        assertThrows(BusinessException.class, () ->
            employee.completeProbation());
    }
}
