package com.orvion.inventory.domain.model.vo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    @Test
    void testCreateQuantity() {
        Quantity q = new Quantity(new BigDecimal("100"), "KG");
        assertEquals(new BigDecimal("100.0000"), q.getValue());
        assertEquals("KG", q.getUnit());
    }

    @Test
    void testAdd() {
        Quantity a = new Quantity(new BigDecimal("100"), "KG");
        Quantity b = new Quantity(new BigDecimal("50"), "KG");
        Quantity result = a.add(b);
        assertEquals(new BigDecimal("150.0000"), result.getValue());
    }

    @Test
    void testSubtract() {
        Quantity a = new Quantity(new BigDecimal("100"), "KG");
        Quantity b = new Quantity(new BigDecimal("30"), "KG");
        Quantity result = a.subtract(b);
        assertEquals(new BigDecimal("70.0000"), result.getValue());
    }

    @Test
    void testUnitMismatch() {
        Quantity a = new Quantity(new BigDecimal("100"), "KG");
        Quantity b = new Quantity(new BigDecimal("50"), "PCS");
        assertThrows(IllegalArgumentException.class, () -> a.add(b));
    }

    @Test
    void testIsZero() {
        assertTrue(Quantity.zero("PCS").isZero());
        assertFalse(new Quantity(new BigDecimal("10"), "PCS").isZero());
    }

    @Test
    void testIsPositive() {
        assertTrue(new Quantity(new BigDecimal("10"), "PCS").isPositive());
        assertFalse(Quantity.zero("PCS").isPositive());
    }

    @Test
    void testNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(null, "KG"));
    }

    @Test
    void testEmptyUnit() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(new BigDecimal("10"), ""));
    }

    @Test
    void testNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(new BigDecimal("-10"), "KG"));
    }
}
