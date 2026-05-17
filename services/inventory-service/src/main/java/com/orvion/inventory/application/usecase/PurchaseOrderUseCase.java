package com.orvion.inventory.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.inventory.application.dto.request.CreatePurchaseOrderRequest;
import com.orvion.inventory.application.dto.response.PurchaseOrderResponse;
import com.orvion.inventory.application.mapper.InventoryMapper;
import com.orvion.inventory.domain.event.PurchaseOrderApprovedEvent;
import com.orvion.inventory.domain.model.PurchaseOrder;
import com.orvion.inventory.domain.model.PurchaseOrderLine;
import com.orvion.inventory.domain.model.vo.Money;
import com.orvion.inventory.domain.repository.PurchaseOrderRepository;
import com.orvion.inventory.infrastructure.grpc.client.FinanceServiceGrpcClient;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class PurchaseOrderUseCase {
    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderUseCase.class);
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryMapper mapper;
    private final FinanceServiceGrpcClient financeGrpcClient;
    private final Counter purchaseOrdersCounter;

    public PurchaseOrderUseCase(PurchaseOrderRepository purchaseOrderRepository,
                                 InventoryMapper mapper,
                                 FinanceServiceGrpcClient financeGrpcClient,
                                 @Qualifier("purchaseOrdersCounter") Counter purchaseOrdersCounter) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.mapper = mapper;
        this.financeGrpcClient = financeGrpcClient;
        this.purchaseOrdersCounter = purchaseOrdersCounter;
    }

    public PurchaseOrderResponse createPurchaseOrder(String tenantId, String userId, CreatePurchaseOrderRequest request) {
        PurchaseOrder po = new PurchaseOrder(tenantId, request.getSupplierId(), request.getSupplierName(),
            java.time.Instant.now(), request.getExpectedDelivery(), request.getNotes());
        if (request.getLines() != null) {
            for (var lineReq : request.getLines()) {
                Money unitPrice = new Money(lineReq.getUnitPrice(),
                    lineReq.getCurrency() != null ? lineReq.getCurrency() : "IDR");
                PurchaseOrderLine line = new PurchaseOrderLine(po,
                    UUID.fromString(lineReq.getProductId()), lineReq.getProductName(), lineReq.getSku(),
                    lineReq.getQuantity(), unitPrice);
                po.addLine(line);
            }
        }
        po = purchaseOrderRepository.save(po);
        purchaseOrdersCounter.increment();
        log.info("Purchase order created: po={}, supplier={}, tenant={}", po.getPoNumber(), po.getSupplierId(), tenantId);
        return mapper.toPurchaseOrderResponse(po);
    }

    public PurchaseOrderResponse approvePurchaseOrder(String tenantId, String approverId, UUID poId) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
            .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", poId.toString()));
        if (!po.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "PO does not belong to tenant");
        var budgetResp = financeGrpcClient.validateBudget("default", "5-1000",
            po.getTotalAmount() != null ? po.getTotalAmount().getAmount().toPlainString() : "0", tenantId);
        log.info("Budget validation result: valid={}, message={}", budgetResp.getApproved(), budgetResp.getMessage());
        po.approve(approverId);
        po = purchaseOrderRepository.save(po);
        log.info("Purchase order approved: po={}, approver={}, tenant={}", po.getPoNumber(), approverId, tenantId);
        return mapper.toPurchaseOrderResponse(po);
    }

    public PurchaseOrderResponse cancelPurchaseOrder(String tenantId, UUID poId, String reason) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
            .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", poId.toString()));
        if (!po.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "PO does not belong to tenant");
        po.cancel(reason);
        po = purchaseOrderRepository.save(po);
        return mapper.toPurchaseOrderResponse(po);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrder(String tenantId, UUID poId) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
            .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", poId.toString()));
        if (!po.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "PO does not belong to tenant");
        return mapper.toPurchaseOrderResponse(po);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> getActivePurchaseOrders(String tenantId, Pageable pageable) {
        return purchaseOrderRepository.findByTenantId(tenantId, pageable).map(mapper::toPurchaseOrderResponse);
    }
}
