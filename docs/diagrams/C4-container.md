# C4 Container Diagram — Orvion ERP

## Containers

### Angular SPA (TypeScript, Angular 18)
Frontend application served on port 4200. Communicates with backend via API Gateway.

### API Gateway (Spring Cloud Gateway, port 8080)
Single entry point for all API requests. Routes to microservices, handles authentication,
rate limiting, and cross-cutting concerns.

### Microservices (Spring Boot, Java 21)

| Service | Port | gRPC | Database | Description |
|---------|------|------|----------|-------------|
| Config Service | 8888 | — | — | Centralized configuration |
| Finance Service | 8081 | 9091 | orvion_finance | General ledger, AR/AP |
| Inventory Service | 8082 | 9092 | orvion_inventory | Products, warehouses |
| HCM Service | 8083 | 9093 | orvion_hcm | Employees, payroll |
| Manufacturing | 8084 | 9094 | orvion_manufacturing | BOMs, work orders |
| Sales CRM | 8085 | 9095 | orvion_sales | Leads, opportunities |
| Notification | 8086 | — | orvion_notification | Email, in-app notifs |
| Reporting | 8087 | — | orvion_reporting | Reports, audit, ES |

### PostgreSQL (9 databases)
One database per service plus Keycloak database.

### Redis (single instance, 16 logical DBs)
Caching, rate limiting, session storage.

### RabbitMQ
Message broker for async communication between services.

### Elasticsearch
Audit log storage and full-text search.

### Keycloak
Identity provider with OAuth2/OIDC.

## Container Diagram

```
┌──────────────┐     ┌───────────────────────────────────────────────┐
│  Angular SPA │────▶│              API Gateway                      │
│   (:4200)    │     │          Spring Cloud Gateway                 │
└──────────────┘     └──────┬──────────┬──────────┬────────────────┘
                            │          │          │
                    ┌───────▼──┐ ┌─────▼────┐ ┌───▼────────┐
                    │ Keycloak │ │ Auth     │ │ Rate        │
                    │ (:8180)  │ │ Filter   │ │ Limiter     │
                    └──────────┘ └──────────┘ └────────────┘
                            │
          ┌─────────────────┼──────────────────┐
          │                 │                  │
   ┌──────▼──────┐  ┌──────▼──────┐  ┌───────▼─────┐
   │ Finance     │  │ Inventory   │  │ HCM         │
   │ (:8081)     │  │ (:8082)     │  │ (:8083)     │
   │ gRPC:9091   │  │ gRPC:9092   │  │ gRPC:9093   │
   └──────┬──────┘  └──────┬──────┘  └───────┬─────┘
          │                │                  │
   ┌──────▼──────┐  ┌──────▼──────┐  ┌───────▼─────┐
   │ Manuf.      │  │ Sales CRM   │  │ Notification│
   │ (:8084)     │  │ (:8085)     │  │ (:8086)     │
   │ gRPC:9094   │  │ gRPC:9095   │  │             │
   └──────┬──────┘  └──────┬──────┘  └───────┬─────┘
          │                │                  │
   ┌──────┴───────────────┴──────────────────┴────┐
   │              Reporting (:8087)                │
   └───────────────────────────────────────────────┘

                      │
     ┌────────────────┼──────────────────┐
     │                │                  │
┌────▼────┐     ┌─────▼─────┐     ┌─────▼────┐
│PostgreSQL│     │   Redis   │     │ RabbitMQ │
│ ×9 DBs   │     │ 16 logical│     │ Messages │
└──────────┘     └───────────┘     └──────────┘
┌──────────┐     ┌───────────┐
│Elastic   │     │  Jaeger   │
│ Search   │     │  Tracing  │
└──────────┘     └───────────┘
```
