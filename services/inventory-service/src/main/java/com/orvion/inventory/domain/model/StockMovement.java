package com.orvion.inventory.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.inventory.domain.model.enums.MovementType;
import com.orvion.inventory.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movements", indexes = {
    @Index(name = "idx_sm_tenant_product", columnList = "tenantId, productId"),
    @Index(name = "idx_sm_tenant_ref", columnList = "tenantId, reference")
})
@Getter @Setter @NoArgsConstructor
public class StockMovement extends Auditable {
    @Id
    private UUID id;
    
    @Column(length = 50, nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private UUID productId;
    
    @Column(nullable = false)
    private UUID warehouseId;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MovementType movementType;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_cost", precision = 18, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "cost_currency", length = 3))
    })
    private Money unitCost;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_cost", precision = 18, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "total_cost_currency", length = 3))
    })
    private Money totalCost;
    
    @Column(length = 100)
    private String reference;
    
    @Column(length = 50)
    private String sourceDocument;
    
    @Column(nullable = false)
    private Instant movementDate;
    
    @Column(length = 50)
    private String performedBy;

    public StockMovement(String tenantId, UUID productId, UUID warehouseId, MovementType movementType,
                         BigDecimal quantity, Money unitCost, Money totalCost, String reference,
                         String sourceDocument, String performedBy) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.reference = reference;
        this.sourceDocument = sourceDocument;
        this.movementDate = Instant.now();
        this.performedBy = performedBy;
    }
}
