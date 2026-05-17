package com.orvion.notification.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationMetricsConfig {

    @Bean
    public Counter notificationsSentCounter(MeterRegistry registry) {
        return Counter.builder("notifications_sent_total")
            .description("Total notifications sent")
            .tag("application", "orvion-notification-service")
            .register(registry);
    }

    @Bean
    public Counter notificationsFailedCounter(MeterRegistry registry) {
        return Counter.builder("notifications_failed_total")
            .description("Total notifications failed")
            .tag("application", "orvion-notification-service")
            .register(registry);
    }
}
