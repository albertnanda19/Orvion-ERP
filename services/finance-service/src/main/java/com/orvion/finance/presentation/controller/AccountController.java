package com.orvion.finance.presentation.controller;

import com.orvion.finance.application.dto.request.CreateAccountRequest;
import com.orvion.finance.application.dto.response.AccountResponse;
import com.orvion.finance.application.usecase.AccountUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance/accounts")
@Tag(name = "Accounts", description = "Chart of Accounts management")
public class AccountController {

    private final AccountUseCase accountUseCase;

    public AccountController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new account", description = "Creates a new chart of accounts entry")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "Account code already exists")
    })
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request,
                                                          HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        AccountResponse response = accountUseCase.createAccount(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an account")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable UUID id,
                                                          @Valid @RequestBody CreateAccountRequest request,
                                                          HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        AccountResponse response = accountUseCase.updateAccount(tenantId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate an account")
    @PreAuthorize("hasAnyRole('FINANCE_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deactivateAccount(@PathVariable UUID id, HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        accountUseCase.deactivateAccount(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID id, HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        AccountResponse response = accountUseCase.getAccountById(tenantId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get chart of accounts", description = "Returns all active accounts for the tenant")
    public ResponseEntity<List<AccountResponse>> getChartOfAccounts(HttpServletRequest httpRequest) {
        String tenantId = extractTenantId(httpRequest);
        List<AccountResponse> accounts = accountUseCase.getChartOfAccounts(tenantId);
        return ResponseEntity.ok(accounts);
    }

    private String extractTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader("X-Tenant-Id");
        if (tenantId == null || tenantId.isBlank()) {
            throw new com.orvion.common.exception.UnauthorizedException("Missing X-Tenant-Id header");
        }
        return tenantId;
    }
}
