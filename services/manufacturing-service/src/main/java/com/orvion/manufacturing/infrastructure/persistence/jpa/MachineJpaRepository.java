package com.orvion.manufacturing.infrastructure.persistence.jpa;

import com.orvion.manufacturing.domain.model.Machine;
import com.orvion.manufacturing.domain.model.enums.MachineStatus;
import com.orvion.manufacturing.domain.repository.MachineRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MachineJpaRepository extends JpaRepository<Machine, UUID>, MachineRepository {
    @Override
    Optional<Machine> findByTenantIdAndMachineId(String tenantId, String machineId);

    @Override
    List<Machine> findAllByTenantId(String tenantId);

    @Override
    List<Machine> findByTenantIdAndStatus(String tenantId, MachineStatus status);

    @Override
    boolean existsByTenantIdAndMachineId(String tenantId, String machineId);
}
