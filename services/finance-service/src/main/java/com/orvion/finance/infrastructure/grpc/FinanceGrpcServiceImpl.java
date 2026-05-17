package com.orvion.finance.infrastructure.grpc;

import com.orvion.finance.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class FinanceGrpcServiceImpl extends FinanceServiceGrpc.FinanceServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(FinanceGrpcServiceImpl.class);
    private static final Map<String, BigDecimal> EXCHANGE_RATES = new ConcurrentHashMap<>();

    private final Timer balanceTimer;
    private final Timer invoiceTimer;
    private final Timer budgetTimer;
    private final Timer exchangeTimer;

    public FinanceGrpcServiceImpl(MeterRegistry meterRegistry) {
        this.balanceTimer = meterRegistry.timer("grpc.account.balance.duration",
            "service", "finance", "method", "getAccountBalance");
        this.invoiceTimer = meterRegistry.timer("grpc.invoice.summary.duration",
            "service", "finance", "method", "getInvoiceSummary");
        this.budgetTimer = meterRegistry.timer("grpc.budget.validate.duration",
            "service", "finance", "method", "validateBudget");
        this.exchangeTimer = meterRegistry.timer("grpc.exchange.rate.duration",
            "service", "finance", "method", "getExchangeRate");

        EXCHANGE_RATES.put("USD_IDR", new BigDecimal("15500.0000"));
        EXCHANGE_RATES.put("IDR_USD", new BigDecimal("0.0000645"));
        EXCHANGE_RATES.put("EUR_IDR", new BigDecimal("16900.0000"));
        EXCHANGE_RATES.put("IDR_EUR", new BigDecimal("0.0000592"));
        EXCHANGE_RATES.put("SGD_IDR", new BigDecimal("11500.0000"));
        EXCHANGE_RATES.put("IDR_SGD", new BigDecimal("0.0000870"));
    }

    @Override
    public void getAccountBalance(AccountBalanceRequest request, StreamObserver<AccountBalanceResponse> responseObserver) {
        Timer.Sample sample = Timer.start();
        try {
            if (request.getTenantId().isBlank()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("tenant_id is required").asRuntimeException());
                return;
            }
            responseObserver.onNext(AccountBalanceResponse.newBuilder()
                .setAccountId(request.getAccountId())
                .setAccountName("Account")
                .setBalance("0.0000")
                .setCurrency(request.getCurrency().isBlank() ? "IDR" : request.getCurrency())
                .setAsOfDate(Instant.now().toString())
                .build());
            responseObserver.onCompleted();
            sample.stop(balanceTimer);
            log.debug("gRPC getAccountBalance: account={}, tenant={}", request.getAccountId(), request.getTenantId());
        } catch (Exception e) {
            log.error("gRPC getAccountBalance error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void getInvoiceSummary(InvoiceSummaryRequest request, StreamObserver<InvoiceSummaryResponse> responseObserver) {
        Timer.Sample sample = Timer.start();
        try {
            responseObserver.onNext(InvoiceSummaryResponse.newBuilder()
                .setTotalCount(0)
                .setTotalAmount("0.0000")
                .setPaidAmount("0.0000")
                .setOutstandingAmount("0.0000")
                .setCurrency("IDR")
                .build());
            responseObserver.onCompleted();
            sample.stop(invoiceTimer);
        } catch (Exception e) {
            log.error("gRPC getInvoiceSummary error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void validateBudget(BudgetValidationRequest request, StreamObserver<BudgetValidationResponse> responseObserver) {
        Timer.Sample sample = Timer.start();
        try {
            BigDecimal requestedAmount = new BigDecimal(request.getAmount());
            BigDecimal available = new BigDecimal("1000000000.0000"); // Simplified: 1B default budget

            boolean approved = requestedAmount.compareTo(available) <= 0;
            String message = approved
                ? "Budget available: " + available.toPlainString()
                : "Budget exceeded. Requested: " + requestedAmount.toPlainString()
                  + ", Available: " + available.toPlainString();

            responseObserver.onNext(BudgetValidationResponse.newBuilder()
                .setApproved(approved)
                .setAvailableBudget(available.toPlainString())
                .setMessage(message)
                .build());
            responseObserver.onCompleted();
            sample.stop(budgetTimer);
        } catch (Exception e) {
            log.error("gRPC validateBudget error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Invalid amount format").asRuntimeException());
        }
    }

    @Override
    public void getExchangeRate(ExchangeRateRequest request, StreamObserver<ExchangeRateResponse> responseObserver) {
        Timer.Sample sample = Timer.start();
        try {
            String key = request.getFromCurrency() + "_" + request.getToCurrency();
            BigDecimal rate = EXCHANGE_RATES.getOrDefault(key, BigDecimal.ONE);

            if (!EXCHANGE_RATES.containsKey(key)) {
                log.warn("Exchange rate not found for {} -> {}, using 1.0", request.getFromCurrency(), request.getToCurrency());
            }

            responseObserver.onNext(ExchangeRateResponse.newBuilder()
                .setFromCurrency(request.getFromCurrency())
                .setToCurrency(request.getToCurrency())
                .setRate(rate.toPlainString())
                .setEffectiveDate(Instant.now().toString())
                .build());
            responseObserver.onCompleted();
            sample.stop(exchangeTimer);
        } catch (Exception e) {
            log.error("gRPC getExchangeRate error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal server error").asRuntimeException());
        }
    }
}
