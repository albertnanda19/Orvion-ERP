package com.orvion.inventory.infrastructure.persistence;

import com.orvion.inventory.domain.model.StockMovement;
import com.orvion.inventory.domain.repository.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface StockMovementJpaRepository extends JpaRepository<StockMovement, UUID>, StockMovementRepository {
    @Override
    Page<StockMovement> findByTenantIdAndProductId(String tenantId, UUID productId, Pageable pageable);
}
