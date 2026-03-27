# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Rush Deal is a microservices-based time-deal e-commerce platform built to handle high-traffic scenarios. The system implements Saga pattern for distributed transactions, Outbox pattern for reliable event publishing, and Redis-based queue management.

## Build and Run

### Prerequisites
- Java 21
- Docker and Docker Compose
- PostgreSQL, Redis, Kafka (provided via Docker)

### Build Commands

```bash
# Build all services
./gradlew build

# Build specific service
./gradlew :payment-service:build

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build
```

### Running Services

```bash
# Start all infrastructure (PostgreSQL, Redis, Kafka, Zookeeper)
docker-compose up -d

# Start all services including monitoring (Prometheus, Grafana, Zipkin)
docker-compose -f docker-compose-app.yml up -d

# Start specific service
docker-compose -f docker-compose-app.yml up -d payment-service

# Rebuild and start service
docker-compose -f docker-compose-app.yml up -d --build payment-service

# View logs
docker-compose -f docker-compose-app.yml logs -f payment-service

# Stop all services
docker-compose -f docker-compose-app.yml down
```

### Test Commands

```bash
# Run all tests
./gradlew test

# Run tests for specific service
./gradlew :payment-service:test

# Run specific test class
./gradlew :payment-service:test --tests PaymentServiceTest

# Run tests with detailed output
./gradlew test --info
```

## Architecture

### Service Structure (Hexagonal/Clean Architecture)

Each service follows a layered architecture:

```
{service-name}/
├── domain/           # Core business logic, entities, value objects, domain services
│   ├── model/        # Domain entities and aggregates
│   ├── vo/           # Value objects (immutable)
│   ├── repository/   # Repository interfaces (ports)
│   └── exception/    # Domain-specific exceptions
├── application/      # Use cases, application services, commands/results
│   ├── service/      # Application services (orchestration)
│   ├── command/      # Command DTOs
│   └── result/       # Result DTOs
├── infrastructure/   # External adapters (DB, messaging, external APIs)
│   ├── repository/   # JPA repository implementations
│   ├── kafka/        # Kafka producers/consumers
│   ├── client/       # OpenFeign clients
│   └── event/        # Event message definitions
└── presentation/     # Controllers, request/response DTOs
    ├── controller/
    └── dto/
```

### Key Design Patterns

**Saga Pattern (Orchestration-based)**
- Order-service acts as saga orchestrator
- Coordinates distributed transactions across order, payment, timedeal, user services
- State machine: Order → Payment → Stock → Point
- Compensating transactions for rollback

**Outbox Pattern**
- Ensures reliable event publishing (99.9% success rate)
- Transactional outbox saves events to database first
- Scheduler publishes pending events every 5 seconds
- Retry mechanism: 3 attempts before marking as FAILED
- Implementation: `PaymentOutbox`, `TransactionKafkaProducer.publishPendingEvents()`

**CQRS (Command Query Responsibility Segregation)**
- Order-service: Write to PostgreSQL, read from Redis cache
- 2-Tier caching: Local + Redis (85-90% cache hit rate)
- Query performance: 500ms → 10ms (50x improvement)

**Redis-based Queue System**
- Sorted Set for waitlist/active queue management
- User Index Key prevents duplicate entries (1 user = 1 token)
- CompletableFuture with dedicated executor for async processing
- Scheduler queue for timedeal start/end automation

**Optimistic Locking + Redis Pre-filtering**
- Stock management: Redis decrements first, then DB update with @Version
- Prevents unnecessary DB requests during high traffic
- Maintains data consistency via optimistic lock

### Service Ports

| Service | Port |
|---------|------|
| auth-service | 8000 |
| payment-service | 8010 |
| product-service | 8020 |
| timedeal-service | 8030 |
| queue-service | 8040 |
| order-service | 8050 |
| user-service | 8060 |
| discovery-service (Eureka) | 8761 |
| api-gateway | 8080 |
| Prometheus | 9090 |
| Grafana | 3000 |
| Zipkin | 9411 |
| Kafka UI | 18080 |

### Redis Instances

| Instance | Port | Service |
|----------|------|---------|
| auth_redis | 6378 | auth-service |
| user_redis | 6379 | user-service |
| queue_redis | 6380 | queue-service |
| order_redis | 6381 | order-service |
| timedeal_redis | 6382 | timedeal-service |

### Kafka Topics

**Order Flow:**
- `payment-request` - Order → Payment (결제 요청)
- `payment-complete-result` - Payment → Order (결제 완료)
- `payment-transaction-result` - Order/Timedeal → Payment (보상 트랜잭션)
- `stock-reservation` - Order → Timedeal (재고 예약)
- `stock-reservation-result` - Timedeal → Order (재고 예약 결과)
- `point-deduction` - Order → User (포인트 차감)
- `point-deduction-result` - User → Order (포인트 차감 결과)

**Dead Letter Topics (DLT):**
- `{topic-name}.DLT` - Failed messages after 3 retries (1초 간격)
- Configuration: `KafkaConsumerConfig.kafkaListenerContainerFactory()`

## Important Implementation Details

### Transaction Boundaries

**DO:**
- Keep `@Transactional` scope minimal
- Kafka publishing should be outside transaction or use Outbox pattern
- State-based compensating transactions:
  - PENDING → DB only (failPayment())
  - PAID → Call external API (cancelPayment())
  - CANCELLED/FAILED → Idempotent (log only)

**DON'T:**
- Don't mix reactive (Mono/Flux) operations inside @Transactional
- Don't block() inside @Transactional methods
- Don't call external APIs (PortOne, OpenFeign) inside transaction

### Error Handling in Kafka Consumers

```java
@KafkaListener(topics = "payment-request")
public void consume(String message) {
    try {
        // Process message
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Deserialization 실패", e); // Triggers retry
    } catch (BusinessException e) {
        throw new RuntimeException("Processing 실패", e); // Triggers retry
    }
}
```

- RuntimeException → Retry with FixedBackOff (1초 간격, 3회)
- After 3 retries → Sent to DLT
- BusinessException should be wrapped in RuntimeException for retry

### Payment Service - PortOne Integration

**Payment Flow:**
1. `preparePayment()` - Validates order, creates payment record
2. Client completes payment on PortOne
3. PortOne sends webhook or client calls `completePayment()`
4. `processPayment()` - Verifies amount/currency, saves to Outbox, publishes event

**Cancellation:**
- State-based: Check payment.getStatus() before calling PortOne API
- Prevents unnecessary PG API costs
- Idempotent: Safe to call multiple times

### Common Pitfalls

1. **Redis Configuration**
   - Each service uses different Redis instance
   - Use service-specific env vars: `{SERVICE}_REDIS_HOST`
   - Default passwords may not exist (add `:` default in @Value)

2. **Docker Builds**
   - Services take 2-6 minutes to build
   - Use `--build` flag only when code changes
   - Check healthchecks: `depends_on.condition: service_healthy`

3. **Duplicate Keys in YAML**
   - Check for duplicate `server:` blocks
   - Validate with `docker-compose config`

4. **Outbox Pattern**
   - Always save to Outbox first, then publish to Kafka
   - Scheduler handles retry automatically
   - Check `retryCount >= 3` for failure threshold

## Development Workflow

1. **Making Changes to a Service**
   ```bash
   # Edit code
   # Build
   ./gradlew :payment-service:build
   # Rebuild Docker image and restart
   docker-compose -f docker-compose-app.yml up -d --build payment-service
   # Check logs
   docker-compose -f docker-compose-app.yml logs -f payment-service
   ```

2. **Testing Kafka Events**
   - Access Kafka UI at http://localhost:18080
   - Check topics and consumer groups
   - Verify message format and partition distribution

3. **Monitoring**
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)
   - Zipkin: http://localhost:9411
   - Metrics: http://localhost:{port}/actuator/prometheus

4. **Database Access**
   - PostgreSQL: localhost:15432
   - Schemas: auth_schema, user_schema, order_schema, payment_schema, etc.
   - Init script: `scripts/init-schemas.sql`

## Code Style

- Use Lombok for boilerplate reduction (@Getter, @RequiredArgsConstructor)
- Prefer immutability: record classes for DTOs, final fields in VOs
- Domain entities should have rich behavior (not anemic)
- Repository methods follow Spring Data JPA conventions
- Exception handling: BusinessException for domain errors, RuntimeException for infrastructure
- Logging: Use @Slf4j, structured logging with context

## Key Files to Reference

- `PROJECT_OVERVIEW.md` - Comprehensive project documentation
- `build.gradle` - Dependency management (Java 21, Spring Boot 3.5.8, Spring Cloud 2025.0.0)
- `docker-compose.yml` - Infrastructure setup
- `docker-compose-app.yml` - All services + monitoring
- `.env.example` - Required environment variables
- `scripts/init-schemas.sql` - Database schema initialization