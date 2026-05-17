package com.orvion.manufacturing.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.manufacturing.domain.model.enums.MachineStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "machines", indexes = {
    @Index(name = "idx_mach_tenant_status", columnList = "tenantId, status"),
    @Index(name = "idx_mach_tenant_machine_id", columnList = "tenantId, machineId", unique = true)
})
@Getter @Setter @NoArgsConstructor
public class Machine extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 50, nullable = false)
    private String machineId;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 100)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MachineStatus status;

    @Column(precision = 5, scale = 2)
    private BigDecimal oeeTarget;

    @Column
    private Instant lastMaintenanceDate;

    @Column
    private Instant nextMaintenanceDate;

    public Machine(String tenantId, String machineId, String name, String type) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.machineId = machineId;
        this.name = name;
        this.type = type;
        this.status = MachineStatus.IDLE;
    }

    public void startOperation() {
        if (status == MachineStatus.MAINTENANCE) {
            throw new BusinessException("MACHINE_MAINTENANCE", "Machine is under maintenance and cannot start");
        }
        if (status == MachineStatus.BREAKDOWN) {
            throw new BusinessException("MACHINE_BREAKDOWN", "Machine is in breakdown and cannot start");
        }
        this.status = MachineStatus.RUNNING;
    }

    public void stopOperation() {
        if (status != MachineStatus.RUNNING) {
            throw new BusinessException("MACHINE_NOT_RUNNING", "Machine is not currently running");
        }
        this.status = MachineStatus.IDLE;
    }

    public void markMaintenance() {
        this.status = MachineStatus.MAINTENANCE;
    }

    public void markBreakdown() {
        this.status = MachineStatus.BREAKDOWN;
    }

    public void completeMaintenance(Instant nextMaintenance) {
        this.status = MachineStatus.IDLE;
        this.lastMaintenanceDate = Instant.now();
        this.nextMaintenanceDate = nextMaintenance;
    }
}
