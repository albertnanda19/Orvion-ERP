package com.orvion.inventory.infrastructure.grpc.server;

import com.orvion.inventory.domain.model.Product;
import com.orvion.inventory.domain.repository.ProductRepository;
import com.orvion.inventory.grpc.*;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@GrpcService
public class InventoryGrpcServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {
    private final ProductRepository productRepository;
    private final Timer stockLevelTimer;
    private final Timer productCostTimer;

    public InventoryGrpcServiceImpl(ProductRepository productRepository, MeterRegistry registry) {
        this.productRepository = productRepository;
        this.stockLevelTimer = registry.timer("grpc.inventory.stock_level");
        this.productCostTimer = registry.timer("grpc.inventory.product_cost");
    }

    @Override
    public void getStockLevel(StockLevelRequest request, StreamObserver<StockLevelResponse> responseObserver) {
        stockLevelTimer.record(() -> {
            try {
                UUID productId = UUID.fromString(request.getProductId());
                Optional<Product> opt = productRepository.findById(productId);
                if (opt.isEmpty()) {
                    responseObserver.onError(new RuntimeException("Product not found: " + request.getProductId()));
                    return;
                }
                Product p = opt.get();
                StockLevelResponse response = StockLevelResponse.newBuilder()
                    .setProductId(p.getId().toString())
                    .setProductName(p.getName())
                    .setSku(p.getSku())
                    .setAvailableQuantity(p.getCurrentStock().subtract(p.getReservedStock()).toPlainString())
                    .setReservedQuantity(p.getReservedStock().toPlainString())
                    .setUnit(p.getUnit())
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }

    @Override
    public void getProductCost(ProductCostRequest request, StreamObserver<ProductCostResponse> responseObserver) {
        productCostTimer.record(() -> {
            try {
                UUID productId = UUID.fromString(request.getProductId());
                Optional<Product> opt = productRepository.findById(productId);
                if (opt.isEmpty()) {
                    responseObserver.onError(new RuntimeException("Product not found: " + request.getProductId()));
                    return;
                }
                Product p = opt.get();
                ProductCostResponse response = ProductCostResponse.newBuilder()
                    .setProductId(p.getId().toString())
                    .setUnitCost(p.getStandardCost() != null ? p.getStandardCost().getAmount().toPlainString() : "0")
                    .setCurrency(p.getStandardCost() != null ? p.getStandardCost().getCurrencyCode() : "IDR")
                    .setCostingMethod(p.getCostingMethod().name())
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }

    @Override
    public void checkReorderStatus(ReorderStatusRequest request, StreamObserver<ReorderStatusResponse> responseObserver) {
        try {
            List<Product> lowStock = productRepository.findLowStockProducts(request.getTenantId());
            ReorderStatusResponse.Builder builder = ReorderStatusResponse.newBuilder();
            for (Product p : lowStock) {
                builder.addItems(ProductReorderItem.newBuilder()
                    .setProductId(p.getId().toString())
                    .setProductName(p.getName())
                    .setCurrentStock(p.getCurrentStock().toPlainString())
                    .setReorderPoint(p.getReorderPoint().toPlainString())
                    .setReorderQuantity(p.getReorderQuantity().toPlainString())
                    .setPreferredSupplierId(p.getPreferredSupplierId() != null ? p.getPreferredSupplierId() : "")
                    .build());
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void reserveStock(StockReservationRequest request, StreamObserver<StockReservationResponse> responseObserver) {
        try {
            UUID productId = UUID.fromString(request.getProductId());
            Optional<Product> opt = productRepository.findById(productId);
            if (opt.isEmpty()) {
                responseObserver.onNext(StockReservationResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Product not found")
                    .build());
                responseObserver.onCompleted();
                return;
            }
            Product p = opt.get();
            java.math.BigDecimal qty = new java.math.BigDecimal(request.getQuantity());
            p.reserveStock(qty, request.getReference());
            productRepository.save(p);
            responseObserver.onNext(StockReservationResponse.newBuilder()
                .setSuccess(true)
                .setReservationId(UUID.randomUUID().toString())
                .setMessage("Stock reserved successfully")
                .setAvailableAfterReservation(p.getCurrentStock().subtract(p.getReservedStock()).toPlainString())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(StockReservationResponse.newBuilder()
                .setSuccess(false)
                .setMessage(e.getMessage())
                .build());
            responseObserver.onCompleted();
        }
    }
}
