package com.orvion.manufacturing.domain.repository;

import com.orvion.manufacturing.domain.model.ProductionSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductionScheduleRepository {
    ProductionSchedule save(ProductionSchedule schedule);
    Optional<ProductionSchedule> findById(UUID id);
    List<ProductionSchedule> findAllByTenantId(String tenantId);
    Optional<ProductionSchedule> findByTenantIdAndDate(String tenantId, LocalDate date);
    List<ProductionSchedule> findByTenantIdAndDateBetween(String tenantId, LocalDate from, LocalDate to);
    void delete(ProductionSchedule schedule);
}
