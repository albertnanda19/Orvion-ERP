package com.orvion.hcm.infrastructure.grpc.server;

import com.orvion.hcm.domain.model.Employee;
import com.orvion.hcm.domain.model.PayrollRecord;
import com.orvion.hcm.domain.repository.EmployeeRepository;
import com.orvion.hcm.domain.repository.PayrollRecordRepository;
import com.orvion.hcm.grpc.*;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class HcmGrpcServiceImpl extends HcmServiceGrpc.HcmServiceImplBase {
    private final EmployeeRepository employeeRepository;
    private final PayrollRecordRepository payrollRecordRepository;
    private final Timer employeeInfoTimer;
    private final Timer headcountTimer;
    private final Timer payrollSummaryTimer;

    public HcmGrpcServiceImpl(EmployeeRepository employeeRepository,
                              PayrollRecordRepository payrollRecordRepository,
                              MeterRegistry registry) {
        this.employeeRepository = employeeRepository;
        this.payrollRecordRepository = payrollRecordRepository;
        this.employeeInfoTimer = registry.timer("grpc.hcm.employee_info");
        this.headcountTimer = registry.timer("grpc.hcm.headcount");
        this.payrollSummaryTimer = registry.timer("grpc.hcm.payroll_summary");
    }

    @Override
    public void getEmployeeInfo(EmployeeInfoRequest request, StreamObserver<EmployeeInfoResponse> responseObserver) {
        employeeInfoTimer.record(() -> {
            try {
                Optional<Employee> opt = employeeRepository.findByTenantIdAndEmployeeId(
                    request.getTenantId(), request.getEmployeeId());
                if (opt.isEmpty()) {
                    responseObserver.onError(new RuntimeException("Employee not found: " + request.getEmployeeId()));
                    return;
                }
                Employee e = opt.get();
                EmployeeInfoResponse response = EmployeeInfoResponse.newBuilder()
                    .setEmployeeId(e.getEmployeeId())
                    .setFullName(e.getFirstName() + " " + e.getLastName())
                    .setDepartment(e.getDepartment() != null ? e.getDepartment() : "")
                    .setPosition(e.getPosition() != null ? e.getPosition() : "")
                    .setEmploymentStatus(e.getEmploymentStatus().name())
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }

    @Override
    public void getDepartmentHeadcount(HeadcountRequest request, StreamObserver<HeadcountResponse> responseObserver) {
        headcountTimer.record(() -> {
            try {
                List<Employee> employees = employeeRepository.findAllByTenantIdAndActiveTrue(request.getTenantId());
                Map<String, Integer> byDept = employees.stream()
                    .filter(e -> e.getDepartment() != null)
                    .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.summingInt(e -> 1)
                    ));
                HeadcountResponse.Builder builder = HeadcountResponse.newBuilder()
                    .setTotalHeadcount(employees.size());
                byDept.forEach(builder::putByDepartment);
                responseObserver.onNext(builder.build());
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }

    @Override
    public void getPayrollSummary(PayrollSummaryRequest request, StreamObserver<PayrollSummaryResponse> responseObserver) {
        payrollSummaryTimer.record(() -> {
            try {
                List<PayrollRecord> records = payrollRecordRepository
                    .findByTenantIdAndPeriodYearAndPeriodMonth(request.getTenantId(), request.getYear(), request.getMonth());
                BigDecimal totalGross = BigDecimal.ZERO;
                BigDecimal totalNet = BigDecimal.ZERO;
                BigDecimal totalTax = BigDecimal.ZERO;
                for (PayrollRecord r : records) {
                    if (r.getBasicSalary() != null) {
                        totalGross = totalGross.add(r.getBasicSalary().getAmount());
                    }
                    if (r.getNetPay() != null) {
                        totalNet = totalNet.add(r.getNetPay().getAmount());
                    }
                    if (r.getTaxAmount() != null) {
                        totalTax = totalTax.add(r.getTaxAmount());
                    }
                }
                PayrollSummaryResponse response = PayrollSummaryResponse.newBuilder()
                    .setEmployeeCount(records.size())
                    .setTotalGross(totalGross.toPlainString())
                    .setTotalNet(totalNet.toPlainString())
                    .setTotalTax(totalTax.toPlainString())
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });
    }
}
