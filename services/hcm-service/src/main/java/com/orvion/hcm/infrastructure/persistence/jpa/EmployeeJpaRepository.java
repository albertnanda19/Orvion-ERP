package com.orvion.hcm.infrastructure.persistence.jpa;

import com.orvion.hcm.domain.model.Employee;
import com.orvion.hcm.domain.repository.EmployeeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeJpaRepository extends JpaRepository<Employee, UUID>, EmployeeRepository {
    @Override
    Optional<Employee> findByTenantIdAndEmployeeId(String tenantId, String employeeId);

    @Override
    List<Employee> findAllByTenantIdAndActiveTrue(String tenantId);

    @Override
    List<Employee> findByTenantIdAndDepartment(String tenantId, String department);

    @Override
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.tenantId = :tenantId AND e.active = true")
    long countByTenantIdAndActiveTrue(@Param("tenantId") String tenantId);

    @Override
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.tenantId = :tenantId AND e.department = :department AND e.active = true")
    long countByTenantIdAndDepartmentAndActiveTrue(@Param("tenantId") String tenantId, @Param("department") String department);

    @Override
    boolean existsByTenantIdAndEmployeeId(String tenantId, String employeeId);
}
