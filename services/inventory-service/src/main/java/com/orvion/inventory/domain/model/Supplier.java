package com.orvion.inventory.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "suppliers", indexes = {
    @Index(name = "idx_supp_tenant_code", columnList = "tenantId, code", unique = true)
})
@Getter @Setter @NoArgsConstructor
public class Supplier extends Auditable {
    @Id
    private UUID id;
    
    @Column(length = 50, nullable = false)
    private String tenantId;
    
    @Column(length = 20, nullable = false)
    private String code;
    
    @Column(length = 200, nullable = false)
    private String name;
    
    @Column(length = 100)
    private String contactEmail;
    
    @Column(length = 30)
    private String contactPhone;
    
    @Column(length = 500)
    private String address;
    
    @Column(length = 20)
    private String paymentTerms;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal performanceScore;
    
    @Column(nullable = false)
    private boolean active = true;

    public Supplier(String tenantId, String code, String name) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.code = code;
        this.name = name;
        this.active = true;
    }
}
