# C4 Context Diagram — Orvion ERP

```
┌─────────────────────────────────────────────────────────────────┐
│                      Orvion ERP System                           │
│                                                                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ Company   │  │ Finance  │  │   HR     │  │Inventory │        │
│  │ Admin     │  │  Staff   │  │  Staff   │  │  Staff   │        │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘        │
│       │             │             │             │               │
│       └─────────────┼─────────────┼─────────────┘               │
│                     │             │                             │
│              ┌──────▼─────────────▼──────┐                      │
│              │     API Gateway (:8080)    │                      │
│              └──────┬─────────────┬──────┘                      │
│                     │             │                             │
│         ┌───────────▼──┐   ┌─────▼───────────┐                  │
│         │  Keycloak    │   │  Microservices  │                  │
│         │  (:8180)     │   │  (:8081-8087)   │                  │
│         └──────────────┘   └────────┬────────┘                  │
│                                     │                           │
│         ┌───────────┐  ┌───────────┼────────────┐              │
│         │  Email    │  │  ┌────▼──┐ ┌──▼───┐ ┌──▼───┐        │
│         │  Server   │  │  │Postgre│ │Redis │ │Rabbit│         │
│         └───────────┘  │  └───────┘ └──────┘ └──────┘         │
│                        │  ┌───────┐ ┌──────┐                   │
│                        │  │Elastic│ │Jaeger│                   │
│                        │  └───────┘ └──────┘                   │
└─────────────────────────────────────────────────────────────────┘
```

## People

- **Company Admin** — Administers the system, manages users and roles
- **Finance Staff** — Manages invoices, payments, journal entries
- **HR Staff** — Manages employees, payroll, leave requests
- **Inventory Staff** — Manages products, warehouses, purchase orders
- **Manufacturing Staff** — Manages BOMs, work orders, machines
- **Sales Staff** — Manages leads, opportunities, customers, orders

## External Systems

- **Keycloak** — Identity and access management (OAuth2/OIDC)
- **Email Server** — Sends notifications via SMTP

## System Boundary

Orvion ERP is the system of record for all business operations.
