package com.orvion.manufacturing.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bill_of_materials", indexes = {
    @Index(name = "idx_bom_tenant_product", columnList = "tenantId, productId"),
    @Index(name = "idx_bom_tenant_active", columnList = "tenantId, active")
})
@Getter @Setter @NoArgsConstructor
public class BillOfMaterials extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 50, nullable = false)
    private String productId;

    @Column(nullable = false)
    private int version;

    @OneToMany(mappedBy = "billOfMaterials", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BomComponent> components = new ArrayList<>();

    @Column(nullable = false)
    private Instant effectiveDate;

    @Column(nullable = false)
    private boolean active = true;

    public BillOfMaterials(String tenantId, String productId, int version) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.productId = productId;
        this.version = version;
        this.effectiveDate = Instant.now();
        this.active = true;
    }

    public void addComponent(String componentProductId, BigDecimal quantity, String unit, BigDecimal wastePercentage) {
        BomComponent component = new BomComponent(this, componentProductId, quantity, unit, wastePercentage);
        components.add(component);
    }

    public void deactivate() {
        this.active = false;
    }
}
