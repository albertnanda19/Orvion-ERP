package com.orvion.inventory.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateSupplierRequest {
    @NotBlank private String code;
    @NotBlank private String name;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String paymentTerms;
}
