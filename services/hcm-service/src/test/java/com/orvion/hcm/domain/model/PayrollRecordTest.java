package com.orvion.hcm.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.hcm.domain.model.enums.FiscalPeriod;
import com.orvion.hcm.domain.model.enums.PayrollStatus;
import com.orvion.hcm.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PayrollRecordTest {

    private PayrollRecord record;

    @BeforeEach
    void setUp() {
        Money salary = new Money(new BigDecimal("10000000"), "IDR");
        record = new PayrollRecord("tenant1", UUID.randomUUID(), new FiscalPeriod(2024, 6), salary);
        record.setAllowances(new BigDecimal("2000000"));
        record.setOvertime(new BigDecimal("500000"));
        record.setDeductions(new BigDecimal("500000"));
        record.setTaxAmount(new BigDecimal("1000000"));
    }

    @Test
    void testCreatePayrollRecord() {
        assertNotNull(record.getId());
        assertEquals("tenant1", record.getTenantId());
        assertEquals(PayrollStatus.DRAFT, record.getStatus());
    }

    @Test
    void testCalculatePayroll() {
        record.calculate();
        assertEquals(PayrollStatus.PROCESSED, record.getStatus());
        // netPay = 10,000,000 + 2,000,000 + 500,000 - 500,000 - 1,000,000 = 11,000,000
        assertEquals(0, new BigDecimal("11000000.0000").compareTo(record.getNetPay().getAmount()));
    }

    @Test
    void testCalculateFromDraftOnly() {
        record.calculate();
        assertThrows(BusinessException.class, () -> record.calculate());
    }

    @Test
    void testMarkPaid() {
        record.calculate();
        record.markPaid();
        assertEquals(PayrollStatus.PAID, record.getStatus());
    }

    @Test
    void testMarkPaidFromDraftFails() {
        assertThrows(BusinessException.class, () -> record.markPaid());
    }

    @Test
    void testApprovePayroll() {
        record.calculate();
        record.approve();
        assertEquals(PayrollStatus.PAID, record.getStatus());
    }
}
