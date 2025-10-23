## RabbitMQ

- Exchange: qr.tx.update (topic)
- Queues: qr.tx.update.dispatch, qr.tx.update.dlq
- Message: { transactionId, status, amount, commission?, pspTransactionId?, receiptId?, attempt }
- Backoff: 15s → 60s → 5m → 15m → 1h; лимит попыток; DLQ
- Ordering: routing key по transactionId


