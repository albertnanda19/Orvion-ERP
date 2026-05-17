package com.orvion.manufacturing.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QualityInspectionResponse {
    private UUID id;
    private String workOrderId;
    private String inspectedBy;
    private Instant inspectionDate;
    private BigDecimal passedQuantity;
    private BigDecimal failedQuantity;
    private String defectReasons;
    private String status;
    private Instant createdAt;
}
