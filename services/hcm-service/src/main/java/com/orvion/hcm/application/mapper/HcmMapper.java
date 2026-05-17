package com.orvion.hcm.application.mapper;

import com.orvion.hcm.application.dto.response.*;
import com.orvion.hcm.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = {java.math.BigDecimal.class})
public interface HcmMapper {

    @Mapping(target = "employmentType", expression = "java(employee.getEmploymentType().name())")
    @Mapping(target = "employmentStatus", expression = "java(employee.getEmploymentStatus().name())")
    @Mapping(target = "basicSalary", expression = "java(employee.getBasicSalary() != null ? employee.getBasicSalary().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "salaryCurrency", expression = "java(employee.getBasicSalary() != null ? employee.getBasicSalary().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "createdAt", source = "createdAt")
    EmployeeResponse toEmployeeResponse(Employee employee);

    List<EmployeeResponse> toEmployeeResponseList(List<Employee> employees);

    @Mapping(target = "basicSalary", expression = "java(record.getBasicSalary() != null ? record.getBasicSalary().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "currency", expression = "java(record.getBasicSalary() != null ? record.getBasicSalary().getCurrencyCode() : \"IDR\")")
    @Mapping(target = "netPay", expression = "java(record.getNetPay() != null ? record.getNetPay().getAmount() : BigDecimal.ZERO)")
    @Mapping(target = "status", expression = "java(record.getStatus().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    PayrollResponse toPayrollResponse(PayrollRecord record);

    List<PayrollResponse> toPayrollResponseList(List<PayrollRecord> records);

    @Mapping(target = "leaveType", expression = "java(request.getLeaveType().name())")
    @Mapping(target = "status", expression = "java(request.getStatus().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    LeaveRequestResponse toLeaveRequestResponse(LeaveRequest request);

    List<LeaveRequestResponse> toLeaveRequestResponseList(List<LeaveRequest> requests);

    @Mapping(target = "leaveType", expression = "java(balance.getLeaveType().name())")
    @Mapping(target = "remainingDays", expression = "java(balance.getRemainingDays())")
    LeaveBalanceResponse toLeaveBalanceResponse(LeaveBalance balance);

    AttendanceResponse toAttendanceResponse(Attendance attendance);

    List<AttendanceResponse> toAttendanceResponseList(List<Attendance> attendances);

    @Mapping(target = "status", expression = "java(review.getStatus().name())")
    @Mapping(target = "createdAt", source = "createdAt")
    PerformanceReviewResponse toPerformanceReviewResponse(PerformanceReview review);

    List<PerformanceReviewResponse> toPerformanceReviewResponseList(List<PerformanceReview> reviews);
}
