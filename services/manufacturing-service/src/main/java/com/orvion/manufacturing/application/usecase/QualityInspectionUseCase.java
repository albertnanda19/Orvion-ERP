package com.orvion.manufacturing.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.manufacturing.application.dto.response.QualityInspectionResponse;
import com.orvion.manufacturing.application.mapper.ManufacturingMapper;
import com.orvion.manufacturing.domain.event.QualityInspectionCompletedEvent;
import com.orvion.manufacturing.domain.model.QualityInspection;
import com.orvion.manufacturing.domain.model.enums.QualityStatus;
import com.orvion.manufacturing.domain.repository.ProcessedEventRepository;
import com.orvion.manufacturing.domain.repository.QualityInspectionRepository;
import com.orvion.manufacturing.infrastructure.persistence.outbox.ProcessedEvent;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.orvion.manufacturing.infrastructure.config.ManufacturingRabbitConfig.EXCHANGE;
import static com.orvion.manufacturing.infrastructure.config.ManufacturingRabbitConfig.QUALITY_INSPECTION_COMPLETED_QUEUE;

@Service
@Transactional
public class QualityInspectionUseCase {
    private static final Logger log = LoggerFactory.getLogger(QualityInspectionUseCase.class);

    private final QualityInspectionRepository inspectionRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ManufacturingMapper mapper;
    private final RabbitTemplate rabbitTemplate;
    private final Counter inspectionsCompletedCounter;

    public QualityInspectionUseCase(QualityInspectionRepository inspectionRepository,
                                    ProcessedEventRepository processedEventRepository,
                                    ManufacturingMapper mapper,
                                    RabbitTemplate rabbitTemplate,
                                    Counter inspectionsCompletedCounter) {
        this.inspectionRepository = inspectionRepository;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
        this.inspectionsCompletedCounter = inspectionsCompletedCounter;
    }

    public QualityInspectionResponse createInspection(String tenantId, String workOrderId, String inspectedBy,
                                                       BigDecimal passedQty, BigDecimal failedQty, String defectReasons) {
        QualityInspection inspection = new QualityInspection(tenantId, workOrderId, inspectedBy,
            passedQty, failedQty, defectReasons);
        inspection = inspectionRepository.save(inspection);
        return mapper.toQualityInspectionResponse(inspection);
    }

    public QualityInspectionResponse completeInspection(String tenantId, UUID inspectionId) {
        QualityInspection inspection = inspectionRepository.findById(inspectionId)
            .orElseThrow(() -> new ResourceNotFoundException("QualityInspection", "id", inspectionId.toString()));
        if (!inspection.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Inspection does not belong to tenant");

        inspection.complete();
        inspection = inspectionRepository.save(inspection);
        inspectionsCompletedCounter.increment();

        publishQualityInspectionCompleted(inspection);
        return mapper.toQualityInspectionResponse(inspection);
    }

    private void publishQualityInspectionCompleted(QualityInspection inspection) {
        try {
            QualityInspectionCompletedEvent event = new QualityInspectionCompletedEvent(
                inspection.getId().toString(), inspection.getWorkOrderId(),
                inspection.getStatus().name(), inspection.getPassedQuantity(),
                inspection.getFailedQuantity(), inspection.getTenantId());

            if (!processedEventRepository.existsByEventId(event.getEventId())) {
                rabbitTemplate.convertAndSend(EXCHANGE, QUALITY_INSPECTION_COMPLETED_QUEUE, event);
                processedEventRepository.save(new ProcessedEvent(event.getEventId(), event.getEventType()));
            }
        } catch (Exception e) {
            log.error("Failed to publish QualityInspectionCompletedEvent for inspectionId={}", inspection.getId(), e);
        }
    }

    @Transactional(readOnly = true)
    public QualityInspectionResponse getInspectionById(String tenantId, UUID inspectionId) {
        QualityInspection inspection = inspectionRepository.findById(inspectionId)
            .orElseThrow(() -> new ResourceNotFoundException("QualityInspection", "id", inspectionId.toString()));
        if (!inspection.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Inspection does not belong to tenant");
        return mapper.toQualityInspectionResponse(inspection);
    }

    @Transactional(readOnly = true)
    public List<QualityInspectionResponse> getInspectionsByWorkOrder(String tenantId, String workOrderId) {
        return mapper.toQualityInspectionResponseList(
            inspectionRepository.findByTenantIdAndWorkOrderId(tenantId, workOrderId));
    }

    @Transactional(readOnly = true)
    public List<QualityInspectionResponse> getInspectionsByStatus(String tenantId, String status) {
        return mapper.toQualityInspectionResponseList(
            inspectionRepository.findByTenantIdAndStatus(tenantId, QualityStatus.valueOf(status)));
    }
}
