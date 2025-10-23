## Схема данных (Oracle)

- qr_tx: pk id, unique psp_transaction_id, idx operator_tx_id, status, amount, commission, currency ('417'), qr_tx_id, merchant_provider, qr_link_hash, transaction_type, request_hash, request_started_at, request_finished_at, last_error_code, last_error_message, last_error_at, created_at, updated_at
- qr_tx_audit: tx_id, direction (REQ|RESP), stage (CHECK|CREATE|EXECUTE|UPDATE|GET), payload_json, created_at
- outbox_events: event_id, type, payload_json, status, attempts, next_run_at, created_at

Примечание: ключи подписи/шифрования не хранятся в БД — размещаются на файловой системе сервера и кешируются в памяти/Redis.


