## Resilience

- Timeouts (operator): connect=5000ms, read=30000ms, write=30000ms, response=60000ms
- Retries: 2–3 на 5xx/сетевые (502/503/504), экспоненциальная задержка 1s → 2s → 4s (max 10s)
- Circuit breaker: sliding window 20–50 req
- Bulkhead: ограничение параллелизма


