# Configuration Reference

> **Single Source of Truth** for all PSP system configurations. All other documents should reference this file.

## Overview

This document provides the definitive configuration values for the PSP system, including timeouts, retries, performance targets, and infrastructure settings.

## Timeout Configuration

### Operator API Timeouts

**Default Values:**
```yaml
operator:
  timeout:
    connection: 5000      # Connection timeout (ms)
    read: 30000          # Read timeout (ms)
    write: 30000         # Write timeout (ms)
    response: 60000      # Response timeout (ms)
```

**Environment-Specific Values:**

| Environment | Connection | Read | Write | Response |
|-------------|-----------|------|-------|----------|
| Development | 5000ms | 30000ms | 30000ms | 60000ms |
| Testing | 5000ms | 30000ms | 30000ms | 60000ms |
| Production | 3000ms | 20000ms | 20000ms | 45000ms |

### Timeout Types Explained

1. **Connection Timeout:** Maximum time to establish TCP connection
2. **Read Timeout:** Maximum time waiting for data after connection established
3. **Write Timeout:** Maximum time to send data after connection established
4. **Response Timeout:** Maximum time for entire request-response cycle

---

## Retry Configuration

### Retry Strategy

**Retryable Errors:**
- 502 Bad Gateway
- 503 Service Unavailable
- 504 Gateway Timeout

**Non-Retryable Errors:**
- 400 Bad Request
- 404 Not Found
- 422 Unprocessable Entity
- 452-456 Business validation errors

**Retry Parameters:**
```yaml
retry:
  max-attempts: 3
  initial-delay: 1000ms
  max-delay: 10000ms
  backoff-multiplier: 2.0
  exponential-delay: 1s → 2s → 4s (max 10s)
```

---

## Performance Targets

### System Performance

| Metric | Target | Measurement |
|--------|--------|-------------|
| TPS (Transactions Per Second) | 5,000 | Peak load |
| p95 Latency | ≤ 150ms | Hot path |
| p99 Latency | ≤ 300ms | Hot path |
| Availability | ≥ 99.95% | Monthly |

### Infrastructure Scaling

| Component | Configuration |
|-----------|---------------|
| PSP Instances | 6–10 nodes |
| CPU per Instance | 2–4 vCPU |
| RAM per Instance | 2–4 GB |
| DB Connections | ≤ 50 per node |
| Redis Latency | 1–2 ms |
| Redis Pools | 200–400 |

---

## Circuit Breaker Configuration

```yaml
circuit-breaker:
  sliding-window: 20-50 requests
  failure-threshold: 50%
  timeout: 60000ms
  bulkhead:
    max-concurrent-calls: 100
    max-wait-duration: 1000ms
```

---

## Database Configuration

### Oracle Database

```yaml
database:
  oracle:
    connection-pool:
      min-size: 5
      max-size: 50
      timeout: 30000ms
    partitions:
      strategy: by created_at
      granularity: day/week
```

### Redis Configuration

```yaml
redis:
  connection-pool:
    min-size: 10
    max-size: 200
    timeout: 5000ms
  latency-target: 1-2ms
```

---

## RabbitMQ Configuration

```yaml
rabbitmq:
  exchange: qr.tx.update
  queues:
    dispatch: qr.tx.update.dispatch
    dlq: qr.tx.update.dlq
  retry-backoff: 15s → 60s → 5m → 15m → 1h
  attempt-limit: 5
```

---

## Security Configuration

### JWS/JWE Settings

```yaml
security:
  signing-version: "2"
  algorithm: SHA256withRSA
  key-size: 2048
  encryption: RSA-OAEP-256 + A256GCM
  key-storage: file-system
  key-permissions: 600/700
```

### mTLS Configuration

```yaml
mtls:
  enabled: true
  certificate-pinning: true
  tls-version: 1.2+
```

---

## Monitoring Configuration

### Metrics Collection

```yaml
monitoring:
  metrics:
    - TPS (transactions per second)
    - p95/p99 latency
    - error rate
    - circuit breaker state
    - retry count
    - Redis/DB latency
  sampling-rate: 100%
  retention: 30 days
```

### Alerting Thresholds

| Metric | Warning | Critical |
|--------|---------|----------|
| Network Error Rate | > 5% (5 min) | > 15% (5 min) |
| Timeout Rate | > 5% (5 min) | > 10% (5 min) |
| Availability | < 99.9% | < 99.5% |

---

## Logging Configuration

```yaml
logging:
  format: JSON
  level: INFO
  pii-masking: true
  correlation-id: traceId/pspTransactionId
  retention: 90 days
```

---

## Disaster Recovery Configuration

```yaml
disaster-recovery:
  rpo: 5 minutes
  rto: 30 minutes
  backup-frequency: daily
  retention: 30 days
  hot-standby: Redis/Oracle
```

---

## Environment Variables

### Required Environment Variables

```bash
# Database
DB_HOST=oracle-host
DB_PORT=1521
DB_NAME=psp_db
DB_USERNAME=psp_user
DB_PASSWORD=secure_password

# Redis
REDIS_HOST=redis-host
REDIS_PORT=6379
REDIS_PASSWORD=redis_password

# RabbitMQ
RABBITMQ_HOST=rabbitmq-host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=psp_user
RABBITMQ_PASSWORD=rabbitmq_password

# Security
JWS_PRIVATE_KEY_PATH=/opt/psp/keys/private.pem
JWS_PUBLIC_KEY_PATH=/opt/psp/keys/public.pem
OPERATOR_JWKS_URL=https://operator.example.com/.well-known/jwks.json

# Operator API
OPERATOR_BASE_URL=https://operator-api.example.com
OPERATOR_PSP_TOKEN=psp_auth_token
OPERATOR_PSP_ID=psp_identifier
```

---

## Configuration Validation

### Startup Checks

1. **Database Connectivity:** Verify Oracle connection
2. **Redis Connectivity:** Verify Redis connection and latency
3. **RabbitMQ Connectivity:** Verify message broker connection
4. **Security Keys:** Verify JWS key files exist and are readable
5. **Operator API:** Verify operator endpoint accessibility

### Runtime Validation

1. **Performance Metrics:** Monitor against targets
2. **Error Rates:** Track against thresholds
3. **Resource Usage:** Monitor CPU, memory, connections
4. **Security:** Verify certificate validity

---

## Related Documentation

- [Observability](observability.md) - Monitoring and logging setup
- [Resilience](../reliability/resilience.md) - Resilience patterns
- [Network Exceptions](../reliability/network-exceptions.md) - Error handling
- [Security](../security/crypto.md) - Security configuration

---

**Last Updated:** 2024-01-XX  
**Version:** 1.0  
**Maintainer:** PSP Team
