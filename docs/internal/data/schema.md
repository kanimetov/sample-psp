## Data Schema (Oracle)

### Main Tables

- **operations**: pk id, unique psp_transaction_id, operation_type (CHECK/CREATE/EXECUTE/UPDATE), transfer_direction (IN/OUT/OWN), unique transaction_id (operator's ID), unique receipt_id, qr_type, merchant_provider, merchant_id, service_id, service_name, beneficiary_account_number, merchant_code, currency_code ('417'), qr_transaction_id, qr_comment, customer_type, amount, qr_link_hash, transaction_type, status, beneficiary_name, request_hash, api_version, created_at, updated_at, executed_at, last_status_update_at, error_message, retry_count, max_retries, is_final, created_by, updated_by, ip_address, user_agent

- **extra_data**: pk id, key_name, value_data, order_index, operation_id (FK), created_at, updated_at, is_active, data_type, description

### Indexes

- **operations**: idx_operation_type, idx_transfer_direction, idx_status, idx_created_at, idx_executed_at, idx_merchant_code, idx_qr_link_hash, idx_customer_type, idx_psp_transaction_id
- **extra_data**: idx_operation_id, idx_key_name

### Sequences

- OPERATIONS_SEQ
- EXTRA_DATA_SEQ

### Design Notes

- **Unified table design**: All operations (check, create, execute, update) are stored in the consolidated `operations` table
- **Operation types**: CHECK (10), CREATE (20), EXECUTE (30), UPDATE (40)
- **Transfer directions**: IN (incoming to PSP), OUT (outgoing from PSP), OWN (internal PSP transfers)
- **Customer types**: 1=Individual, 2=Corporate
- **ID Strategy**:
  - `psp_transaction_id`: PSP's business identifier (UNIQUE, NOT NULL)
    - For INCOMING: uses senderTransactionId from request
    - For OUTGOING: generated UUID
    - For CHECK: always generated UUID
  - `transaction_id`: Operator's transaction ID (UNIQUE when present, NULL for CHECK)
  - `receipt_id`: Unified receipt ID (UNIQUE when present, NULL for CHECK)
    - For INCOMING: uses senderReceiptId from request
    - For OUTGOING: generated receipt ID
- **Replaced**: `check_requests` and `transactions` tables have been completely replaced by `operations` table

Note: signature/encryption keys are not stored in the database — they are placed on the server file system and cached in memory/Redis.


