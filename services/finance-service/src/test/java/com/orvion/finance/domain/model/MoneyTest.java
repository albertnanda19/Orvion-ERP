package com.orvion.finance.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.finance.domain.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void testAdd() {
        Money a = new Money(new BigDecimal("100.0000"), "IDR");
        Money b = new Money(new BigDecimal("200.0000"), "IDR");
        Money result = a.add(b);
        assertEquals(new BigDecimal("300.0000"), result.getAmount());
        assertEquals("IDR", result.getCurrencyCode());
    }

    @Test
    void testSubtract() {
        Money a = new Money(new BigDecimal("500.0000"), "IDR");
        Money b = new Money(new BigDecimal("200.0000"), "IDR");
        Money result = a.subtract(b);
        assertEquals(new BigDecimal("300.0000"), result.getAmount());
    }

    @Test
    void testMultiply() {
        Money a = new Money(new BigDecimal("100.0000"), "IDR");
        Money result = a.multiply(new BigDecimal("3"));
        assertEquals(new BigDecimal("300.0000"), result.getAmount());
    }

    @Test
    void testCurrencyMismatchThrowsException() {
        Money idr = new Money(new BigDecimal("100.0000"), "IDR");
        Money usd = new Money(new BigDecimal("10.0000"), "USD");
        assertThrows(BusinessException.class, () -> idr.add(usd));
        assertThrows(BusinessException.class, () -> idr.subtract(usd));
    }

    @Test
    void testIsPositive() {
        assertTrue(new Money(new BigDecimal("100.0000"), "IDR").isPositive());
        assertFalse(Money.zero("IDR").isPositive());
        assertFalse(new Money(new BigDecimal("-10.0000"), "IDR").isPositive());
    }

    @Test
    void testIsZero() {
        assertTrue(Money.zero("IDR").isZero());
        assertFalse(new Money(new BigDecimal("100.0000"), "IDR").isZero());
    }

    @Test
    void testNegate() {
        Money a = new Money(new BigDecimal("100.0000"), "IDR");
        Money negated = a.negate();
        assertEquals(new BigDecimal("-100.0000"), negated.getAmount());
    }

    @Test
    void testCompareTo() {
        Money a = new Money(new BigDecimal("100.0000"), "IDR");
        Money b = new Money(new BigDecimal("200.0000"), "IDR");
        assertTrue(a.isLessThan(b));
        assertTrue(b.isGreaterThan(a));
        assertEquals(0, a.compareTo(new Money(new BigDecimal("100.0000"), "IDR")));
    }

    @Test
    void testInvalidCurrency() {
        assertThrows(BusinessException.class, () -> new Money(new BigDecimal("100.0000"), "INVALID"));
    }

    @Test
    void testNullAmount() {
        assertThrows(BusinessException.class, () -> new Money(null, "IDR"));
    }

    @Test
    void testScale() {
        Money a = new Money(new BigDecimal("100.5"), "IDR");
        assertEquals(4, a.getAmount().scale());
        assertEquals(new BigDecimal("100.5000"), a.getAmount());
    }
}
