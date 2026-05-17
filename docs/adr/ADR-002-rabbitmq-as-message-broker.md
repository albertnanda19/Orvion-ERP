# ADR-002: Use RabbitMQ as Message Broker over Apache Kafka

**Date**: 2026-05-17

**Status**: Accepted

## Context

Orvion ERP requires asynchronous messaging between services for domain events, notifications, and eventual consistency. The two primary contenders for the message broker are Apache Kafka and RabbitMQ.

Key requirements:
- Reliable message delivery with at-least-once guarantees
- Flexible routing (topic, direct, fanout exchanges)
- Dead Letter Queue support for failed message handling
- Publisher confirms for guaranteed publishing
- Lower operational overhead for the team's current skill set
- Good Spring Boot integration via Spring AMQP

## Decision

**Use RabbitMQ 3.13** as the primary message broker for Orvion ERP.

### Key Configuration

- **Exchanges**: Topic exchange (`orvion.topic`) for flexible routing, Direct exchange (`orvion.direct`) for point-to-point, Fanout exchange (`orvion.fanout`) for broadcast
- **Queues**: One queue per consumer group with clear naming convention
- **Dead Letter Queues**: Every queue has a corresponding DLQ with suffix `.dlq`
- **Publisher Confirms**: Enabled for guaranteed publishing
- **Delivery Mode**: Persistent messages for durability
- **Retry**: Spring Retry with exponential backoff for transient failures

### Queue Naming Convention

```
orvion.<service>.<action>
```

### Routing Key Convention

```
<domain>.<action>
```

## Consequences

### Positive

- Lower operational complexity compared to Kafka
- Mature Spring Boot integration via `spring-boot-starter-amqp`
- Built-in Dead Letter Queue support simplifies error handling
- Flexible routing with multiple exchange types
- Publisher confirms ensure message durability

### Negative

- Lower throughput compared to Kafka (not a concern for current requirements)
- Messages are not persisted for replay beyond consumer acknowledgment — event sourcing patterns would need additional infrastructure
- Consumer groups must be configured per queue (not automatic like Kafka consumer groups)

## Alternatives Considered

### Apache Kafka
Rejected because:
- Higher operational overhead for the current team expertise
- Overkill for current throughput requirements (< 10K msg/s)
- Spring Cloud Stream with Kafka binder adds complexity for the current use case
- Kafka's log-based storage is better suited for event sourcing, which is not the primary pattern here

### AWS SQS / SNS
Rejected because the platform must remain cloud-agnostic and deployable on-premises.

### Redis Pub/Sub
Rejected because it lacks durable message storage, delivery guarantees, and dead letter support.
