## Capacity & Performance

> **📋 Reference:** For complete performance targets and infrastructure configuration, see [Configuration Reference](../runtime/configuration-reference.md)

### Quick Summary

**Performance Targets:**
- TPS: 5,000 (peak)
- p95 Latency: ≤ 150 ms
- p99 Latency: ≤ 300 ms
- Availability: ≥ 99.95%

**Infrastructure:**
- Instances: 6–10 PSP nodes
- CPU: 2–4 vCPU per node
- RAM: 2–4 GB per node
- DB Connections: ≤ 50 per node
- Redis Latency: 1–2 ms
- Redis Pools: 200–400

**Timeouts & Resilience:**
- See [Configuration Reference](../runtime/configuration-reference.md#timeout-configuration)
- Retry: 2–3 attempts
- Circuit breaker: enabled

For complete configuration details, see [Configuration Reference](../runtime/configuration-reference.md).


