package com.orvion.inventory.domain.repository;
import com.orvion.inventory.domain.model.GoodsReceipt;
import java.util.Optional;
import java.util.UUID;

public interface GoodsReceiptRepository {
    GoodsReceipt save(GoodsReceipt goodsReceipt);
    Optional<GoodsReceipt> findById(UUID id);
    Optional<GoodsReceipt> findByTenantIdAndReceiptNumber(String tenantId, String receiptNumber);
}
