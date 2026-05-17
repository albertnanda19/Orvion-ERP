# ADR-004: API Gateway as Single Entry Point with Reactive Architecture

**Date**: 2026-05-17

**Status**: Accepted

## Context

Orvion ERP consists of multiple microservices (finance, inventory, HCM, manufacturing, sales, notification, reporting). Each service exposes its own REST API, creating several cross-cutting concerns that must be handled centrally:
- Authentication and authorization (JWT validation via Keycloak)
- Rate limiting and DoS protection
- Circuit breaking and resilience
- Cross-Origin Resource Sharing (CORS)
- Request logging, correlation IDs, and distributed tracing
- Request routing and versioning
- Centralized configuration management

Without a gateway, each microservice would need to independently implement these concerns, leading to duplication, inconsistency, and increased maintenance burden.

## Decision

We will deploy a reactive API Gateway using **Spring Cloud Gateway** as the single entry point for all external and internal HTTP traffic.

### Architecture

```
Client (Angular SPA / Mobile / External)
        |
        v
  [Spring Cloud Gateway :8080]
        |
        |--- Config Service :8888 (configuration source)
        |
        |--- Finance Service :8081
        |--- Inventory Service :8082
        |--- HCM Service :8083
        |--- Manufacturing Service :8084
        |--- Sales CRM Service :8085
        |--- Notification Service :8086
        |--- Reporting Service :8087
        |
  [Keycloak :8180] -- JWT token validation
  [Redis :6379] -- Rate limit counters
  [Prometheus :9090] -- Metrics scraping
```

### Routing Table

| Route Pattern | Target Service | Port | Circuit Breaker |
|---|---|---|---|
| `/api/v1/finance/**` | finance-service | 8081 | finance-cb |
| `/api/v1/inventory/**` | inventory-service | 8082 | inventory-cb |
| `/api/v1/hcm/**` | hcm-service | 8083 | hcm-cb |
| `/api/v1/manufacturing/**` | manufacturing-service | 8084 | manufacturing-cb |
| `/api/v1/sales/**` | sales-crm-service | 8085 | sales-cb |
| `/api/v1/notifications/**` | notification-service | 8086 | notification-cb |
| `/api/v1/reports/**` | reporting-service | 8087 | reporting-cb |
| `/api/v1/auth/**` | (handled by Keycloak) | 8180 | — |
| `/fallback/**` | Gateway fallback controller | 8080 | — |
| `/actuator/**` | Gateway management | 8080 | — |

### Rate Limiting Strategy

- **Key resolution**: Tenant ID + User ID composite key (`X-Tenant-Id:X-User-Id`)
- **Default limits**: 100 requests/minute per user
- **Super admin limits**: 1000 requests/minute per user
- **Backend**: Redis (DB 8) via `RequestRateLimiter` filter with token bucket algorithm
- **Burst capacity**: 2x replenish rate (200 for default, 2000 for super admin)

### Circuit Breaker Configuration

Each service route has a dedicated circuit breaker instance with the following defaults:

| Parameter | Value |
|---|---|
| slidingWindowSize | 10 |
| failureRateThreshold | 50% |
| waitDurationInOpenState | 10s |
| permittedNumberOfCallsInHalfOpenState | 3 |
| automaticTransition (finance-cb only) | true |

All circuit breakers fall back to the Gateway's `/fallback/{service}` controller, which returns HTTP 503 with a standardized `ErrorResponse` body.

### JWT Validation Approach

1. The gateway validates all incoming JWT tokens against Keycloak's issuer URI (`http://localhost:8180/realms/orvion`)
2. The `JwtClaimsExtractor` extracts tenant, user, roles, and email from the token
3. These values are propagated to downstream services as HTTP headers:
   - `X-Tenant-Id`
   - `X-User-Id`
   - `X-User-Roles`
   - `X-User-Email`
4. Downstream services trust these headers because they only accept traffic from the internal network
5. Public endpoints (`/actuator/health`, `/actuator/prometheus`, `/fallback/**`, `/api/v1/auth/**`) do not require authentication

### Reactive Architecture

- **Spring Cloud Gateway** is built on **Spring WebFlux** (reactive stack)
- Uses **Netty** as the embedded web server (non-blocking I/O)
- All filters use reactive types (`Mono`/`Flux`)
- Redis client uses **Lettuce** (reactive, non-blocking)
- Resilience4j circuit breakers use the **reactor** module for non-blocking execution

### Observability

- **Metrics**: Prometheus metrics exposed at `/actuator/prometheus` (HTTP server request percentiles, JVM metrics)
- **Tracing**: Micrometer Tracing bridge to OpenTelemetry, configurable for OTLP export
- **Logging**: Structured JSON-format logging with correlation IDs propagated via `X-Correlation-Id` header
- **Health checks**: Readiness probe at `/actuator/health` (checks Redis, config server connectivity)

## Consequences

### Positive
- Centralized security enforcement — one place to audit and update
- Consistent error responses across all services (via `ErrorResponse` from common-lib)
- Rate limiting prevents abuse of individual services
- Circuit breakers prevent cascade failures
- Correlation IDs enable end-to-end request tracing
- Reactive architecture provides high throughput with low resource consumption

### Negative
- Gateway becomes a single point of failure (mitigated by circuit breakers and fallbacks)
- Adds network latency (mitigated by co-locating services on same host in development)
- Gateway configuration must be updated when new services are added

### Trade-offs
- WebFlux (reactive) chosen over Spring MVC (servlet) for higher concurrency with fewer threads
- Native config repository chosen over Git-based for simplicity; can migrate to Git later if needed
- Rate limit key uses tenant+user rather than IP to support mobile clients behind NAT
