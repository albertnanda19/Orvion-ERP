package com.orvion.inventory.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.inventory.application.dto.response.StockMovementResponse;
import com.orvion.inventory.application.mapper.InventoryMapper;
import com.orvion.inventory.domain.event.ReorderTriggeredEvent;
import com.orvion.inventory.domain.event.StockUpdatedEvent;
import com.orvion.inventory.domain.model.Product;
import com.orvion.inventory.domain.model.StockMovement;
import com.orvion.inventory.domain.model.enums.MovementType;
import com.orvion.inventory.domain.model.vo.Money;
import com.orvion.inventory.domain.repository.ProductRepository;
import com.orvion.inventory.domain.repository.StockMovementRepository;
import com.orvion.inventory.infrastructure.grpc.client.FinanceServiceGrpcClient;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class StockMovementUseCase {
    private static final Logger log = LoggerFactory.getLogger(StockMovementUseCase.class);
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryMapper mapper;
    private final FinanceServiceGrpcClient financeGrpcClient;
    private final Counter stockMovementsCounter;
    private final Counter reorderEventsCounter;

    public StockMovementUseCase(ProductRepository productRepository,
                                 StockMovementRepository stockMovementRepository,
                                 InventoryMapper mapper,
                                 FinanceServiceGrpcClient financeGrpcClient,
                                 @Qualifier("stockMovementsCounter") Counter stockMovementsCounter,
                                 @Qualifier("reorderEventsCounter") Counter reorderEventsCounter) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.mapper = mapper;
        this.financeGrpcClient = financeGrpcClient;
        this.stockMovementsCounter = stockMovementsCounter;
        this.reorderEventsCounter = reorderEventsCounter;
    }

    public StockMovementResponse receiveStock(String tenantId, UUID productId, UUID warehouseId,
                                               BigDecimal quantity, Money unitCost, String reference,
                                               String sourceDocument, String performedBy) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));
        if (!product.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Product does not belong to tenant");
        product.receiveStock(quantity, unitCost);
        productRepository.save(product);
        Money totalCost = unitCost.multiply(quantity);
        StockMovement movement = new StockMovement(tenantId, productId, warehouseId, MovementType.IN,
            quantity, unitCost, totalCost, reference, sourceDocument, performedBy);
        movement = stockMovementRepository.save(movement);
        stockMovementsCounter.increment();
        log.info("Stock received: product={}, qty={}, cost={}, tenant={}", productId, quantity, unitCost, tenantId);
        if (product.isReorderRequired()) {
            ReorderTriggeredEvent reEvent = new ReorderTriggeredEvent(
                product.getId().toString(), product.getName(), product.getSku(),
                product.getCurrentStock(), product.getReorderQuantity(),
                product.getPreferredSupplierId(), tenantId);
            log.info("Reorder triggered for product: {}", product.getSku());
            reorderEventsCounter.increment();
        }
        return mapper.toStockMovementResponse(movement);
    }

    public StockMovementResponse issueStock(String tenantId, UUID productId, UUID warehouseId,
                                              BigDecimal quantity, String reference,
                                              String sourceDocument, String performedBy) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));
        if (!product.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Product does not belong to tenant");
        product.issueStock(quantity, reference);
        productRepository.save(product);
        Money unitCost = product.getStandardCost() != null ? product.getStandardCost() : Money.zero("IDR");
        Money totalCost = unitCost.multiply(quantity);
        StockMovement movement = new StockMovement(tenantId, productId, warehouseId, MovementType.OUT,
            quantity, unitCost, totalCost, reference, sourceDocument, performedBy);
        movement = stockMovementRepository.save(movement);
        stockMovementsCounter.increment();
        log.info("Stock issued: product={}, qty={}, ref={}, tenant={}", productId, quantity, reference, tenantId);
        return mapper.toStockMovementResponse(movement);
    }

    public StockMovementResponse adjustStock(String tenantId, UUID productId, UUID warehouseId,
                                               BigDecimal adjustment, String reason, String performedBy) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId.toString()));
        if (!product.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Product does not belong to tenant");
        Money unitCost = product.getStandardCost() != null ? product.getStandardCost() : Money.zero("IDR");
        if (adjustment.compareTo(BigDecimal.ZERO) > 0) {
            product.receiveStock(adjustment, unitCost);
        } else {
            product.issueStock(adjustment.abs(), reason);
        }
        productRepository.save(product);
        StockMovement movement = new StockMovement(tenantId, productId, warehouseId, MovementType.ADJUSTMENT,
            adjustment.abs(), unitCost, unitCost.multiply(adjustment.abs()), reason, reason, performedBy);
        movement = stockMovementRepository.save(movement);
        stockMovementsCounter.increment();
        return mapper.toStockMovementResponse(movement);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> getMovementHistory(String tenantId, UUID productId, Pageable pageable) {
        return stockMovementRepository.findByTenantIdAndProductId(tenantId, productId, pageable)
            .map(mapper::toStockMovementResponse);
    }
}
