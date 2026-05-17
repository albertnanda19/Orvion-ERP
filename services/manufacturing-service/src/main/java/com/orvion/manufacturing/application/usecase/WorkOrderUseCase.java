package com.orvion.manufacturing.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.manufacturing.application.dto.request.CreateWorkOrderRequest;
import com.orvion.manufacturing.application.dto.response.WorkOrderResponse;
import com.orvion.manufacturing.application.mapper.ManufacturingMapper;
import com.orvion.manufacturing.domain.event.WorkOrderCompletedEvent;
import com.orvion.manufacturing.domain.model.WorkOrder;
import com.orvion.manufacturing.domain.model.enums.WorkOrderStatus;
import com.orvion.manufacturing.domain.repository.ProcessedEventRepository;
import com.orvion.manufacturing.domain.repository.WorkOrderRepository;
import com.orvion.manufacturing.infrastructure.grpc.client.MfgInventoryGrpcClient;
import com.orvion.manufacturing.infrastructure.persistence.outbox.ProcessedEvent;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.orvion.manufacturing.infrastructure.config.ManufacturingRabbitConfig.EXCHANGE;
import static com.orvion.manufacturing.infrastructure.config.ManufacturingRabbitConfig.WORK_ORDER_COMPLETED_QUEUE;

@Service
@Transactional
public class WorkOrderUseCase {
    private static final Logger log = LoggerFactory.getLogger(WorkOrderUseCase.class);
    private static final AtomicInteger WO_COUNTER = new AtomicInteger(1);

    private final WorkOrderRepository workOrderRepository;
    private final MfgInventoryGrpcClient inventoryGrpcClient;
    private final ProcessedEventRepository processedEventRepository;
    private final ManufacturingMapper mapper;
    private final RabbitTemplate rabbitTemplate;
    private final Counter workOrdersCreatedCounter;
    private final Counter workOrdersCompletedCounter;

    public WorkOrderUseCase(WorkOrderRepository workOrderRepository,
                            MfgInventoryGrpcClient inventoryGrpcClient,
                            ProcessedEventRepository processedEventRepository,
                            ManufacturingMapper mapper,
                            RabbitTemplate rabbitTemplate,
                            Counter workOrdersCreatedCounter,
                            Counter workOrdersCompletedCounter) {
        this.workOrderRepository = workOrderRepository;
        this.inventoryGrpcClient = inventoryGrpcClient;
        this.processedEventRepository = processedEventRepository;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
        this.workOrdersCreatedCounter = workOrdersCreatedCounter;
        this.workOrdersCompletedCounter = workOrdersCompletedCounter;
    }

    @CacheEvict(value = "workOrders", allEntries = true)
    public WorkOrderResponse createWorkOrder(String tenantId, CreateWorkOrderRequest request) {
        String orderNumber = generateOrderNumber();
        WorkOrder workOrder = new WorkOrder(tenantId, orderNumber, request.getProductId(),
            request.getPlannedQuantity(), request.getBomId(),
            request.getPlannedStart(), request.getPlannedEnd(), request.getWarehouseId());
        workOrder = workOrderRepository.save(workOrder);
        workOrdersCreatedCounter.increment();
        return mapper.toWorkOrderResponse(workOrder);
    }

    @CacheEvict(value = "workOrders", allEntries = true)
    public WorkOrderResponse startWorkOrder(String tenantId, UUID workOrderId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", "id", workOrderId.toString()));
        if (!workOrder.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Work order does not belong to tenant");

        workOrder.start();

        workOrder = workOrderRepository.save(workOrder);
        return mapper.toWorkOrderResponse(workOrder);
    }

    @CacheEvict(value = "workOrders", allEntries = true)
    public WorkOrderResponse completeWorkOrder(String tenantId, UUID workOrderId, BigDecimal actualQty) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", "id", workOrderId.toString()));
        if (!workOrder.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Work order does not belong to tenant");

        workOrder.complete(actualQty);

        boolean reserved = inventoryGrpcClient.reserveStock(workOrder.getProductId(),
            actualQty, workOrder.getWarehouseId(), tenantId, workOrder.getOrderNumber());

        workOrder = workOrderRepository.save(workOrder);

        if (reserved) {
            publishWorkOrderCompleted(workOrder);
        }

        workOrdersCompletedCounter.increment();
        return mapper.toWorkOrderResponse(workOrder);
    }

    private void publishWorkOrderCompleted(WorkOrder workOrder) {
        try {
            WorkOrderCompletedEvent event = new WorkOrderCompletedEvent(
                workOrder.getId().toString(), workOrder.getProductId(),
                workOrder.getActualQuantity(), workOrder.getWarehouseId(), workOrder.getTenantId());

            if (!processedEventRepository.existsByEventId(event.getEventId())) {
                rabbitTemplate.convertAndSend(EXCHANGE, WORK_ORDER_COMPLETED_QUEUE, event);
                processedEventRepository.save(new ProcessedEvent(event.getEventId(), event.getEventType()));
            }
        } catch (Exception e) {
            log.error("Failed to publish WorkOrderCompletedEvent for workOrderId={}", workOrder.getId(), e);
        }
    }

    @CacheEvict(value = "workOrders", allEntries = true)
    public WorkOrderResponse cancelWorkOrder(String tenantId, UUID workOrderId, String reason) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", "id", workOrderId.toString()));
        if (!workOrder.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Work order does not belong to tenant");
        workOrder.cancel(reason);
        workOrder = workOrderRepository.save(workOrder);
        return mapper.toWorkOrderResponse(workOrder);
    }

    @CacheEvict(value = "workOrders", allEntries = true)
    public WorkOrderResponse reportProgress(String tenantId, UUID workOrderId, BigDecimal completedQty) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", "id", workOrderId.toString()));
        if (!workOrder.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Work order does not belong to tenant");
        workOrder.reportProgress(completedQty);
        workOrder = workOrderRepository.save(workOrder);
        return mapper.toWorkOrderResponse(workOrder);
    }

    @Cacheable(value = "workOrders", key = "#tenantId + ':' + #workOrderId")
    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderById(String tenantId, UUID workOrderId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", "id", workOrderId.toString()));
        if (!workOrder.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Work order does not belong to tenant");
        return mapper.toWorkOrderResponse(workOrder);
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getWorkOrders(String tenantId) {
        return mapper.toWorkOrderResponseList(workOrderRepository.findAllByTenantId(tenantId));
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getWorkOrdersByStatus(String tenantId, String status) {
        return mapper.toWorkOrderResponseList(
            workOrderRepository.findByTenantIdAndStatus(tenantId, WorkOrderStatus.valueOf(status)));
    }

    private synchronized String generateOrderNumber() {
        int seq = WO_COUNTER.getAndIncrement();
        if (WO_COUNTER.get() > 9999) WO_COUNTER.set(1);
        return String.format("WO-%04d", seq);
    }
}
