package com.orvion.inventory.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.inventory.domain.model.enums.GoodsReceiptStatus;
import com.orvion.inventory.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GoodsReceiptTest {

    private GoodsReceipt gr;
    private UUID purchaseOrderId;
    private UUID warehouseId;
    private UUID purchaseOrderLineId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        purchaseOrderId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();
        gr = new GoodsReceipt("tenant1", purchaseOrderId, warehouseId, "user1", Instant.now(), "Test GR");
        purchaseOrderLineId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    void testCreateGoodsReceipt() {
        assertNotNull(gr.getId());
        assertTrue(gr.getReceiptNumber().startsWith("GR-"));
        assertEquals(GoodsReceiptStatus.DRAFT, gr.getStatus());
        assertEquals(purchaseOrderId, gr.getPurchaseOrderId());
    }

    @Test
    void testConfirm() {
        addSampleLine();
        gr.confirm();
        assertEquals(GoodsReceiptStatus.CONFIRMED, gr.getStatus());
    }

    @Test
    void testConfirmFailsWhenNoLines() {
        assertThrows(BusinessException.class, () -> gr.confirm());
    }

    @Test
    void testConfirmFailsWhenAlreadyConfirmed() {
        addSampleLine();
        gr.confirm();
        assertThrows(BusinessException.class, () -> gr.confirm());
    }

    private void addSampleLine() {
        GoodsReceiptLine line = new GoodsReceiptLine(gr, purchaseOrderLineId, productId, "Test Product",
            new BigDecimal("100"), new BigDecimal("95"), new BigDecimal("5"),
            new Money(new BigDecimal("25000"), "IDR"));
        gr.getLines().add(line);
    }
}
