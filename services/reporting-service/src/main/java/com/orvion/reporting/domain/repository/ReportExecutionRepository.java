package com.orvion.reporting.domain.repository;

import com.orvion.reporting.domain.model.ReportExecution;
import java.util.Optional;
import java.util.UUID;

public interface ReportExecutionRepository {
    ReportExecution save(ReportExecution execution);
    Optional<ReportExecution> findById(UUID id);
}
