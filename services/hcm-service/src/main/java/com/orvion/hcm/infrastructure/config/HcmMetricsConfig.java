package com.orvion.hcm.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HcmMetricsConfig {
    @Bean
    public Counter employeesOnboardedCounter(MeterRegistry registry) {
        return Counter.builder("employees_onboarded_total")
            .description("Total employees onboarded")
            .tag("application", "orvion-hcm-service")
            .register(registry);
    }

    @Bean
    public Counter payrollProcessedCounter(MeterRegistry registry) {
        return Counter.builder("payroll_processed_total")
            .description("Total payroll records processed")
            .tag("application", "orvion-hcm-service")
            .register(registry);
    }

    @Bean
    public Counter leavesApprovedCounter(MeterRegistry registry) {
        return Counter.builder("leaves_approved_total")
            .description("Total leave requests approved")
            .tag("application", "orvion-hcm-service")
            .register(registry);
    }
}
