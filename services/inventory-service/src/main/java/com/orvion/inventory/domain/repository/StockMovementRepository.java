package com.orvion.inventory.domain.repository;
import com.orvion.inventory.domain.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface StockMovementRepository {
    StockMovement save(StockMovement movement);
    Page<StockMovement> findByTenantIdAndProductId(String tenantId, UUID productId, Pageable pageable);
}
