## Idempotency

> **📋 Reference:** For complete Redis key patterns and TTL values, see [Redis Schema Reference](../data/redis-schema.md)

### Quick Summary

**Redis Key Patterns:**
- `idem:check:{pspId}:{merchantProvider}:{qrTxId}:{amount}` (TTL: 120s)
- `idem:create:{pspTransactionId}` (TTL: 24h)
- `idem:execute:{transactionId}` (TTL: 24h)
- `idem:update:{transactionId}:{status}` (TTL: 24h)

**Behavior:**
- Duplicate UPDATE: don't change terminal status; respond 200
- All operations use `SET NX + TTL` pattern

### Implementation

```redis
# Check operation idempotency
SET idem:check:{pspId}:{merchantProvider}:{qrTxId}:{amount} "1" NX EX 120

# Create operation idempotency  
SET idem:create:{pspTransactionId} "1" NX EX 86400

# Execute operation idempotency
SET idem:execute:{transactionId} "1" NX EX 86400

# Update operation idempotency
SET idem:update:{transactionId}:{status} "1" NX EX 86400
```

For complete Redis schema details, see [Redis Schema Reference](../data/redis-schema.md).


