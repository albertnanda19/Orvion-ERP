package com.orvion.manufacturing.domain.repository;

import com.orvion.manufacturing.domain.model.Machine;
import com.orvion.manufacturing.domain.model.enums.MachineStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MachineRepository {
    Machine save(Machine machine);
    Optional<Machine> findById(UUID id);
    Optional<Machine> findByTenantIdAndMachineId(String tenantId, String machineId);
    List<Machine> findAllByTenantId(String tenantId);
    List<Machine> findByTenantIdAndStatus(String tenantId, MachineStatus status);
    boolean existsByTenantIdAndMachineId(String tenantId, String machineId);
    void delete(Machine machine);
}
