# ADR-0002: Redis Mandatory

Decision: Redis included as mandatory component (idempotency, cache, limits, locks).

Motives: p95 ≤ 150 ms at 5k TPS, database offloading, resilience.


