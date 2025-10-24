## Outbox Pattern

- UPDATE events are written to outbox_events in the same transaction as business changes.
- Publisher reads outbox, publishes to RabbitMQ and marks delivery.
- Semantics: at least once; idempotent consumer guarantees effectively once.


