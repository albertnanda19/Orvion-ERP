package com.orvion.manufacturing.infrastructure.grpc.client;

import com.orvion.inventory.grpc.InventoryServiceGrpc;
import com.orvion.inventory.grpc.StockReservationRequest;
import com.orvion.inventory.grpc.StockReservationResponse;
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
public class MfgInventoryGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(MfgInventoryGrpcClient.class);
    private final ManagedChannel channel;
    private final InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    public MfgInventoryGrpcClient() {
        this.channel = ManagedChannelBuilder.forTarget("localhost:9092")
            .usePlaintext()
            .build();
        this.stub = InventoryServiceGrpc.newBlockingStub(channel)
            .withDeadline(Deadline.after(3, TimeUnit.SECONDS));
    }

    @CircuitBreaker(name = "inventory-grpc-cb", fallbackMethod = "reserveStockFallback")
    public boolean reserveStock(String productId, java.math.BigDecimal quantity, String warehouseId,
                                String tenantId, String reference) {
        StockReservationRequest request = StockReservationRequest.newBuilder()
            .setProductId(productId)
            .setQuantity(quantity.toPlainString())
            .setWarehouseId(warehouseId != null ? warehouseId : "")
            .setTenantId(tenantId)
            .setReference(reference)
            .build();
        StockReservationResponse response = stub.reserveStock(request);
        return response.getSuccess();
    }

    public boolean reserveStockFallback(String productId, java.math.BigDecimal quantity, String warehouseId,
                                        String tenantId, String reference, Throwable t) {
        log.warn("Fallback for reserveStock: productId={}, error={}", productId, t.getMessage());
        return true;
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }
}
