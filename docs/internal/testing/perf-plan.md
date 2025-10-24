## Load Testing (k6)

> **📋 Reference:** For complete performance targets, see [Configuration Reference](../runtime/configuration-reference.md#performance-targets)

**Testing Goals:**
- TPS: 5,000 (peak load)
- p95 Latency: ≤ 150 ms
- p99 Latency: ≤ 300 ms

**Test Scenarios:**
- check→create→execute flow
- UPDATE/GET operations on failures
- Error handling and retry scenarios

**Monitoring:**
- System resources (CPU, memory, network)
- Redis/DB performance metrics
- Error rates and retry counts


