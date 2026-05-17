package com.orvion.gateway.filter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private final Tracer tracer;

    public RequestLoggingFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getPath().value();
        String tenantId = exchange.getRequest().getHeaders().getFirst("X-Tenant-Id");
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String traceId = resolveTraceId(exchange);

        log.info("[{}] {} {} | tenant={} user={}", traceId, method, path,
            tenantId != null ? tenantId : "-", userId != null ? userId : "-");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - start;
            int status = exchange.getResponse().getStatusCode() != null
                ? exchange.getResponse().getStatusCode().value() : 0;
            log.info("[{}] {} {} => {} ({}ms)", traceId, method, path, status, duration);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private String resolveTraceId(ServerWebExchange exchange) {
        Span span = tracer.currentSpan();
        if (span != null && span.context() != null) {
            return span.context().traceId();
        }
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        return traceId != null ? traceId : "NO_TRACE";
    }
}
