## Capacity & Performance

- Target: 5k TPS, p95 ≤ 150 ms
- Instances: 6–10 PSP nodes, 2–4 vCPU, 2–4 GB RAM
- DB: pooled ≤ 50 conns/node; partitions by day/week
- Redis: latency 1–2 ms; pools 200–400
- Timeouts: 200–400 ms; retry 2–3; circuit breaker


