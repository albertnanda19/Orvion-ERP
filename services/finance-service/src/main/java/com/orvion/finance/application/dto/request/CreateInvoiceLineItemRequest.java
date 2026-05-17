package com.orvion.finance.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceLineItemRequest {
    @NotBlank
    private String description;
    @Positive
    private BigDecimal quantity;
    @Positive
    private BigDecimal unitPrice;
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal taxRate;
}
