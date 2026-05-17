package com.orvion.sales.domain.model.event;

import java.util.UUID;

public record SalesOrderConfirmedEvent(UUID eventId, UUID orderId, String orderNumber, String tenantId, String customerId) {
}
