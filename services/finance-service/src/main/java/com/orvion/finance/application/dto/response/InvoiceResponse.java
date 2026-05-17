package com.orvion.finance.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private UUID id;
    private String invoiceNumber;
    private String type;
    private String status;
    private String counterpartyId;
    private String counterpartyName;
    private Instant issueDate;
    private Instant dueDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private String currency;
    private List<InvoiceLineItemResponse> lineItems;
    private String notes;
    private String approvedBy;
    private Instant approvedAt;
    private Instant createdAt;
}
