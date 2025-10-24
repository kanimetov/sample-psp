## Data Schema (Oracle)

### Main Tables

- **transactions**: pk id, unique transaction_id, unique psp_transaction_id, unique receipt_id, unique qr_transaction_id, status, amount, currency_code ('417'), qr_type, merchant_provider, merchant_id, service_id, service_name, beneficiary_account_number, merchant_code, qr_comment, customer_type, qr_link_hash, transaction_type, beneficiary_name, request_hash, api_version, created_at, updated_at, executed_at, last_status_update_at, error_message, retry_count, max_retries, is_final, created_by, updated_by, ip_address, user_agent

- **check_requests**: pk id, qr_type, merchant_provider, merchant_id, service_id, service_name, beneficiary_account_number, merchant_code, currency_code, qr_transaction_id, qr_comment, customer_type, amount, qr_link_hash, request_hash, api_version, created_at, updated_at, processed_at, response_status, error_message

- **extra_data**: pk id, key_name, value_data, order_index, check_request_id (FK), transaction_id (FK), created_at, updated_at, is_active, data_type, description

### Indexes

- **transactions**: idx_status, idx_created_at, idx_executed_at, idx_merchant_code, idx_qr_link_hash
- **check_requests**: idx_created_at, idx_qr_link_hash, idx_merchant_code, idx_qr_transaction_id  
- **extra_data**: idx_check_request_id, idx_transaction_id, idx_key_name

### Sequences

- TRANSACTIONS_SEQ
- CHECK_REQUESTS_SEQ  
- EXTRA_DATA_SEQ

Note: signature/encryption keys are not stored in the database — they are placed on the server file system and cached in memory/Redis.


