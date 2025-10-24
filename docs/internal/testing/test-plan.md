## Test Plan

- Unit: DTO validation, error mapping
- Crypto: JWS/JWE v2 signature/verification, encryption/decryption
- Contract: WireMock for operator APIs (check/create/execute/get/update)
- Integration: Redis idempotency, RabbitMQ retries
- Load: k6 scenarios up to 2× peak


