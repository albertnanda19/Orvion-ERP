# ADR-001: Use Microservices Architecture with Event-Driven Communication

**Date**: 2026-05-17

**Status**: Accepted

## Context

Orvion ERP is an enterprise-grade platform that must support finance, inventory, human capital management, manufacturing, sales CRM, notifications, and reporting — each with distinct domain logic, scalability requirements, and release cadences.

A monolithic architecture was evaluated but deemed unsuitable because:
- Teams cannot work independently on different modules
- A failure in one module (e.g., report generation) can bring down the entire system
- Scaling is coarse-grained — we cannot scale individual capabilities independently
- Technology upgrades require full-application redeployment
- The codebase becomes unmanageable beyond ~50 developers

Given the need for long-term maintainability, independent deployability, and team autonomy, a microservices architecture is the preferred approach.

## Decision

Adopt a **microservices architecture** where each business capability is implemented as an independent, self-contained service. Services communicate via:

- **Synchronous**: RESTful APIs (JSON over HTTP) via Spring Cloud Gateway for request-response patterns
- **Asynchronous**: RabbitMQ events for domain events and eventual consistency
- **Internal**: gRPC for high-throughput inter-service communication where needed

Each service follows **Domain-Driven Design (DDD)** with clearly defined bounded contexts. The services are:

- `gateway-service` — API Gateway (Spring Cloud Gateway)
- `finance-service` — Financial management
- `inventory-service` — Inventory and warehouse management
- `hcm-service` — Human capital management
- `manufacturing-service` — Production and manufacturing
- `sales-crm-service` — Sales and customer relationship management
- `notification-service` — Multi-channel notification delivery
- `reporting-service` — Report generation and BI
- `config-service` — Centralized configuration
- `common-lib` — Shared library (exceptions, responses, utilities, constants)

## Consequences

### Positive

- Independent development, testing, and deployment per service
- Fine-grained scaling based on service-specific load
- Failure isolation — one service outage does not cascade
- Technology flexibility — each service can evolve independently
- Team ownership — each team owns one or more services end-to-end

### Negative

- Increased operational complexity (deployment, monitoring, logging)
- Network latency for inter-service calls
- Data consistency challenges — eventual consistency required across services
- More complex testing (contract tests, integration tests)
- Distributed debugging and tracing overhead

### Mitigations

- OpenTelemetry for distributed tracing across all services
- API Gateway pattern to centralize cross-cutting concerns (auth, rate limiting, logging)
- Circuit breakers (Resilience4j) to handle service failures gracefully
- Well-defined API contracts using OpenAPI
- Integration event catalog documented and versioned

## Alternatives Considered

### Modular Monolith
Rejected because it does not provide independent deployability or fine-grained scaling. Suitable for smaller teams but not for enterprise ERP at scale.

### Service-Oriented Architecture (SOA)
Rejected due to heavy ESB overhead and slower evolution cycles compared to lightweight microservices with RabbitMQ and REST/gRPC.
