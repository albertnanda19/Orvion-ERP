package com.orvion.sales.presentation.controller;

import com.orvion.sales.application.dto.request.CreateCustomerRequest;
import com.orvion.sales.application.dto.response.CustomerResponse;
import com.orvion.sales.application.usecase.CustomerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/customers")
@Tag(name = "Customers", description = "Customer management endpoints")
public class CustomerController extends BaseController {
    private final CustomerUseCase customerUseCase;

    public CustomerController(CustomerUseCase customerUseCase) { this.customerUseCase = customerUseCase; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN')")
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return customerUseCase.createCustomer(extractTenantId(), request);
    }

    @GetMapping
    @Operation(summary = "List all customers")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public List<CustomerResponse> getCustomers() {
        return customerUseCase.getCustomers(extractTenantId());
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'SUPER_ADMIN', 'REPORT_VIEWER')")
    public CustomerResponse getCustomer(@PathVariable UUID customerId) {
        return customerUseCase.getCustomerById(extractTenantId(), customerId);
    }
}
