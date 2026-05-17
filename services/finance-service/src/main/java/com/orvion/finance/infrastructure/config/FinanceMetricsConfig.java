package com.orvion.finance.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class FinanceMetricsConfig {

    private static final String SERVICE_TAG = "finance";

    @Bean
    public Counter invoicesCreatedCounter(MeterRegistry registry) {
        return Counter.builder("finance.invoices.created.total")
            .description("Total number of invoices created")
            .tag("service", SERVICE_TAG)
            .register(registry);
    }

    @Bean
    public Counter paymentsProcessedCounter(MeterRegistry registry) {
        return Counter.builder("finance.payments.processed.total")
            .description("Total number of payments processed")
            .tag("service", SERVICE_TAG)
            .register(registry);
    }

    @Bean
    public Counter journalEntriesPostedCounter(MeterRegistry registry) {
        return Counter.builder("finance.journal.entries.posted.total")
            .description("Total number of journal entries posted")
            .tag("service", SERVICE_TAG)
            .register(registry);
    }

    @Bean
    public Timer reportGenerationTimer(MeterRegistry registry) {
        return Timer.builder("finance.report.generation.duration")
            .description("Time taken to generate reports")
            .tag("service", SERVICE_TAG)
            .register(registry);
    }

    @Bean
    public AtomicLong outstandingInvoicesGauge(MeterRegistry registry) {
        AtomicLong gaugeValue = new AtomicLong(0);
        Gauge.builder("finance.invoices.outstanding.count", gaugeValue, AtomicLong::get)
            .description("Number of outstanding invoices per tenant")
            .tag("service", SERVICE_TAG)
            .register(registry);
        return gaugeValue;
    }
}
