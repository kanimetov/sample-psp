## Resilience

> **📋 Reference:** For complete configuration values, see [Configuration Reference](../runtime/configuration-reference.md)

### Quick Summary

- **Timeouts:** See [Configuration Reference](../runtime/configuration-reference.md#timeout-configuration)
- **Retries:** 2–3 on 5xx/network (502/503/504), exponential delay 1s → 2s → 4s (max 10s)
- **Circuit breaker:** sliding window 20–50 req
- **Bulkhead:** concurrency limitation

### Implementation Details

**Timeout Configuration:**
- Connection: 5000ms (dev/test), 3000ms (prod)
- Read: 30000ms (dev/test), 20000ms (prod)
- Write: 30000ms (dev/test), 20000ms (prod)
- Response: 60000ms (dev/test), 45000ms (prod)

**Retry Strategy:**
- Max attempts: 3
- Initial delay: 1s
- Backoff multiplier: 2x
- Max delay: 10s

**Circuit Breaker:**
- Sliding window: 20-50 requests
- Failure threshold: 50%
- Timeout: 60s

For complete configuration details, see [Configuration Reference](../runtime/configuration-reference.md).


