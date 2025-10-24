# Redis Schema Reference

> **Single Source of Truth** for all Redis key patterns, TTL values, and data structures. All other documents should reference this file.

## Overview

Redis is a **mandatory** component in the PSP system, used for idempotency, caching, rate limiting, and distributed locking.

## Key Patterns

### 1. Idempotency Keys

**Purpose:** Prevent duplicate operations

| Operation | Key Pattern | TTL | Description |
|-----------|-------------|-----|-------------|
| Check | `idem:check:{pspId}:{merchantProvider}:{qrTxId}:{amount}` | 120s | Check operation idempotency |
| Create | `idem:create:{pspTransactionId}` | 24h | Create operation idempotency |
| Execute | `idem:execute:{transactionId}` | 24h | Execute operation idempotency |
| Update | `idem:update:{transactionId}:{status}` | 24h | Update operation idempotency |

**Example:**
```
idem:check:PSP001:DEMO_MERCHANT:QR123:100000
idem:create:PSP-TX-123456
idem:execute:fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7
idem:update:fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7:50
```

---

### 2. Rate Limiting Keys

**Purpose:** Control request frequency and prevent abuse

| Type | Key Pattern | TTL | Description |
|------|-------------|-----|-------------|
| PSP Rate Limit | `rl:{pspId}:{minute}` | 60s | Per-PSP rate limiting |
| Transaction Rate Limit | `rl:tx:{transactionId}` | 300s | Per-transaction rate limiting |

**Example:**
```
rl:PSP001:2024-01-15-14-30
rl:tx:fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7
```

**Rate Limits:**
- PSP: 1000 requests/minute
- Transaction: 10 requests/5 minutes

---

### 3. Cache Keys

**Purpose:** Store frequently accessed data

| Type | Key Pattern | TTL | Description |
|------|-------------|-----|-------------|
| Status Cache | `status:{transactionId}` | 60s | Transaction status cache |
| JWKS Cache | `jwks:operator:{kid}` | 1h | Operator public keys |
| PSP Token Cache | `token:psp:{pspId}` | 24h | PSP authentication tokens |

**Example:**
```
status:fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7
jwks:operator:key-123
token:psp:PSP001
```

---

### 4. Lock Keys

**Purpose:** Distributed locking for critical operations

| Operation | Key Pattern | TTL | Description |
|-----------|-------------|-----|-------------|
| Update Lock | `lock:update:{transactionId}` | 30s | Prevent concurrent updates |
| Processing Lock | `lock:process:{pspTransactionId}` | 60s | Prevent concurrent processing |

**Example:**
```
lock:update:fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7
lock:process:PSP-TX-123456
```

---

## Data Structures

### 1. String Operations

**Idempotency Keys:**
```redis
SET idem:check:{key} "1" NX EX 120
SET idem:create:{key} "1" NX EX 86400
```

**Cache Values:**
```redis
SET status:{transactionId} "{status_data}" EX 60
SET jwks:operator:{kid} "{jwks_data}" EX 3600
```

### 2. Hash Operations

**Transaction Status:**
```redis
HSET status:{transactionId} status "SUCCESS" amount 100000 timestamp "2024-01-15T14:30:00Z"
EXPIRE status:{transactionId} 60
```

### 3. Sorted Set Operations

**Rate Limiting (Token Bucket):**
```redis
ZADD rl:{pspId}:{minute} {timestamp} {request_id}
ZREMRANGEBYSCORE rl:{pspId}:{minute} 0 {current_timestamp - 60}
ZCARD rl:{pspId}:{minute}
```

---

## TTL Strategy

### TTL Values by Purpose

| Purpose | TTL Range | Rationale |
|---------|-----------|-----------|
| Idempotency (Check) | 120s | Short-lived, quick operations |
| Idempotency (Create/Execute/Update) | 24h | Long-lived, prevent duplicates |
| Rate Limiting | 60s-300s | Sliding window for rate control |
| Status Cache | 60s | Balance between performance and freshness |
| JWKS Cache | 1h | Key rotation frequency |
| Locks | 30s-60s | Prevent deadlocks |

### TTL Management

**Automatic Expiration:**
- All keys have explicit TTL
- No manual cleanup required
- Memory usage bounded

**TTL Monitoring:**
```redis
# Check remaining TTL
TTL key_name

# Check if key exists
EXISTS key_name
```

---

## Performance Considerations

### Memory Usage

**Estimated Memory per Key:**
- Idempotency keys: ~50 bytes
- Status cache: ~200 bytes
- JWKS cache: ~2KB
- Rate limiting: ~100 bytes

**Total Memory Estimation:**
- 10,000 TPS × 24h = ~864M operations/day
- Estimated Redis memory: 2-4 GB

### Latency Targets

| Operation | Target Latency | Measurement |
|-----------|----------------|-------------|
| SET operations | < 1ms | 95th percentile |
| GET operations | < 1ms | 95th percentile |
| Complex operations | < 2ms | 95th percentile |

---

## Redis Configuration

### Connection Pool

```yaml
redis:
  connection-pool:
    min-size: 10
    max-size: 200
    timeout: 5000ms
  cluster:
    enabled: false  # Single instance for MVP
  persistence:
    rdb: true
    aof: false
```

### Memory Configuration

```yaml
redis:
  maxmemory: 4gb
  maxmemory-policy: allkeys-lru
  save: "900 1 300 10 60 10000"
```

---

## Monitoring and Alerting

### Key Metrics

| Metric | Threshold | Action |
|--------|-----------|--------|
| Memory Usage | > 80% | Scale up |
| Hit Rate | < 95% | Investigate |
| Latency p95 | > 2ms | Investigate |
| Connection Count | > 150 | Scale up |

### Health Checks

```redis
# Basic connectivity
PING

# Memory usage
INFO memory

# Key count by pattern
EVAL "return redis.call('keys', 'idem:*')" 0
```

---

## Error Handling

### Common Redis Errors

| Error | Cause | Resolution |
|-------|-------|------------|
| `OOM` | Memory limit exceeded | Scale up or optimize keys |
| `NOAUTH` | Authentication failed | Check credentials |
| `CONNECTION_REFUSED` | Redis unavailable | Check Redis service |
| `TIMEOUT` | Operation timeout | Check network/load |

### Fallback Strategy

**When Redis is unavailable:**
1. Log warning
2. Continue without idempotency (accept duplicates)
3. Disable caching
4. Use database for rate limiting

---

## Security Considerations

### Access Control

```yaml
redis:
  authentication:
    enabled: true
    password: "secure_redis_password"
  network:
    bind: "127.0.0.1"
    port: 6379
  tls:
    enabled: false  # For internal network only
```

### Data Protection

- **No PII in Redis:** Only transaction IDs and status
- **Encryption at Rest:** Redis AOF/RDB encryption
- **Network Security:** Internal network only
- **Access Logging:** All Redis operations logged

---

## Backup and Recovery

### Backup Strategy

```bash
# RDB backup
redis-cli BGSAVE

# Point-in-time backup
redis-cli --rdb /backup/redis-$(date +%Y%m%d).rdb
```

### Recovery Process

1. Stop Redis service
2. Restore RDB file
3. Start Redis service
4. Verify key patterns
5. Monitor performance

---

## Related Documentation

- [Configuration Reference](../runtime/configuration-reference.md) - Redis configuration
- [Idempotency](../reliability/idempotency.md) - Idempotency implementation
- [Rate Limiting](../reliability/ratelimiting.md) - Rate limiting strategy
- [Architecture Decisions](../design/decisions/ADR-0002-redis-mandatory.md) - Redis mandatory decision

---

**Last Updated:** 2024-01-XX  
**Version:** 1.0  
**Maintainer:** PSP Team
