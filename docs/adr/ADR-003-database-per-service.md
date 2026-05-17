# ADR-003: Database per Service Pattern

**Date**: 2026-05-17

**Status**: Accepted

## Context

In a microservices architecture, data ownership is a critical architectural decision. Sharing a single database across services creates tight coupling, makes independent deployments impossible, and creates a single point of failure.

For Orvion ERP, each service manages distinct business data:
- Finance: ledgers, chart of accounts, invoices, budgets
- Inventory: stock levels, warehouses, inventory transactions
- HCM: employees, payroll, attendance, organizational structure
- Manufacturing: BOMs, work orders, production runs, quality checks
- Sales CRM: customers, leads, opportunities, sales orders
- Notifications: templates, delivery logs
- Reporting: cached datasets, report definitions

## Decision

Each microservice owns its **private database/schema**. Services can only access their own database; all cross-service data access happens through the service's API or through event-driven data synchronization.

- Database: PostgreSQL 16
- Schema per service: `orvion_<service_name>`
- Migration tool: Flyway (each service has its own migration directory)
- No direct cross-service database queries — all access via APIs
- Event-driven data replication where cross-service read models are needed

### Migration Strategy

Each service runs Flyway on startup and owns its migration files:

```
services/<service>/src/main/resources/db/migration/
    V1__initial_schema.sql
    V2__add_<feature>.sql
```

### Cross-Service Data Access

When Service A needs data owned by Service B:
1. Service A calls Service B's REST API (synchronous)
2. Service B publishes a domain event, Service A consumes it and updates its local read model (asynchronous)

## Consequences

### Positive

- Strong data encapsulation and ownership
- Services can evolve their schemas independently
- Each service can choose the optimal data model for its needs
- No single database bottleneck
- Schema changes in one service never break another service
- Natural isolation for multi-tenancy

### Negative

- Data consistency across services is eventually consistent (no distributed transactions)
- Additional latency for cross-service data access
- Need for event-driven data synchronization across services
- More complex reporting — must aggregate from multiple services
- Operational overhead of managing multiple databases

### Mitigations

- Use Saga pattern (choreography-based) for multi-service transactions
- Implement a reporting-service that aggregates data from multiple services via events
- Use distributed tracing (OpenTelemetry) to debug cross-service data issues
- Implement idempotent event consumers for safe retry

## Alternatives Considered

### Shared Database
Rejected because it creates tight coupling, makes independent deployments impossible, and creates a single point of failure.

### Schema-per-Service in Shared Database
Provides logical isolation but still couples database infrastructure. Acceptable as a transition step but not the target architecture.
