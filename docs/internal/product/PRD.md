## PRD: PSP (Sender/Beneficiary) according to DKIBQR

### 1. Context and Goals
- Implement PSP supporting outgoing transfers (Sender) and payment acceptance (Beneficiary) according to DKIBQR.
- Performance goals: 5k TPS peak, p95 ≤ 150 ms, availability 99.95%.
- Infrastructure: VMs; Oracle DB; Redis (mandatory); RabbitMQ; H‑SIGNING‑VERSION=2 (only), JWS/JWE (RSA‑2048), mTLS.

### 2. Scope
- Included:
  - Sender API: tx/check, tx/create, tx/execute/{transactionId}, tx/get/{transactionId} (fallback without version).
  - Beneficiary API (/in/...): accepting check/create/execute/get and mandatory UPDATE acceptance.
  - UPDATE:
    - Incoming UPDATE from Operator — always accept.
    - Outgoing UPDATE to Operator — send if execute didn't return final status.
    - GET status — only on timeout.
- Excluded: billing/interbank settlements, frontend.

### 3. Actors
- Bank client channels (mobile/internet banking) — Sender.
- Interaction Operator — counterparty.
- Support operators (monitoring).

### 4. Processes (high-level)
- Sender: Scan QR → Check → Create → Execute → (receive UPDATE or GET on timeout).
- Beneficiary: Accept Check/Create/Execute → Execution → (if no final status) outgoing UPDATE to Operator.

### 5. Endpoints

**PSP System has only two communication directions:**

#### Outgoing (PSP → Operator) - OperatorClient.java
- POST /psp/api/v1/payment/qr/{version}/tx/check
- POST /psp/api/v1/payment/qr/{version}/tx/create
- POST /psp/api/v1/payment/qr/{version}/tx/execute/{transactionId}
- GET  /psp/api/v1/payment/qr/{version}/tx/get/{transactionId}
- POST /psp/api/v1/payment/qr/{version}/tx/update/{transactionId}

#### Incoming (Operator → PSP) - IncomingController.java
- POST /in/qr/{version}/tx/check
- POST /in/qr/{version}/tx/create
- POST /in/qr/{version}/tx/execute/{transactionId}
- POST /in/qr/{version}/tx/update/{transactionId}

**Note:** No direct client-facing APIs. PSP acts as intermediary between Operator and bank systems.

Notes on headers/security:
- Only H‑SIGNING‑VERSION=2 is supported. H‑HASH — JWS signature according to v2 rules.
- POST bodies: JWE (RSA‑OAEP‑256/A256GCM) when required by protocol.
- GET: usually without JWE; signature according to v2.

### 6. Data Contracts (brief)
- tx/check (request): qrType, merchantProvider, merchantCode, currencyCode=417, qrTransactionId, amount (tyiyns), qrLinkHash (4), customerType (1|2), extra[≤3].
- tx/check (response): beneficiaryName, transactionType, extra[].
- tx/create/execute: use pspTransactionId, receiptId and fields according to specification.
- get/{transactionId} (response): { transactionId, status, transactionType, amount, commission, senderTransactionId, senderReceiptId, createdDate, executedDate }.
- update (payload): { transactionId, status, amount, commission?, pspTransactionId?, receiptId?, createdDate?, executedDate? } — final composition to be clarified with Operator.

### 7. Non-Functional Requirements (NFR)
> **📋 Reference:** For complete performance targets and infrastructure configuration, see [Configuration Reference](../runtime/configuration-reference.md)

**Quick Summary:**
- 5k TPS total; horizontal scaling (2 instances)
- p95 ≤ 150 ms on hot path
- 99.95% availability; resilience to operator failures

### 8. Redis (mandatory)
> **📋 Reference:** For complete Redis key patterns and TTL values, see [Redis Schema Reference](../data/redis-schema.md)

**Quick Summary:**
- Idempotency keys with TTL (120s-24h)
- Rate limiting with token bucket
- Status and key caching
- Distributed locking

### 9. RabbitMQ
> **📋 Reference:** For complete RabbitMQ configuration, see [RabbitMQ Configuration](../messaging/rabbitmq.md)

**Quick Summary:**
- Exchange: qr.tx.update
- Queues: dispatch + DLQ
- Exponential backoff retries
- Outbox: event persistence in DB and MQ publication (exactly‑once at domain level)

### 10. Data (Oracle)
- operations (unified table for all operations: check, create, execute, update)
  - PK: id
  - Unique: psp_transaction_id, payment_session_id, transaction_id, receipt_id
  - Fields: operation_type (CHECK/CREATE/EXECUTE/UPDATE), transfer_direction (IN/OUT/OWN), QR attributes, status, timestamps, retry logic
  - Indexes: operation_type, transfer_direction, status, created_at, executed_at, merchant_code, qr_link_hash, customer_type
- extra_data (key-value pairs for additional operation metadata)
  - FK: operation_id → operations.id
- outbox_events (for asynchronous event publishing via RabbitMQ)

### 11. Security
- mTLS to operator; public key pinning.
- JWS/JWE (RSA‑2048) v2; private/public keys loaded to server directory (outside repository), access rights 600/700; document path/rotation.
- Logs with PII masking; strict JSON canonicalization for v2 signature.

### 12. Observability
- OpenTelemetry (traces/metrics/logs). Metrics: TPS, p95/p99, error rate, CB state, retry count, Redis/DB latency.
- JSON logs with traceId/pspTransactionId correlation. Alerts on DLQ/timeouts/availability.

### 13. Operations and Deployment
- VMs + NGINX/HAProxy; blue/green/canary; health/readiness.
- Backups: DB and keys; DR: RPO ≤ 5 min, RTO ≤ 30 min.
- Secret/key management: files on server + rotation SOP.

### 14. Acceptance Criteria
- Correctness of check/create/execute/update/get; error code mapping.
- Idempotency and no duplicates; resilient UPDATE retries.
- Performance goals achieved; JWS/JWE v2 verified.

### 15. Risks/Questions
- Possible DKIBQR changes; operator limitations (SLA/rate‑limit).
- Clarify final field composition in UPDATE payload.


