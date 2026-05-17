package com.orvion.manufacturing.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "production_schedules", indexes = {
    @Index(name = "idx_ps_tenant_date", columnList = "tenantId, date")
})
@Getter @Setter @NoArgsConstructor
public class ProductionSchedule extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private LocalDate date;

    @ElementCollection
    @CollectionTable(name = "schedule_work_orders", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "work_order_id", length = 50)
    private List<String> workOrderIds;

    @ElementCollection
    @CollectionTable(name = "schedule_machine_allocations", joinColumns = @JoinColumn(name = "schedule_id"))
    @MapKeyColumn(name = "machine_id", length = 50)
    @Column(name = "work_order_id", length = 50)
    private Map<String, String> machineAllocations;

    public ProductionSchedule(String tenantId, LocalDate date) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.date = date;
    }
}
