## Миграции (Flyway)

- V1__init.sql: qr_tx, qr_tx_audit, integrations_keys, outbox_events
- Индексы: psp_transaction_id (unique), operator_tx_id, (merchant_provider, qr_tx_id)
- Партиции по created_at (день/неделя)


