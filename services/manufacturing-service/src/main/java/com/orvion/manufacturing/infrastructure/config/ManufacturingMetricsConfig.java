package com.orvion.manufacturing.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManufacturingMetricsConfig {
    @Bean
    public Counter workOrdersCreatedCounter(MeterRegistry registry) {
        return Counter.builder("work_orders_created_total")
            .description("Total work orders created")
            .tag("application", "orvion-manufacturing-service")
            .register(registry);
    }

    @Bean
    public Counter workOrdersCompletedCounter(MeterRegistry registry) {
        return Counter.builder("work_orders_completed_total")
            .description("Total work orders completed")
            .tag("application", "orvion-manufacturing-service")
            .register(registry);
    }

    @Bean
    public Counter inspectionsCompletedCounter(MeterRegistry registry) {
        return Counter.builder("inspections_completed_total")
            .description("Total quality inspections completed")
            .tag("application", "orvion-manufacturing-service")
            .register(registry);
    }
}
