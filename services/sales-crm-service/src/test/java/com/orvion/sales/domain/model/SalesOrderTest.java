package com.orvion.sales.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.sales.domain.model.enums.SalesOrderStatus;
import com.orvion.sales.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SalesOrderTest {

    private SalesOrder order;

    @BeforeEach
    void setUp() {
        SalesOrderLine line1 = new SalesOrderLine("prod-1", "Product A", "SKU-A",
            BigDecimal.TEN, new Money(new BigDecimal("10000"), "IDR"));
        SalesOrderLine line2 = new SalesOrderLine("prod-2", "Product B", "SKU-B",
            BigDecimal.valueOf(5), new Money(new BigDecimal("20000"), "IDR"));
        order = new SalesOrder("tenant1", "cust-001", "salesrep1", List.of(line1, line2));
    }

    @Test
    void testCreateSalesOrder() {
        assertNotNull(order.getId());
        assertTrue(order.getOrderNumber().startsWith("SO-"));
        assertEquals("tenant1", order.getTenantId());
        assertEquals("cust-001", order.getCustomerId());
        assertEquals(SalesOrderStatus.DRAFT, order.getStatus());
        assertEquals(2, order.getLines().size());
        assertNotNull(order.getOrderDate());
    }

    @Test
    void testCalculateTotal() {
        assertEquals(0, new BigDecimal("200000.0000").compareTo(order.getTotalAmount().getAmount()));
    }

    @Test
    void testConfirmOrder() {
        order.confirm();
        assertEquals(SalesOrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void testConfirmNonDraftOrder() {
        order.confirm();
        assertThrows(BusinessException.class, () -> order.confirm());
    }

    @Test
    void testShipOrder() {
        order.confirm();
        order.ship();
        assertEquals(SalesOrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void testShipNonConfirmedOrder() {
        assertThrows(BusinessException.class, () -> order.ship());
    }

    @Test
    void testDeliverOrder() {
        order.confirm();
        order.ship();
        order.deliver();
        assertEquals(SalesOrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void testDeliverNonShippedOrder() {
        order.confirm();
        assertThrows(BusinessException.class, () -> order.deliver());
    }

    @Test
    void testCancelOrder() {
        order.confirm();
        order.cancel("Customer request");
        assertEquals(SalesOrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void testCancelDeliveredOrder() {
        order.confirm();
        order.ship();
        order.deliver();
        assertThrows(BusinessException.class, () -> order.cancel("Reason"));
    }

    @Test
    void testCancelAlreadyCancelled() {
        order.cancel("Reason");
        assertThrows(BusinessException.class, () -> order.cancel("Another reason"));
    }
}
