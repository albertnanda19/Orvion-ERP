package com.orvion.sales.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateLeadRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private String email;
    private String phone;
    private String company;
    @NotBlank private String source;
    private String assignedTo;
    private String notes;
}
