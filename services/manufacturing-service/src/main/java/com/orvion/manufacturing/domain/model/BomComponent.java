package com.orvion.manufacturing.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bom_components")
@Getter @Setter @NoArgsConstructor
public class BomComponent {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    @JsonIgnore
    private BillOfMaterials billOfMaterials;

    @Column(length = 50, nullable = false)
    private String componentProductId;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(length = 20, nullable = false)
    private String unit;

    @Column(precision = 5, scale = 2)
    private BigDecimal wastePercentage = BigDecimal.ZERO;

    public BomComponent(BillOfMaterials billOfMaterials, String componentProductId, BigDecimal quantity, String unit, BigDecimal wastePercentage) {
        this.id = UUID.randomUUID();
        this.billOfMaterials = billOfMaterials;
        this.componentProductId = componentProductId;
        this.quantity = quantity;
        this.unit = unit;
        this.wastePercentage = wastePercentage != null ? wastePercentage : BigDecimal.ZERO;
    }
}
