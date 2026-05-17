package com.orvion.hcm.application.usecase;

import com.orvion.common.exception.BusinessException;
import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.hcm.application.dto.request.InitiatePayrollRequest;
import com.orvion.hcm.application.dto.response.PayrollResponse;
import com.orvion.hcm.application.mapper.HcmMapper;
import com.orvion.hcm.domain.model.Employee;
import com.orvion.hcm.domain.model.PayrollRecord;
import com.orvion.hcm.domain.model.enums.FiscalPeriod;
import com.orvion.hcm.domain.model.vo.Money;
import com.orvion.hcm.domain.repository.EmployeeRepository;
import com.orvion.hcm.domain.repository.PayrollRecordRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PayrollUseCase {
    private final PayrollRecordRepository payrollRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final HcmMapper mapper;

    public PayrollUseCase(PayrollRecordRepository payrollRecordRepository,
                          EmployeeRepository employeeRepository,
                          HcmMapper mapper) {
        this.payrollRecordRepository = payrollRecordRepository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @CacheEvict(value = "payroll", allEntries = true)
    public PayrollResponse initiatePayroll(String tenantId, InitiatePayrollRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId().toString()));
        if (!employee.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Employee does not belong to tenant");

        FiscalPeriod period = new FiscalPeriod(request.getPeriodYear(), request.getPeriodMonth());
        Money salary = request.getBasicSalary() != null
            ? new Money(request.getBasicSalary(), request.getCurrency() != null ? request.getCurrency() : "IDR")
            : (employee.getBasicSalary() != null ? employee.getBasicSalary() : Money.zero("IDR"));

        PayrollRecord record = new PayrollRecord(tenantId, request.getEmployeeId(), period, salary);
        record.setAllowances(request.getAllowances());
        record.setOvertime(request.getOvertime());
        record.setDeductions(request.getDeductions());
        record.setTaxAmount(request.getTaxAmount());
        record.calculate();
        record = payrollRecordRepository.save(record);
        return mapper.toPayrollResponse(record);
    }

    @CacheEvict(value = "payroll", allEntries = true)
    public PayrollResponse approvePayroll(String tenantId, UUID payrollId) {
        PayrollRecord record = payrollRecordRepository.findById(payrollId)
            .orElseThrow(() -> new ResourceNotFoundException("PayrollRecord", "id", payrollId.toString()));
        if (!record.getTenantId().equals(tenantId))
            throw new BusinessException("TENANT_MISMATCH", "Payroll record does not belong to tenant");
        record.markPaid();
        record = payrollRecordRepository.save(record);
        return mapper.toPayrollResponse(record);
    }

    @Transactional(readOnly = true)
    public List<PayrollResponse> getPayrollByPeriod(String tenantId, int year, int month) {
        return mapper.toPayrollResponseList(
            payrollRecordRepository.findByTenantIdAndPeriodYearAndPeriodMonth(tenantId, year, month));
    }

    @Transactional(readOnly = true)
    public List<PayrollResponse> getPayrollByEmployee(String tenantId, UUID employeeId) {
        return mapper.toPayrollResponseList(
            payrollRecordRepository.findByTenantIdAndEmployeeId(tenantId, employeeId));
    }
}
