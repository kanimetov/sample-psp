## Data Schema (Oracle)

### Main Tables

- **transactions**: pk id, unique transaction_id, unique psp_transaction_id, unique receipt_id, unique qr_transaction_id, status, amount, currency_code ('417'), qr_type, merchant_provider, merchant_id, service_id, service_name, beneficiary_account_number, merchant_code, qr_comment, customer_type, qr_link_hash, transaction_type, beneficiary_name, request_hash, api_version, transfer_direction (IN/OUT/OWN), created_at, updated_at, executed_at, last_status_update_at, processed_at, response_status, error_message, retry_count, max_retries, is_final, created_by, updated_by, ip_address, user_agent

- **extra_data**: pk id, key_name, value_data, order_index, transaction_id (FK), created_at, updated_at, is_active, data_type, description

### Indexes

- **transactions**: idx_status, idx_created_at, idx_executed_at, idx_merchant_code, idx_qr_link_hash, idx_transfer_direction, idx_customer_type, idx_processed_at
- **extra_data**: idx_transaction_id, idx_key_name

### Sequences

- TRANSACTIONS_SEQ
- EXTRA_DATA_SEQ

### Design Notes

- **Single table design**: All operations (check, create, execute) are stored in the consolidated `transactions` table
- **Transfer directions**: IN (incoming to PSP), OUT (outgoing from PSP), OWN (internal PSP transfers)
- **Customer types**: 1=Individual, 2=Corporate
- **Deprecated**: `check_requests` table has been consolidated into `transactions` table

Note: signature/encryption keys are not stored in the database — they are placed on the server file system and cached in memory/Redis.


