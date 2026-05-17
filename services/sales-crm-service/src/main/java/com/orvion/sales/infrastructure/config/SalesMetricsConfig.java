package com.orvion.sales.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SalesMetricsConfig {
    @Bean
    public Counter leadsCreatedCounter(MeterRegistry registry) {
        return Counter.builder("leads_created_total")
            .description("Total leads created")
            .tag("application", "orvion-sales-crm-service")
            .register(registry);
    }

    @Bean
    public Counter opportunitiesCreatedCounter(MeterRegistry registry) {
        return Counter.builder("opportunities_created_total")
            .description("Total opportunities created")
            .tag("application", "orvion-sales-crm-service")
            .register(registry);
    }

    @Bean
    public Counter ordersConfirmedCounter(MeterRegistry registry) {
        return Counter.builder("orders_confirmed_total")
            .description("Total sales orders confirmed")
            .tag("application", "orvion-sales-crm-service")
            .register(registry);
    }
}
