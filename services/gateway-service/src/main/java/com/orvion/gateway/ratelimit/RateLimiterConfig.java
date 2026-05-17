package com.orvion.gateway.ratelimit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    private static final String SUPER_ADMIN = "SUPER_ADMIN";

    @Bean
    @Primary
    public KeyResolver tenantUserKeyResolver() {
        return exchange -> {
            String tenantId = exchange.getRequest().getHeaders().getFirst("X-Tenant-Id");
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            String roles = exchange.getRequest().getHeaders().getFirst("X-User-Roles");

            String key = (tenantId != null ? tenantId : "default")
                + ":" + (userId != null ? userId : "anonymous");

            if (roles != null && roles.contains(SUPER_ADMIN)) {
                exchange.getAttributes().put("rate_limit_per_minute", 1000);
            } else {
                exchange.getAttributes().put("rate_limit_per_minute", 100);
            }

            return Mono.just(key);
        };
    }
}
