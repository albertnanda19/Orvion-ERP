package com.orvion.inventory.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.inventory.domain.model.enums.WarehouseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "warehouses", indexes = {
    @Index(name = "idx_wh_tenant_code", columnList = "tenantId, code", unique = true)
})
@Getter @Setter @NoArgsConstructor
public class Warehouse extends Auditable {
    @Id
    private UUID id;
    
    @Column(length = 50, nullable = false)
    private String tenantId;
    
    @Column(length = 20, nullable = false)
    private String code;
    
    @Column(length = 200, nullable = false)
    private String name;
    
    @Column(length = 500)
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private WarehouseType type = WarehouseType.MAIN;
    
    @Column(nullable = false)
    private boolean active = true;

    public Warehouse(String tenantId, String code, String name, WarehouseType type) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.code = code;
        this.name = name;
        this.type = type;
        this.active = true;
    }
}
