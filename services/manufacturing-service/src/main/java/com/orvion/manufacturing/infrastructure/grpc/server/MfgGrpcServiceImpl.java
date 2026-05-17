package com.orvion.manufacturing.infrastructure.grpc.server;

import com.orvion.manufacturing.domain.model.Machine;
import com.orvion.manufacturing.domain.model.WorkOrder;
import com.orvion.manufacturing.domain.model.enums.MachineStatus;
import com.orvion.manufacturing.domain.repository.MachineRepository;
import com.orvion.manufacturing.domain.repository.WorkOrderRepository;
import com.orvion.manufacturing.grpc.*;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@GrpcService
public class MfgGrpcServiceImpl extends ManufacturingServiceGrpc.ManufacturingServiceImplBase {
    private final WorkOrderRepository workOrderRepository;
    private final MachineRepository machineRepository;
    private final Timer workOrderStatusTimer;
    private final Timer machineUtilizationTimer;

    public MfgGrpcServiceImpl(WorkOrderRepository workOrderRepository, MachineRepository machineRepository,
                              MeterRegistry registry) {
        this.workOrderRepository = workOrderRepository;
        this.machineRepository = machineRepository;
        this.workOrderStatusTimer = registry.timer("grpc.manufacturing.work_order_status");
        this.machineUtilizationTimer = registry.timer("grpc.manufacturing.machine_utilization");
    }

    @Override
    public void getWorkOrderStatus(WorkOrderStatusRequest request, StreamObserver<WorkOrderStatusResponse> responseObserver) {
        workOrderStatusTimer.record(() -> {
            try {
                UUID woId = UUID.fromString(request.getWorkOrderId());
                Optional<WorkOrder> opt = workOrderRepository.findById(woId);
                if (opt.isEmpty()) {
                    responseObserver.onError(new RuntimeException("Work order not found: " + request.getWorkOrderId()));
                    return;
                }
                WorkOrder wo = opt.get();
                WorkOrderStatusResponse response = WorkOrderStatusResponse.newBuilder()
                    .setWorkOrderId(wo.getId().toString())
                    .setStatus(wo.getStatus().name())
                    .setProductId(wo.getProductId())
                    .setPlannedQuantity(wo.getPlannedQuantity().toPlainString())
                    .setActualQuantity(wo.getActualQuantity() != null ? wo.getActualQuantity().toPlainString() : "0")
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }

    @Override
    public void getMachineUtilization(MachineUtilizationRequest request, StreamObserver<MachineUtilizationResponse> responseObserver) {
        machineUtilizationTimer.record(() -> {
            try {
                List<Machine> machines = machineRepository.findAllByTenantId(request.getTenantId());
                int running = 0, idle = 0, maintenance = 0;
                for (Machine m : machines) {
                    switch (m.getStatus()) {
                        case RUNNING -> running++;
                        case IDLE -> idle++;
                        case MAINTENANCE -> maintenance++;
                        case BREAKDOWN -> maintenance++;
                    }
                }
                MachineUtilizationResponse response = MachineUtilizationResponse.newBuilder()
                    .setTotalMachines(machines.size())
                    .setRunning(running)
                    .setIdle(idle)
                    .setMaintenance(maintenance)
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }
}
