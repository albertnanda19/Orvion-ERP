package com.orvion.manufacturing.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.manufacturing.application.dto.response.MachineResponse;
import com.orvion.manufacturing.application.mapper.ManufacturingMapper;
import com.orvion.manufacturing.domain.model.Machine;
import com.orvion.manufacturing.domain.model.enums.MachineStatus;
import com.orvion.manufacturing.domain.repository.MachineRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MachineUseCase {
    private final MachineRepository machineRepository;
    private final ManufacturingMapper mapper;

    public MachineUseCase(MachineRepository machineRepository, ManufacturingMapper mapper) {
        this.machineRepository = machineRepository;
        this.mapper = mapper;
    }

    @CacheEvict(value = "machines", allEntries = true)
    public MachineResponse registerMachine(String tenantId, String machineId, String name, String type) {
        if (machineRepository.existsByTenantIdAndMachineId(tenantId, machineId))
            throw new BusinessException("DUPLICATE_MACHINE", "Machine with ID " + machineId + " already exists");
        Machine machine = new Machine(tenantId, machineId, name, type);
        machine = machineRepository.save(machine);
        return mapper.toMachineResponse(machine);
    }

    @CacheEvict(value = "machines", allEntries = true)
    public MachineResponse startMachine(String tenantId, UUID machineId) {
        Machine machine = findMachine(tenantId, machineId);
        machine.startOperation();
        machine = machineRepository.save(machine);
        return mapper.toMachineResponse(machine);
    }

    @CacheEvict(value = "machines", allEntries = true)
    public MachineResponse stopMachine(String tenantId, UUID machineId) {
        Machine machine = findMachine(tenantId, machineId);
        machine.stopOperation();
        machine = machineRepository.save(machine);
        return mapper.toMachineResponse(machine);
    }

    @CacheEvict(value = "machines", allEntries = true)
    public MachineResponse markMaintenance(String tenantId, UUID machineId) {
        Machine machine = findMachine(tenantId, machineId);
        machine.markMaintenance();
        machine = machineRepository.save(machine);
        return mapper.toMachineResponse(machine);
    }

    @CacheEvict(value = "machines", allEntries = true)
    public MachineResponse completeMaintenance(String tenantId, UUID machineId, Instant nextMaintenance) {
        Machine machine = findMachine(tenantId, machineId);
        machine.completeMaintenance(nextMaintenance);
        machine = machineRepository.save(machine);
        return mapper.toMachineResponse(machine);
    }

    @CacheEvict(value = "machines", allEntries = true)
    public MachineResponse markBreakdown(String tenantId, UUID machineId) {
        Machine machine = findMachine(tenantId, machineId);
        machine.markBreakdown();
        machine = machineRepository.save(machine);
        return mapper.toMachineResponse(machine);
    }

    @CacheEvict(value = "machines", allEntries = true)
    public MachineResponse updateOeeTarget(String tenantId, UUID machineId, BigDecimal oeeTarget) {
        Machine machine = findMachine(tenantId, machineId);
        machine.setOeeTarget(oeeTarget);
        machine = machineRepository.save(machine);
        return mapper.toMachineResponse(machine);
    }

    @Cacheable(value = "machines", key = "#tenantId + ':' + #machineId")
    @Transactional(readOnly = true)
    public MachineResponse getMachineById(String tenantId, UUID machineId) {
        Machine machine = machineRepository.findById(machineId)
            .orElseThrow(() -> new ResourceNotFoundException("Machine", "id", machineId.toString()));
        if (!machine.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Machine does not belong to tenant");
        return mapper.toMachineResponse(machine);
    }

    @Cacheable(value = "machines", key = "#tenantId + ':all'")
    @Transactional(readOnly = true)
    public List<MachineResponse> getMachines(String tenantId) {
        return mapper.toMachineResponseList(machineRepository.findAllByTenantId(tenantId));
    }

    @Transactional(readOnly = true)
    public List<MachineResponse> getMachinesByStatus(String tenantId, String status) {
        return mapper.toMachineResponseList(
            machineRepository.findByTenantIdAndStatus(tenantId, MachineStatus.valueOf(status)));
    }

    private Machine findMachine(String tenantId, UUID machineId) {
        Machine machine = machineRepository.findById(machineId)
            .orElseThrow(() -> new ResourceNotFoundException("Machine", "id", machineId.toString()));
        if (!machine.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Machine does not belong to tenant");
        return machine;
    }
}
