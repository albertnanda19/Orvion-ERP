package com.orvion.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String existingId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        String correlationId = (existingId != null && !existingId.isBlank()) ? existingId : UUID.randomUUID().toString();

        ServerWebExchange enhanced = exchange.mutate()
            .request(r -> r.header(CORRELATION_ID_HEADER, correlationId))
            .build();

        enhanced.getResponse().getHeaders().add(CORRELATION_ID_HEADER, correlationId);

        return chain.filter(enhanced);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
