package com.orvion.manufacturing.infrastructure.persistence.jpa;

import com.orvion.manufacturing.domain.model.ProductionSchedule;
import com.orvion.manufacturing.domain.repository.ProductionScheduleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionScheduleJpaRepository extends JpaRepository<ProductionSchedule, UUID>, ProductionScheduleRepository {
    @Override
    List<ProductionSchedule> findAllByTenantId(String tenantId);

    @Override
    Optional<ProductionSchedule> findByTenantIdAndDate(String tenantId, LocalDate date);

    @Override
    List<ProductionSchedule> findByTenantIdAndDateBetween(String tenantId, LocalDate from, LocalDate to);
}
