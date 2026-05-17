package com.orvion.manufacturing.domain.model;

import com.orvion.common.audit.Auditable;
import com.orvion.common.exception.BusinessException;
import com.orvion.manufacturing.domain.model.enums.QualityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "quality_inspections", indexes = {
    @Index(name = "idx_qi_tenant_work_order", columnList = "tenantId, workOrderId"),
    @Index(name = "idx_qi_tenant_status", columnList = "tenantId, status")
})
@Getter @Setter @NoArgsConstructor
public class QualityInspection extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(length = 50, nullable = false)
    private String workOrderId;

    @Column(length = 100, nullable = false)
    private String inspectedBy;

    @Column(nullable = false)
    private Instant inspectionDate;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal passedQuantity;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal failedQuantity;

    @Column(length = 1000)
    private String defectReasons;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private QualityStatus status;

    public QualityInspection(String tenantId, String workOrderId, String inspectedBy,
                             BigDecimal passedQuantity, BigDecimal failedQuantity, String defectReasons) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.workOrderId = workOrderId;
        this.inspectedBy = inspectedBy;
        this.inspectionDate = Instant.now();
        this.passedQuantity = passedQuantity;
        this.failedQuantity = failedQuantity;
        this.defectReasons = defectReasons;
        this.status = QualityStatus.PENDING;
    }

    public void complete() {
        if (status != QualityStatus.PENDING) {
            throw new BusinessException("INVALID_STATUS", "Inspection is already completed");
        }
        if (failedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            this.status = QualityStatus.PASSED;
        } else if (passedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            this.status = QualityStatus.FAILED;
        } else {
            this.status = QualityStatus.PARTIAL_PASS;
        }
    }
}
