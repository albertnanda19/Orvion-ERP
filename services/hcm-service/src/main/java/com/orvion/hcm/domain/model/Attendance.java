package com.orvion.hcm.domain.model;

import com.orvion.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "date"})
})
@Getter @Setter @NoArgsConstructor
public class Attendance extends Auditable {
    @Id
    private UUID id;

    @Column(length = 50, nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime clockIn;

    private LocalTime clockOut;

    private Duration workingHours;

    @Column(nullable = false)
    private boolean isLate;

    @Column(nullable = false)
    private boolean isOvertime;

    public Attendance(String tenantId, UUID employeeId, LocalDate date, LocalTime clockIn) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.date = date;
        this.clockIn = clockIn;
        this.isLate = clockIn.isAfter(LocalTime.of(8, 0));
        this.isOvertime = false;
    }

    public void clockOut(LocalTime time) {
        this.clockOut = time;
        this.workingHours = Duration.between(clockIn, time);
        this.isOvertime = workingHours.toHours() > 8;
    }
}
