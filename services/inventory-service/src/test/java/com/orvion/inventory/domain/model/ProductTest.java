package com.orvion.inventory.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.inventory.domain.model.enums.CostingMethod;
import com.orvion.inventory.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("tenant1", "SKU-001", "Test Product", "PCS", CostingMethod.FIFO);
    }

    @Test
    void testCreateProduct() {
        assertNotNull(product.getId());
        assertEquals("tenant1", product.getTenantId());
        assertEquals("SKU-001", product.getSku());
        assertEquals("Test Product", product.getName());
        assertEquals("PCS", product.getUnit());
        assertEquals(CostingMethod.FIFO, product.getCostingMethod());
        assertTrue(product.isActive());
        assertEquals(0, product.getCurrentStock().compareTo(BigDecimal.ZERO));
        assertEquals(0, product.getReservedStock().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testReceiveStock() {
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        assertEquals(0, new BigDecimal("100.0000").compareTo(product.getCurrentStock()));
        assertEquals(1, product.getStockEntries().size());
    }

    @Test
    void testReceiveStockNegativeQty() {
        assertThrows(BusinessException.class, () ->
            product.receiveStock(new BigDecimal("-10"), new Money(new BigDecimal("25000"), "IDR")));
    }

    @Test
    void testReceiveStockZeroQty() {
        assertThrows(BusinessException.class, () ->
            product.receiveStock(BigDecimal.ZERO, new Money(new BigDecimal("25000"), "IDR")));
    }

    @Test
    void testIssueStockFIFO() {
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("20000"), "IDR"));
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        product.issueStock(new BigDecimal("150"), "REF-001");

        assertEquals(0, new BigDecimal("50").compareTo(product.getCurrentStock()));
        assertEquals(1, product.getStockEntries().size());
        assertEquals(0, new BigDecimal("50").compareTo(product.getStockEntries().get(0).getRemainingQuantity()));
        assertEquals(0, new BigDecimal("25000").compareTo(product.getStockEntries().get(0).getUnitCost().getAmount()));
    }

    @Test
    void testIssueStockLIFO() {
        product = new Product("tenant1", "SKU-002", "LIFO Product", "PCS", CostingMethod.LIFO);
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("20000"), "IDR"));
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        product.issueStock(new BigDecimal("150"), "REF-002");

        assertEquals(0, new BigDecimal("50").compareTo(product.getCurrentStock()));
        assertEquals(1, product.getStockEntries().size());
        assertEquals(0, new BigDecimal("50").compareTo(product.getStockEntries().get(0).getRemainingQuantity()));
        assertEquals(0, new BigDecimal("20000").compareTo(product.getStockEntries().get(0).getUnitCost().getAmount()));
    }

    @Test
    void testIssueStockAverage() {
        product = new Product("tenant1", "SKU-003", "AVG Product", "PCS", CostingMethod.AVERAGE_COST);
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("20000"), "IDR"));
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("30000"), "IDR"));
        product.issueStock(new BigDecimal("50"), "REF-003");

        assertEquals(0, new BigDecimal("150").compareTo(product.getCurrentStock()));
    }

    @Test
    void testIssueStockInsufficient() {
        product.receiveStock(new BigDecimal("50"), new Money(new BigDecimal("25000"), "IDR"));
        assertThrows(BusinessException.class, () ->
            product.issueStock(new BigDecimal("100"), "REF-004"));
    }

    @Test
    void testIssueStockNegativeQty() {
        assertThrows(BusinessException.class, () ->
            product.issueStock(new BigDecimal("-10"), "REF-005"));
    }

    @Test
    void testReserveStock() {
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        product.reserveStock(new BigDecimal("30"), "ORDER-001");

        assertEquals(0, new BigDecimal("30").compareTo(product.getReservedStock()));
    }

    @Test
    void testReserveStockInsufficient() {
        product.receiveStock(new BigDecimal("10"), new Money(new BigDecimal("25000"), "IDR"));
        assertThrows(BusinessException.class, () ->
            product.reserveStock(new BigDecimal("20"), "ORDER-002"));
    }

    @Test
    void testReserveStockNegativeQty() {
        assertThrows(BusinessException.class, () ->
            product.reserveStock(new BigDecimal("-5"), "ORDER-003"));
    }

    @Test
    void testReleaseReservation() {
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        product.reserveStock(new BigDecimal("50"), "ORDER-004");
        product.releaseReservation(new BigDecimal("20"), "ORDER-004");

        assertEquals(0, new BigDecimal("30").compareTo(product.getReservedStock()));
    }

    @Test
    void testReleaseReservationExceeds() {
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        product.reserveStock(new BigDecimal("30"), "ORDER-005");
        assertThrows(BusinessException.class, () ->
            product.releaseReservation(new BigDecimal("50"), "ORDER-005"));
    }

    @Test
    void testIsReorderRequired() {
        product.setReorderPoint(new BigDecimal("50.0000"));
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        assertFalse(product.isReorderRequired());

        product.reserveStock(new BigDecimal("60"), "ORDER-006");
        assertTrue(product.isReorderRequired());
    }

    @Test
    void testCalculateAverageCost() {
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("20000"), "IDR"));
        product.receiveStock(new BigDecimal("100"), new Money(new BigDecimal("30000"), "IDR"));
        product.issueStock(new BigDecimal("50"), "REF-006");

        BigDecimal avgCost = product.calculateAverageCost();
        assertEquals(0, new BigDecimal("20000").compareTo(avgCost));
    }

    @Test
    void testCalculateAverageCostEmptyEntries() {
        assertEquals(0, product.calculateAverageCost().compareTo(BigDecimal.ZERO));
    }
}
