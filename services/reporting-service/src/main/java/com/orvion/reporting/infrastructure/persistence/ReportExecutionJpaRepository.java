package com.orvion.reporting.infrastructure.persistence;

import com.orvion.reporting.domain.model.ReportExecution;
import com.orvion.reporting.domain.repository.ReportExecutionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ReportExecutionJpaRepository extends JpaRepository<ReportExecution, UUID>, ReportExecutionRepository {
}
