package com.orvion.inventory.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryMetricsConfig {
    @Bean
    public Counter stockMovementsCounter(MeterRegistry registry) {
        return Counter.builder("stock_movements_total")
            .description("Total stock movements")
            .tag("application", "orvion-inventory-service")
            .register(registry);
    }

    @Bean
    public Counter purchaseOrdersCounter(MeterRegistry registry) {
        return Counter.builder("purchase_orders_created_total")
            .description("Total purchase orders created")
            .tag("application", "orvion-inventory-service")
            .register(registry);
    }

    @Bean
    public Counter reorderEventsCounter(MeterRegistry registry) {
        return Counter.builder("reorder_events_total")
            .description("Total reorder events triggered")
            .tag("application", "orvion-inventory-service")
            .register(registry);
    }
}
