package com.orvion.manufacturing.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BillOfMaterialsTest {

    private BillOfMaterials bom;

    @BeforeEach
    void setUp() {
        bom = new BillOfMaterials("tenant1", "PROD-001", 1);
    }

    @Test
    void testCreateBom() {
        assertNotNull(bom.getId());
        assertEquals("tenant1", bom.getTenantId());
        assertEquals("PROD-001", bom.getProductId());
        assertEquals(1, bom.getVersion());
        assertTrue(bom.isActive());
        assertNotNull(bom.getEffectiveDate());
        assertTrue(bom.getComponents().isEmpty());
    }

    @Test
    void testAddComponent() {
        bom.addComponent("RAW-001", new BigDecimal("2.5"), "KG", new BigDecimal("5.00"));
        assertEquals(1, bom.getComponents().size());
        BomComponent component = bom.getComponents().get(0);
        assertNotNull(component.getId());
        assertEquals("RAW-001", component.getComponentProductId());
        assertEquals(0, new BigDecimal("2.5").compareTo(component.getQuantity()));
        assertEquals("KG", component.getUnit());
        assertEquals(0, new BigDecimal("5.00").compareTo(component.getWastePercentage()));
    }

    @Test
    void testAddMultipleComponents() {
        bom.addComponent("RAW-001", new BigDecimal("2.0"), "KG", null);
        bom.addComponent("RAW-002", new BigDecimal("1.5"), "LTR", new BigDecimal("3.00"));
        assertEquals(2, bom.getComponents().size());
        assertEquals(0, BigDecimal.ZERO.compareTo(bom.getComponents().get(0).getWastePercentage()));
    }

    @Test
    void testDeactivateBom() {
        assertTrue(bom.isActive());
        bom.deactivate();
        assertFalse(bom.isActive());
    }
}
