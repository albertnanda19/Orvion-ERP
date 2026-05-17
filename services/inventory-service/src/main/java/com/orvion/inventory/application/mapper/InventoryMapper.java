package com.orvion.inventory.application.mapper;

import com.orvion.inventory.application.dto.response.*;
import com.orvion.inventory.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", imports = {java.math.BigDecimal.class})
public interface InventoryMapper {

    @Mapping(target = "availableStock", expression = "java(product.getCurrentStock().subtract(product.getReservedStock()))")
    @Mapping(target = "standardCost", expression = "java(product.getStandardCost() != null ? product.getStandardCost().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "costCurrency", expression = "java(product.getStandardCost() != null ? product.getStandardCost().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "costingMethod", expression = "java(product.getCostingMethod().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);

    @Mapping(target = "currency", expression = "java(movement.getUnitCost() != null ? movement.getUnitCost().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "unitCost", expression = "java(movement.getUnitCost() != null ? movement.getUnitCost().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "totalCost", expression = "java(movement.getTotalCost() != null ? movement.getTotalCost().getAmount() : BigDecimal.ZERO)")
    StockMovementResponse toStockMovementResponse(StockMovement movement);

    @Mapping(target = "currency", expression = "java(po.getTotalAmount() != null ? po.getTotalAmount().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "totalAmount", expression = "java(po.getTotalAmount() != null ? po.getTotalAmount().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "status", expression = "java(po.getStatus().name())")
    PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrder po);

    List<PurchaseOrderResponse> toPurchaseOrderResponseList(List<PurchaseOrder> pos);

    @Mapping(target = "currency", expression = "java(line.getUnitPrice() != null ? line.getUnitPrice().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "unitPrice", expression = "java(line.getUnitPrice() != null ? line.getUnitPrice().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "totalPrice", expression = "java(line.getTotalPrice() != null ? line.getTotalPrice().getAmount() : BigDecimal.ZERO)")
    POLineResponse toPOLineResponse(PurchaseOrderLine line);

    @Mapping(target = "status", expression = "java(gr.getStatus().name())")
    GoodsReceiptResponse toGoodsReceiptResponse(GoodsReceipt gr);

    @Mapping(target = "currency", expression = "java(line.getUnitCost() != null ? line.getUnitCost().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "unitCost", expression = "java(line.getUnitCost() != null ? line.getUnitCost().getAmount() : BigDecimal.ZERO)")
    GRLineResponse toGRLineResponse(GoodsReceiptLine line);

    SupplierResponse toSupplierResponse(Supplier supplier);
    List<SupplierResponse> toSupplierResponseList(List<Supplier> suppliers);

    WarehouseResponse toWarehouseResponse(Warehouse warehouse);
    List<WarehouseResponse> toWarehouseResponseList(List<Warehouse> warehouses);
}
