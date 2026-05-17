package com.orvion.inventory.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.inventory.domain.model.enums.PurchaseOrderStatus;
import com.orvion.inventory.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseOrderTest {

    private PurchaseOrder po;
    private UUID productId;

    @BeforeEach
    void setUp() {
        po = new PurchaseOrder("tenant1", "SUPP-001", "PT Supplier", Instant.now(), null, "Test PO");
        productId = UUID.randomUUID();
    }

    @Test
    void testCreatePurchaseOrder() {
        assertNotNull(po.getId());
        assertTrue(po.getPoNumber().startsWith("PO-"));
        assertEquals(PurchaseOrderStatus.DRAFT, po.getStatus());
        assertEquals("tenant1", po.getTenantId());
    }

    @Test
    void testAddLine() {
        PurchaseOrderLine line = new PurchaseOrderLine(po, productId, "Test Product", "SKU-001",
            new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        po.addLine(line);

        assertEquals(1, po.getLines().size());
        assertEquals(new BigDecimal("2500000.0000"), po.getTotalAmount().getAmount());
    }

    @Test
    void testApprove() {
        addSampleLine();
        po.approve("user1");

        assertEquals(PurchaseOrderStatus.APPROVED, po.getStatus());
        assertEquals("user1", po.getApprovedBy());
        assertNotNull(po.getApprovedAt());
    }

    @Test
    void testApproveFailsWhenNoLines() {
        assertThrows(BusinessException.class, () -> po.approve("user1"));
    }

    @Test
    void testApproveFailsWhenAlreadyApproved() {
        addSampleLine();
        po.approve("user1");
        assertThrows(BusinessException.class, () -> po.approve("user2"));
    }

    @Test
    void testCancel() {
        po.cancel("Changed mind");
        assertEquals(PurchaseOrderStatus.CANCELLED, po.getStatus());
    }

    @Test
    void testCancelFailsWhenApproved() {
        addSampleLine();
        po.approve("user1");
        assertThrows(BusinessException.class, () -> po.cancel("Test"));
    }

    @Test
    void testMarkSent() {
        addSampleLine();
        po.approve("user1");
        po.markSent();
        assertEquals(PurchaseOrderStatus.SENT, po.getStatus());
    }

    @Test
    void testMarkSentFailsWhenDraft() {
        assertThrows(BusinessException.class, () -> po.markSent());
    }

    private void addSampleLine() {
        PurchaseOrderLine line = new PurchaseOrderLine(po, productId, "Test Product", "SKU-001",
            new BigDecimal("100"), new Money(new BigDecimal("25000"), "IDR"));
        po.addLine(line);
    }
}
