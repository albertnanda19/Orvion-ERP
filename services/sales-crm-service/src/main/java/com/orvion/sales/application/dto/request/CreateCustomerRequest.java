package com.orvion.sales.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateCustomerRequest {
    @NotBlank private String name;
    private String email;
    private String phone;
    private String address;
    @NotBlank private String customerType;
    private BigDecimal creditLimit;
    private String creditCurrency;
    private String paymentTerms;
}
