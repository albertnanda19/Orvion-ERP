package com.orvion.hcm.domain.repository;

import com.orvion.hcm.domain.model.Employee;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(UUID id);
    Optional<Employee> findByTenantIdAndEmployeeId(String tenantId, String employeeId);
    List<Employee> findAllByTenantIdAndActiveTrue(String tenantId);
    List<Employee> findByTenantIdAndDepartment(String tenantId, String department);
    long countByTenantIdAndActiveTrue(String tenantId);
    long countByTenantIdAndDepartmentAndActiveTrue(String tenantId, String department);
    boolean existsByTenantIdAndEmployeeId(String tenantId, String employeeId);
    void delete(Employee employee);
}
