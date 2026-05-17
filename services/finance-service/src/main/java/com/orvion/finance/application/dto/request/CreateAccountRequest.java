package com.orvion.finance.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotBlank(message = "Account code is required")
    @Pattern(regexp = "^[1-9][0-9]{3,7}$", message = "Account code must be 4-8 digits starting with 1-9")
    private String code;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 200, message = "Account name must be between 2 and 200 characters")
    private String name;

    @NotNull(message = "Account type is required")
    private String type;

    private String description;
    private String parentAccountId;
}

