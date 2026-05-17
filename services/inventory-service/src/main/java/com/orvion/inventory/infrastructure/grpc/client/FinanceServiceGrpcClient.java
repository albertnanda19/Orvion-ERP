package com.orvion.inventory.infrastructure.grpc.client;

import com.orvion.finance.grpc.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
public class FinanceServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(FinanceServiceGrpcClient.class);
    private final ManagedChannel channel;
    private final FinanceServiceGrpc.FinanceServiceBlockingStub stub;

    public FinanceServiceGrpcClient() {
        this.channel = ManagedChannelBuilder.forTarget("localhost:9091")
            .usePlaintext()
            .build();
        this.stub = FinanceServiceGrpc.newBlockingStub(channel)
            .withDeadline(Deadline.after(3, TimeUnit.SECONDS));
    }

    @CircuitBreaker(name = "finance-grpc-cb", fallbackMethod = "getAccountBalanceFallback")
    public AccountBalanceResponse getAccountBalance(String accountId, String tenantId) {
        AccountBalanceRequest request = AccountBalanceRequest.newBuilder()
            .setAccountId(accountId)
            .setTenantId(tenantId)
            .build();
        return stub.getAccountBalance(request);
    }

    public AccountBalanceResponse getAccountBalanceFallback(String accountId, String tenantId, Throwable t) {
        log.warn("Fallback for getAccountBalance: accountId={}, tenantId={}, error={}", accountId, tenantId, t.getMessage());
        return AccountBalanceResponse.newBuilder()
            .setAccountId(accountId)
            .setBalance("0")
            .setCurrency("IDR")
            .build();
    }

    @CircuitBreaker(name = "finance-grpc-cb", fallbackMethod = "validateBudgetFallback")
    public BudgetValidationResponse validateBudget(String department, String accountCode, String amount, String tenantId) {
        BudgetValidationRequest request = BudgetValidationRequest.newBuilder()
            .setDepartment(department)
            .setAccountCode(accountCode)
            .setAmount(amount)
            .setTenantId(tenantId)
            .build();
        return stub.validateBudget(request);
    }

    public BudgetValidationResponse validateBudgetFallback(String department, String accountCode, String amount, String tenantId, Throwable t) {
        log.warn("Fallback for validateBudget: dept={}, account={}, error={}", department, accountCode, t.getMessage());
        return BudgetValidationResponse.newBuilder()
            .setApproved(true)
            .setMessage("Budget validation bypassed (circuit breaker open)")
            .build();
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }
}
