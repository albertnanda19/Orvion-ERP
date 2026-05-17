package com.orvion.sales.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.sales.application.dto.request.CreateSalesOrderRequest;
import com.orvion.sales.application.dto.response.SalesOrderResponse;
import com.orvion.sales.application.mapper.SalesMapper;
import com.orvion.sales.domain.model.SalesOrder;
import com.orvion.sales.domain.model.SalesOrderLine;
import com.orvion.sales.domain.model.event.SalesOrderConfirmedEvent;
import com.orvion.sales.domain.model.vo.Money;
import com.orvion.sales.domain.repository.ProcessedEventRepository;
import com.orvion.sales.domain.repository.SalesOrderRepository;
import com.orvion.sales.infrastructure.grpc.client.SalesInventoryGrpcClient;
import com.orvion.sales.infrastructure.persistence.outbox.ProcessedEvent;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SalesOrderUseCase {
    private static final Logger log = LoggerFactory.getLogger(SalesOrderUseCase.class);
    private final SalesOrderRepository salesOrderRepository;
    private final SalesMapper mapper;
    private final Counter ordersConfirmedCounter;
    private final RabbitTemplate rabbitTemplate;
    private final SalesInventoryGrpcClient inventoryGrpcClient;
    private final ProcessedEventRepository processedEventRepository;

    public SalesOrderUseCase(SalesOrderRepository salesOrderRepository, SalesMapper mapper,
                             Counter ordersConfirmedCounter, RabbitTemplate rabbitTemplate,
                             SalesInventoryGrpcClient inventoryGrpcClient,
                             ProcessedEventRepository processedEventRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.mapper = mapper;
        this.ordersConfirmedCounter = ordersConfirmedCounter;
        this.rabbitTemplate = rabbitTemplate;
        this.inventoryGrpcClient = inventoryGrpcClient;
        this.processedEventRepository = processedEventRepository;
    }

    @CacheEvict(value = "orders", allEntries = true)
    public SalesOrderResponse createOrder(String tenantId, CreateSalesOrderRequest request) {
        List<SalesOrderLine> lines = new ArrayList<>();
        for (CreateSalesOrderRequest.SalesOrderLineRequest lineReq : request.getLines()) {
            Money unitPrice = new Money(new BigDecimal(lineReq.getUnitPrice()),
                lineReq.getCurrency() != null ? lineReq.getCurrency() : "IDR");
            SalesOrderLine line = new SalesOrderLine(lineReq.getProductId(), lineReq.getProductName(),
                lineReq.getSku(), new BigDecimal(lineReq.getQuantity()), unitPrice);
            lines.add(line);
        }
        SalesOrder order = new SalesOrder(tenantId, request.getCustomerId(), request.getAssignedTo(), lines);
        order.setExpectedDelivery(request.getExpectedDelivery());
        order = salesOrderRepository.save(order);
        return mapper.toSalesOrderResponse(order);
    }

    @CacheEvict(value = "orders", allEntries = true)
    public SalesOrderResponse confirmOrder(String tenantId, UUID orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", "id", orderId.toString()));
        if (!order.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Order does not belong to tenant");

        for (SalesOrderLine line : order.getLines()) {
            try {
                boolean reserved = inventoryGrpcClient.reserveStock(line.getProductId(), line.getQuantity(), tenantId, order.getOrderNumber());
                if (reserved) {
                    line.reserve(line.getQuantity());
                } else {
                    throw new BusinessException("STOCK_UNAVAILABLE",
                        "Insufficient stock for product: " + line.getProductName());
                }
            } catch (Exception e) {
                log.error("Failed to reserve stock for product {}: {}", line.getProductId(), e.getMessage());
                throw new BusinessException("STOCK_RESERVATION_FAILED",
                    "Stock reservation failed for product: " + line.getProductName());
            }
        }

        order.confirm();
        order = salesOrderRepository.save(order);

        SalesOrderConfirmedEvent event = new SalesOrderConfirmedEvent(UUID.randomUUID(),
            order.getId(), order.getOrderNumber(), tenantId, order.getCustomerId());
        rabbitTemplate.convertAndSend("orvion.sales.exchange", "orvion.sales.order.confirmed", event);
        ordersConfirmedCounter.increment();

        return mapper.toSalesOrderResponse(order);
    }

    @CacheEvict(value = "orders", allEntries = true)
    public SalesOrderResponse shipOrder(String tenantId, UUID orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", "id", orderId.toString()));
        if (!order.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Order does not belong to tenant");
        order.ship();
        order = salesOrderRepository.save(order);
        return mapper.toSalesOrderResponse(order);
    }

    @CacheEvict(value = "orders", allEntries = true)
    public SalesOrderResponse deliverOrder(String tenantId, UUID orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", "id", orderId.toString()));
        if (!order.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Order does not belong to tenant");
        order.deliver();
        order = salesOrderRepository.save(order);
        return mapper.toSalesOrderResponse(order);
    }

    @CacheEvict(value = "orders", allEntries = true)
    public SalesOrderResponse cancelOrder(String tenantId, UUID orderId, String reason) {
        SalesOrder order = salesOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", "id", orderId.toString()));
        if (!order.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Order does not belong to tenant");
        order.cancel(reason);
        order = salesOrderRepository.save(order);
        return mapper.toSalesOrderResponse(order);
    }

    @Cacheable(value = "orders", key = "#tenantId + ':' + #orderId")
    @Transactional(readOnly = true)
    public SalesOrderResponse getOrderById(String tenantId, UUID orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("SalesOrder", "id", orderId.toString()));
        if (!order.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Order does not belong to tenant");
        return mapper.toSalesOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getOrders(String tenantId) {
        return mapper.toSalesOrderResponseList(salesOrderRepository.findAllByTenantId(tenantId));
    }
}
