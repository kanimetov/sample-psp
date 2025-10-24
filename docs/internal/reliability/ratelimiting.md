## Rate Limiting

> **📋 Reference:** For complete Redis key patterns and implementation details, see [Redis Schema Reference](../data/redis-schema.md)

### Quick Summary

**Redis Key Patterns:**
- `rl:{pspId}:{minute}` (TTL: 60s) - Per-PSP rate limiting
- `rl:tx:{transactionId}` (TTL: 300s) - Per-transaction rate limiting

**Rate Limits:**
- PSP: 1000 requests/minute
- Transaction: 10 requests/5 minutes

**Goals:** Protection from GET/UPDATE storms; fair distribution

### Implementation

```redis
# PSP rate limiting (token bucket)
ZADD rl:{pspId}:{minute} {timestamp} {request_id}
ZREMRANGEBYSCORE rl:{pspId}:{minute} 0 {current_timestamp - 60}
ZCARD rl:{pspId}:{minute}

# Transaction rate limiting
ZADD rl:tx:{transactionId} {timestamp} {request_id}
ZREMRANGEBYSCORE rl:tx:{transactionId} 0 {current_timestamp - 300}
ZCARD rl:tx:{transactionId}
```

For complete Redis schema details, see [Redis Schema Reference](../data/redis-schema.md).


