package com.orvion.reporting.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportingMetricsConfig {
    @Bean
    public Counter reportsGeneratedCounter(MeterRegistry registry) {
        return Counter.builder("reports_generated_total")
            .description("Total reports generated")
            .tag("application", "orvion-reporting-service")
            .register(registry);
    }

    @Bean
    public Counter reportsFailedCounter(MeterRegistry registry) {
        return Counter.builder("reports_failed_total")
            .description("Total reports failed")
            .tag("application", "orvion-reporting-service")
            .register(registry);
    }
}
