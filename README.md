# Orvion ERP — Cloud-Native Enterprise Resource Planning Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-18-red.svg)](https://angular.dev)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Architecture](https://img.shields.io/badge/Architecture-Microservices-purple.svg)]()
[![Messaging](https://img.shields.io/badge/Broker-RabbitMQ-orange.svg)]()

## Overview

**Orvion ERP** is a production-grade, cloud-native Enterprise Resource Planning platform built with a microservices architecture, event-driven communication, and Domain-Driven Design (DDD). It provides a comprehensive suite of business management modules designed for enterprise-scale organizations.

## Architecture

Orvion ERP consists of 9 Spring Boot microservices communicating via REST APIs, gRPC (for inter-service), and RabbitMQ (for async events), with a single Angular 18 SPA frontend.

```
                           ┌─────────────────────────────────────┐
                           │        Angular 18 SPA (:4200)        │
                           │   Signals · Keycloak SSO · Modules   │
                           └──────────────────┬──────────────────┘
                                              │ HTTPS / WebSocket
                           ┌──────────────────▼──────────────────┐
                           │    Spring Cloud Gateway (:8080)      │
                           │  JWT Auth · Rate Limiting · CB      │
                           └──┬──────────┬──────────┬────────────┘
                              │ REST     │ REST     │ REST
                    ┌─────────▼──┐  ┌────▼────┐  ┌──▼─────────┐
                    │ Finance    │  │Inventory│  │ Sales CRM  │
                    │ :8081      │  │:8082    │  │ :8085      │
                    │ gRPC :9091 │  │gRPC:9092│  │ gRPC :9095 │
                    └─────┬─────┘  └────┬────┘  └──────┬──────┘
                          │             │               │
                    ┌─────▼─────────────▼───────────────▼──────┐
                    │         RabbitMQ (Async Events)          │
                    │     orvion.*.exchange · DLQ · Outbox     │
                    └─────────────────┬───────────────────────-┘
                                      │
                    ┌─────────────────▼───────────────────────┐
                    │  Notification (:8086) · Report (:8087)  │
                    │  Config (:8888) · All subscribe events  │
                    └─────────────────┬───────────────────────┘
                                      │
         PostgreSQL ── Redis ── Elasticsearch ── Keycloak
         Prometheus ── Grafana ── Jaeger
```

## Modules

| Module | Port | gRPC | Description |
|--------|------|------|-------------|
| API Gateway | 8080 | — | Single entry point, JWT auth, rate limiting, circuit breaker |
| Finance Service | 8081 | 9091 | GL, AP/AR, Invoicing, Journal Entries, Financial Reports |
| Inventory Service | 8082 | 9092 | Products, Stock, Purchase Orders, Goods Receipt, Suppliers |
| HCM Service | 8083 | 9093 | Employees, Payroll, Leave, Attendance, Performance |
| Manufacturing Service | 8084 | 9094 | BOM, Work Orders, Quality Control, Machines |
| Sales CRM Service | 8085 | 9095 | Leads, Opportunities, Customers, Sales Orders |
| Notification Service | 8086 | — | Email, In-App, WebSocket real-time notifications |
| Reporting Service | 8087 | — | Executive Dashboard, Cross-service analytics, Elasticsearch |
| Config Service | 8888 | — | Centralized configuration for all services |

## Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | Angular 18, Signals, Angular Material, TailwindCSS, Chart.js |
| API Gateway | Spring Cloud Gateway (Reactive/WebFlux) |
| Sync API | REST (OpenAPI 3.1) + gRPC (Protobuf) |
| Async Messaging | RabbitMQ 3.13 (AMQP, DLQ, Publisher Confirms) |
| Backend | Java 21, Spring Boot 3.3.4, Spring Cloud 2023.0.3 |
| Auth & IAM | Keycloak 24 (OAuth2, OIDC, RBAC, Multi-tenancy) |
| Primary DB | PostgreSQL 16 (Database-per-Service) |
| Cache | Redis 7 (Cache-aside, Pub/Sub, Rate Limiting) |
| Search | Elasticsearch 8 (Full-text, Aggregations) |
| Resilience | Resilience4j (Circuit Breaker, Retry, Bulkhead) |
| Observability | Prometheus + Grafana + Jaeger (OpenTelemetry) |
| Migration | Flyway (per service) |
| Testing | JUnit 5, Mockito, Spring Security Test, Playwright (E2E) |

## Architecture Principles

- **Domain-Driven Design** — Bounded contexts, Aggregates, Value Objects, Domain Events
- **Event-Driven Architecture** — Outbox Pattern, Idempotent Consumers, Dead Letter Queues
- **CQRS** — Separate read (Redis/Elasticsearch) and write (PostgreSQL) paths
- **Clean Architecture** — Strict layer separation: presentation -> application -> domain -> infrastructure
- **API-First** — OpenAPI specs and .proto files designed before implementation
- **Security by Design** — JWT at gateway, Row-level security, RBAC, OWASP Top 10 mitigation
- **Observability-First** — Distributed tracing, structured logging, custom business metrics
- **Resilience** — Circuit breakers on all downstream calls, retry with exponential backoff

## Running the Application

### Prerequisites
- Java 21+
- Node.js 20+
- PostgreSQL 16
- Redis 7
- RabbitMQ 3.13
- Keycloak 24
- Elasticsearch 8

### Start All Services
```bash
chmod +x scripts/start-all.sh
./scripts/start-all.sh
```

### Build All Services
```bash
chmod +x scripts/build-all.sh
./scripts/build-all.sh
```

## Observability URLs (when running)

| Tool | URL | Credentials |
|------|-----|-------------|
| Frontend | http://localhost:4200 | Keycloak SSO |
| API Gateway | http://localhost:8080 | JWT required |
| Keycloak Admin | http://localhost:8180 | admin / OrvionAdmin@2024 |
| Grafana | http://localhost:3000 | admin / admin |
| Jaeger Tracing | http://localhost:16686 | — |
| RabbitMQ Mgmt | http://localhost:15672 | orvion_rabbit / Rabb1t@Orv10n2024 |
| Prometheus | http://localhost:9090 | — |

## License

MIT License -- see [LICENSE](LICENSE) for details.
