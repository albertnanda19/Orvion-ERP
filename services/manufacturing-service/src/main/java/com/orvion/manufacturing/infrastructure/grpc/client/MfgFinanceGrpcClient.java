package com.orvion.manufacturing.infrastructure.grpc.client;

import com.orvion.finance.grpc.BudgetValidationRequest;
import com.orvion.finance.grpc.BudgetValidationResponse;
import com.orvion.finance.grpc.FinanceServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MfgFinanceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(MfgFinanceGrpcClient.class);
    private final ManagedChannel channel;
    private final FinanceServiceGrpc.FinanceServiceBlockingStub stub;

    public MfgFinanceGrpcClient() {
        this.channel = ManagedChannelBuilder.forTarget("localhost:9091")
            .usePlaintext()
            .build();
        this.stub = FinanceServiceGrpc.newBlockingStub(channel)
            .withDeadline(Deadline.after(3, TimeUnit.SECONDS));
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
