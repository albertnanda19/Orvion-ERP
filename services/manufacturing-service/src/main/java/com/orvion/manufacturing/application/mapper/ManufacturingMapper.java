package com.orvion.manufacturing.application.mapper;

import com.orvion.manufacturing.application.dto.response.*;
import com.orvion.manufacturing.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ManufacturingMapper {

    @Mapping(target = "components", source = "components")
    BomResponse toBomResponse(BillOfMaterials bom);

    List<BomResponse> toBomResponseList(List<BillOfMaterials> boms);

    @Mapping(target = "componentProductId", source = "componentProductId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "wastePercentage", source = "wastePercentage")
    BomResponse.BomComponentResponse toBomComponentResponse(BomComponent component);

    List<BomResponse.BomComponentResponse> toBomComponentResponseList(List<BomComponent> components);

    @Mapping(target = "status", expression = "java(workOrder.getStatus().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    WorkOrderResponse toWorkOrderResponse(WorkOrder workOrder);

    List<WorkOrderResponse> toWorkOrderResponseList(List<WorkOrder> workOrders);

    @Mapping(target = "status", expression = "java(machine.getStatus().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    MachineResponse toMachineResponse(Machine machine);

    List<MachineResponse> toMachineResponseList(List<Machine> machines);

    @Mapping(target = "status", expression = "java(inspection.getStatus().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    QualityInspectionResponse toQualityInspectionResponse(QualityInspection inspection);

    List<QualityInspectionResponse> toQualityInspectionResponseList(List<QualityInspection> inspections);
}
