package com.orvion.inventory.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.inventory.application.dto.request.CreateGoodsReceiptRequest;
import com.orvion.inventory.application.dto.response.GoodsReceiptResponse;
import com.orvion.inventory.application.mapper.InventoryMapper;
import com.orvion.inventory.domain.event.GoodsReceivedEvent;
import com.orvion.inventory.domain.model.*;
import com.orvion.inventory.domain.model.vo.Money;
import com.orvion.inventory.domain.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class GoodsReceiptUseCase {
    private static final Logger log = LoggerFactory.getLogger(GoodsReceiptUseCase.class);
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper mapper;

    public GoodsReceiptUseCase(GoodsReceiptRepository goodsReceiptRepository,
                                PurchaseOrderRepository purchaseOrderRepository,
                                ProductRepository productRepository,
                                InventoryMapper mapper) {
        this.goodsReceiptRepository = goodsReceiptRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    public GoodsReceiptResponse createGoodsReceipt(String tenantId, CreateGoodsReceiptRequest request) {
        GoodsReceipt gr = new GoodsReceipt(tenantId,
            UUID.fromString(request.getPurchaseOrderId()),
            UUID.fromString(request.getWarehouseId()),
            request.getReceivedBy(), request.getReceivedAt(), request.getNotes());
        if (request.getLines() != null) {
            for (var lineReq : request.getLines()) {
                BigDecimal rejectedQty = lineReq.getRejectedQuantity() != null ? lineReq.getRejectedQuantity() : BigDecimal.ZERO;
                Money unitCost = new Money(lineReq.getUnitCost(),
                    lineReq.getCurrency() != null ? lineReq.getCurrency() : "IDR");
                GoodsReceiptLine line = new GoodsReceiptLine(gr,
                    UUID.fromString(lineReq.getPurchaseOrderLineId()),
                    UUID.fromString(lineReq.getProductId()), lineReq.getProductName(),
                    lineReq.getQuantity(), lineReq.getAcceptedQuantity(), rejectedQty, unitCost);
                gr.getLines().add(line);
            }
        }
        gr = goodsReceiptRepository.save(gr);
        return mapper.toGoodsReceiptResponse(gr);
    }

    public GoodsReceiptResponse confirmGoodsReceipt(String tenantId, UUID grId) {
        GoodsReceipt gr = goodsReceiptRepository.findById(grId)
            .orElseThrow(() -> new ResourceNotFoundException("GoodsReceipt", "id", grId.toString()));
        if (!gr.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Goods receipt does not belong to tenant");
        UUID poId = gr.getPurchaseOrderId();
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
            .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", poId.toString()));
        for (GoodsReceiptLine line : gr.getLines()) {
            Product product = productRepository.findById(line.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", line.getProductId().toString()));
            product.receiveStock(line.getAcceptedQuantity(), line.getUnitCost());
            productRepository.save(product);
            for (PurchaseOrderLine poLine : po.getLines()) {
                if (poLine.getProductId().equals(line.getProductId())) {
                    poLine.setReceivedQuantity(poLine.getReceivedQuantity().add(line.getAcceptedQuantity()));
                    break;
                }
            }
        }
        boolean allReceived = po.getLines().stream()
            .allMatch(l -> l.getReceivedQuantity().compareTo(l.getQuantity()) >= 0);
        if (allReceived) po.setStatus(com.orvion.inventory.domain.model.enums.PurchaseOrderStatus.RECEIVED);
        else po.setStatus(com.orvion.inventory.domain.model.enums.PurchaseOrderStatus.PARTIALLY_RECEIVED);
        purchaseOrderRepository.save(po);
        gr.confirm();
        gr = goodsReceiptRepository.save(gr);
        log.info("Goods receipt confirmed: gr={}, po={}, tenant={}", gr.getReceiptNumber(), po.getPoNumber(), tenantId);
        return mapper.toGoodsReceiptResponse(gr);
    }
}
