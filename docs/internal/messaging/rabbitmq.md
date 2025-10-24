## RabbitMQ

> **📋 Reference:** For complete RabbitMQ configuration, see [Configuration Reference](../runtime/configuration-reference.md#rabbitmq-configuration)

**Quick Summary:**

**Exchange & Queues:**
- Exchange: `qr.tx.update` (topic)
- Queues: `qr.tx.update.dispatch`, `qr.tx.update.dlq`

**Message Format:**
```json
{
  "transactionId": "uuid",
  "status": "string",
  "amount": "number",
  "commission": "number?",
  "pspTransactionId": "string?",
  "receiptId": "string?",
  "attempt": "number"
}
```

**Retry Strategy:**
- Exponential backoff: 15s → 60s → 5m → 15m → 1h
- Attempt limit with DLQ
- Routing key by transactionId

For complete RabbitMQ configuration details, see [Configuration Reference](../runtime/configuration-reference.md).


