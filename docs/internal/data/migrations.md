## Migrations (Flyway)

- V1__init.sql: qr_tx, qr_tx_audit, integrations_keys, outbox_events
- Indexes: psp_transaction_id (unique), operator_tx_id, (merchant_provider, qr_tx_id)
- Partitions by created_at (day/week)


