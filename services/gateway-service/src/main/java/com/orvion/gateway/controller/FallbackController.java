package com.orvion.gateway.controller;

import com.orvion.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private Mono<ResponseEntity<ErrorResponse>> fallback(String serviceName, ServerWebExchange exchange) {
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("SERVICE_UNAVAILABLE")
            .message("Service temporarily unavailable. Please try again later.")
            .timestamp(Instant.now())
            .path(exchange.getRequest().getPath().value())
            .build();

        return Mono.just(ResponseEntity.status(503)
            .header("X-Circuit-Breaker", "open")
            .header("X-Fallback-Service", serviceName)
            .body(error));
    }

    @RequestMapping("/finance")
    public Mono<ResponseEntity<ErrorResponse>> financeFallback(ServerWebExchange exchange) {
        return fallback("finance-service", exchange);
    }

    @RequestMapping("/inventory")
    public Mono<ResponseEntity<ErrorResponse>> inventoryFallback(ServerWebExchange exchange) {
        return fallback("inventory-service", exchange);
    }

    @RequestMapping("/hcm")
    public Mono<ResponseEntity<ErrorResponse>> hcmFallback(ServerWebExchange exchange) {
        return fallback("hcm-service", exchange);
    }

    @RequestMapping("/manufacturing")
    public Mono<ResponseEntity<ErrorResponse>> manufacturingFallback(ServerWebExchange exchange) {
        return fallback("manufacturing-service", exchange);
    }

    @RequestMapping("/sales")
    public Mono<ResponseEntity<ErrorResponse>> salesFallback(ServerWebExchange exchange) {
        return fallback("sales-crm-service", exchange);
    }

    @RequestMapping("/notification")
    public Mono<ResponseEntity<ErrorResponse>> notificationFallback(ServerWebExchange exchange) {
        return fallback("notification-service", exchange);
    }

    @RequestMapping("/reporting")
    public Mono<ResponseEntity<ErrorResponse>> reportingFallback(ServerWebExchange exchange) {
        return fallback("reporting-service", exchange);
    }
}
