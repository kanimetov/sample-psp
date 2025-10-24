## Success metrics (KPI)

> **📋 Reference:** For complete performance targets and monitoring configuration, see [Configuration Reference](../runtime/configuration-reference.md#performance-targets)

**Key Performance Indicators:**

| Metric | Target | Measurement Period |
|--------|--------|-------------------|
| Availability | ≥ 99.95% | Monthly |
| p95 Latency | ≤ 150 ms | Hot path |
| Error Rate | ≤ 0.2% | 4xx excluded |
| Recovery Time | ≤ 30 min | RTO |
| Data Loss Window | ≤ 5 min | RPO |

**Additional Metrics:**
- TPS: 5,000 (peak load)
- p99 Latency: ≤ 300 ms
- Circuit breaker state monitoring
- Redis/DB latency tracking

For complete performance targets and monitoring setup, see [Configuration Reference](../runtime/configuration-reference.md).


