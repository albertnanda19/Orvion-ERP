package com.orvion.finance.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {
    @NotNull
    private String type;
    @NotBlank
    private String counterpartyId;
    private String counterpartyName;
    private Instant issueDate;
    @NotNull
    private Instant dueDate;
    @NotBlank
    private String currency;
    @NotEmpty
    @Valid
    private List<CreateInvoiceLineItemRequest> lineItems;
    private String notes;
}
