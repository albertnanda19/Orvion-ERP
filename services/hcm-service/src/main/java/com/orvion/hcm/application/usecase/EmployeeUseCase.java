package com.orvion.hcm.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.hcm.application.dto.request.CreateEmployeeRequest;
import com.orvion.hcm.application.dto.request.UpdateEmployeeRequest;
import com.orvion.hcm.application.dto.response.EmployeeResponse;
import com.orvion.hcm.application.mapper.HcmMapper;
import com.orvion.hcm.domain.model.Employee;
import com.orvion.hcm.domain.model.enums.EmploymentType;
import com.orvion.hcm.domain.model.vo.Money;
import com.orvion.hcm.domain.repository.EmployeeRepository;
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
public class EmployeeUseCase {
    private final EmployeeRepository employeeRepository;
    private final HcmMapper mapper;

    public EmployeeUseCase(EmployeeRepository employeeRepository, HcmMapper mapper) {
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponse createEmployee(String tenantId, CreateEmployeeRequest request) {
        EmploymentType type;
        try {
            type = EmploymentType.valueOf(request.getEmploymentType());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("INVALID_EMPLOYMENT_TYPE", "Invalid employment type: " + request.getEmploymentType());
        }
        Employee employee = new Employee(tenantId, request.getFirstName(), request.getLastName(),
            request.getEmail(), type, request.getDepartment(), request.getPosition());
        employee.setPhone(request.getPhone());
        employee.setNationalId(request.getNationalId());
        employee.setGrade(request.getGrade());
        employee.setManagerId(request.getManagerId());
        employee.setBankAccount(request.getBankAccount());
        if (request.getBasicSalary() != null) {
            employee.setBasicSalary(new Money(request.getBasicSalary(),
                request.getSalaryCurrency() != null ? request.getSalaryCurrency() : "IDR"));
        }
        if (request.getAllowances() != null) {
            employee.setAllowances(request.getAllowances());
        }
        employee = employeeRepository.save(employee);
        return mapper.toEmployeeResponse(employee);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponse updateEmployee(String tenantId, UUID employeeId, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId.toString()));
        if (!employee.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Employee does not belong to tenant");
        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getEmail() != null) employee.setEmail(request.getEmail());
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if (request.getPosition() != null) employee.setPosition(request.getPosition());
        if (request.getGrade() != null) employee.setGrade(request.getGrade());
        if (request.getManagerId() != null) employee.setManagerId(request.getManagerId());
        if (request.getBankAccount() != null) employee.setBankAccount(request.getBankAccount());
        if (request.getBasicSalary() != null) {
            employee.setBasicSalary(new Money(request.getBasicSalary(),
                request.getSalaryCurrency() != null ? request.getSalaryCurrency() : "IDR"));
        }
        if (request.getAllowances() != null) employee.setAllowances(request.getAllowances());
        employee = employeeRepository.save(employee);
        return mapper.toEmployeeResponse(employee);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public void terminateEmployee(String tenantId, UUID employeeId, String reason) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId.toString()));
        if (!employee.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Employee does not belong to tenant");
        employee.terminate(reason, Instant.now());
        employeeRepository.save(employee);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public void promoteEmployee(String tenantId, UUID employeeId, String newPosition, BigDecimal newSalary, String currency) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId.toString()));
        if (!employee.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Employee does not belong to tenant");
        Money salary = newSalary != null ? new Money(newSalary, currency != null ? currency : "IDR") : null;
        employee.promote(newPosition, salary);
        employeeRepository.save(employee);
    }

    @Cacheable(value = "employees", key = "#tenantId + ':' + #employeeId")
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(String tenantId, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId.toString()));
        if (!employee.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Employee does not belong to tenant");
        return mapper.toEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployees(String tenantId) {
        return mapper.toEmployeeResponseList(employeeRepository.findAllByTenantIdAndActiveTrue(tenantId));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(String tenantId, String department) {
        return mapper.toEmployeeResponseList(employeeRepository.findByTenantIdAndDepartment(tenantId, department));
    }
}
