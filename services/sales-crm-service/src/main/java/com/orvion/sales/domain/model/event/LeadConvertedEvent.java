package com.orvion.sales.domain.model.event;

import java.util.UUID;

public record LeadConvertedEvent(UUID eventId, UUID leadId, UUID opportunityId, String tenantId, String leadName) {
}
