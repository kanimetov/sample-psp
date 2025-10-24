## Internal PSP (DKIBQR) Documentation

### 📋 **Centralized References (Single Source of Truth)**

- **[api/endpoints-reference.md](api/endpoints-reference.md)** — Complete API endpoints reference
- **[runtime/configuration-reference.md](runtime/configuration-reference.md)** — All system configurations
- **[data/redis-schema.md](data/redis-schema.md)** — Redis key patterns and TTL values

### 📁 **Documentation Structure**

- **product/** — Product requirements and roadmap
  - PRD.md — approved product requirements
  - roadmap.md — development milestones
  - success-metrics.md — KPI targets
- **api/** — API contracts and specifications
  - endpoints-reference.md — **MASTER** API reference
  - dto-mapping.md — detailed DTO specifications
  - error-catalog.md — error codes and handling
- **design/** — architecture and flows
  - architecture.md — system architecture
  - flows.md — business process flows
  - decisions/ — architecture decision records (ADRs)
- **security/** — crypto, keys, access control
  - crypto.md — JWS/JWE specifications
  - keys-management.md — key rotation and storage
  - access-control.md — authentication and authorization
- **data/** — database and caching
  - redis-schema.md — **MASTER** Redis reference
  - schema.md — Oracle database schema
  - migrations.md — database migration strategy
- **reliability/** — resilience patterns
  - idempotency.md — idempotency implementation
  - ratelimiting.md — rate limiting strategy
  - resilience.md — circuit breakers and retries
- **messaging/** — RabbitMQ and outbox
  - rabbitmq.md — message broker configuration
  - outbox.md — outbox pattern implementation
- **runtime/** — deployment and operations
  - configuration-reference.md — **MASTER** config reference
  - observability.md — monitoring and logging
  - operations.md — deployment procedures
- **testing/** — testing strategy
  - test-plan.md — comprehensive testing approach
  - contract-tests.md — API contract testing
  - perf-plan.md — performance testing
- **compliance/** — requirements and audit
  - requirements.md — regulatory compliance
  - audit-trail.md — audit logging requirements

### ⚠️ **Important Notes**

- **Always use centralized references** for API endpoints, configurations, and Redis schemas
- **Avoid duplicating information** across multiple files
- **Update centralized references first** when making changes
- **Link to centralized references** instead of copying information


