package com.orvion.inventory.domain.model;

import com.orvion.inventory.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StockEntryTest {

    private Product product;
    private StockEntry entry;

    @BeforeEach
    void setUp() {
        product = new Product("tenant1", "SKU-001", "Test", "PCS", null);
        entry = new StockEntry(product, new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
    }

    @Test
    void testCreateStockEntry() {
        assertNotNull(entry.getId());
        assertEquals(0, new BigDecimal("100").compareTo(entry.getQuantity()));
        assertEquals(0, new BigDecimal("100").compareTo(entry.getRemainingQuantity()));
        assertNotNull(entry.getReceivedAt());
    }

    @Test
    void testConsume() {
        entry.consume(new BigDecimal("30"));
        assertEquals(0, new BigDecimal("70").compareTo(entry.getRemainingQuantity()));
    }

    @Test
    void testConsumeExceedsRemaining() {
        assertThrows(IllegalArgumentException.class, () -> entry.consume(new BigDecimal("150")));
    }

    @Test
    void testConsumeAll() {
        entry.consume(new BigDecimal("100"));
        assertEquals(0, entry.getRemainingQuantity().compareTo(BigDecimal.ZERO));
    }
}
