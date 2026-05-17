package com.orvion.inventory.infrastructure.persistence;

import com.orvion.inventory.domain.model.GoodsReceipt;
import com.orvion.inventory.domain.repository.GoodsReceiptRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoodsReceiptJpaRepository extends JpaRepository<GoodsReceipt, UUID>, GoodsReceiptRepository {
    @Override
    Optional<GoodsReceipt> findByTenantIdAndReceiptNumber(String tenantId, String receiptNumber);
}
