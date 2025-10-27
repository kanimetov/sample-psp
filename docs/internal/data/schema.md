## Data Schema (Oracle)

### Main Tables

#### operations
Primary unified table for storing all PSP operations (check, create, execute, update).

**Primary Keys:**
- `id` (NUMBER) - Auto-generated sequence ID

**Unique Constraints:**
- `psp_transaction_id` (VARCHAR2(50), NOT NULL) - PSP's unified transaction identifier
- `payment_session_id` (VARCHAR2(50)) - Payment session linking check and payment operations
- `transaction_id` (VARCHAR2(32)) - Operator's transaction ID (nullable for CHECK operations)
- `receipt_id` (VARCHAR2(20)) - Unified receipt identifier (nullable for CHECK operations)

**Core Fields:**
- `operation_type` (NUMBER(1), NOT NULL) - CHECK(10), CREATE(20), EXECUTE(30), UPDATE(40)
- `transfer_direction` (VARCHAR2(3), NOT NULL) - IN, OUT, OWN
- `status` (NUMBER(1)) - Transaction status (nullable for CHECK operations)
- `transaction_type` (NUMBER(1)) - Transaction type (nullable for CHECK operations)

**QR Attributes:**
- `qr_type` (VARCHAR2(32), NOT NULL) - staticQr or dynamicQr
- `merchant_provider` (VARCHAR2(32), NOT NULL) - Merchant provider identifier
- `merchant_id` (VARCHAR2(32)) - Service provider name
- `service_id` (VARCHAR2(32)) - Service code
- `service_name` (VARCHAR2(32)) - Service name
- `beneficiary_account_number` (VARCHAR2(32)) - Payer identifier within service
- `merchant_code` (NUMBER(10), NOT NULL) - Merchant MCC code
- `currency_code` (VARCHAR2(3), NOT NULL) - Always '417' for KGS
- `qr_transaction_id` (VARCHAR2(32)) - Transaction ID from QR
- `qr_comment` (VARCHAR2(99)) - Payment comment
- `qr_link_hash` (VARCHAR2(4), NOT NULL) - Last 4 symbols of payment link hash
- `beneficiary_name` (VARCHAR2(100)) - Beneficiary name from check response

**Financial Fields:**
- `amount` (NUMBER(19), NOT NULL) - Amount in tyiyn
- `customer_type` (VARCHAR2(1), NOT NULL) - 1=Individual, 2=Corporate

**Audit Fields:**
- `request_hash` (VARCHAR2(255)) - Request hash for signature verification
- `api_version` (VARCHAR2(10)) - API version used
- `created_at` (TIMESTAMP, NOT NULL) - Creation timestamp
- `updated_at` (TIMESTAMP) - Last update timestamp
- `executed_at` (TIMESTAMP) - Execution timestamp
- `last_status_update_at` (TIMESTAMP) - Last status change timestamp

**Retry & Error Handling:**
- `error_message` (VARCHAR2(500)) - Error details
- `retry_count` (NUMBER(10), NOT NULL, DEFAULT 0) - Current retry count
- `max_retries` (NUMBER(10), NOT NULL, DEFAULT 3) - Maximum retry attempts
- `is_final` (NUMBER(1), NOT NULL, DEFAULT 0) - Final status flag

**Metadata:**
- `created_by` (VARCHAR2(50)) - Creator identifier
- `updated_by` (VARCHAR2(50)) - Last updater identifier

#### extra_data
Stores additional key-value metadata associated with operations.

**Fields:**
- `id` (NUMBER) - Primary key
- `key_name` (VARCHAR2(64), NOT NULL) - Key name
- `value_data` (VARCHAR2(256), NOT NULL) - Value data
- `order_index` (NUMBER(10)) - Display order
- `operation_id` (NUMBER(19), NOT NULL, FK) - References operations.id
- `created_at` (TIMESTAMP, NOT NULL) - Creation timestamp
- `updated_at` (TIMESTAMP) - Last update timestamp
- `is_active` (NUMBER(1), NOT NULL, DEFAULT 1) - Active flag
- `data_type` (VARCHAR2(20)) - Data type (STRING, NUMBER, BOOLEAN, JSON, etc.)
- `description` (VARCHAR2(200)) - Field description

### Indexes

**operations table:**
- `idx_operations_operation_type` - Filter by operation type
- `idx_operations_transfer_direction` - Filter by transfer direction
- `idx_operations_status` - Filter by status
- `idx_operations_created_at` - Sort by creation date
- `idx_operations_executed_at` - Filter by execution date
- `idx_operations_merchant_code` - Filter by merchant
- `idx_operations_qr_link_hash` - Filter by QR hash
- `idx_operations_customer_type` - Filter by customer type
- `idx_operations_psp_transaction_id` - Lookup by PSP transaction ID
- `idx_operations_payment_session_id` - Lookup by payment session

**extra_data table:**
- `idx_extra_data_operation_id` - Join with operations
- `idx_extra_data_key_name` - Filter by key name

### Sequences

- `operations_seq` - Auto-increment ID for operations table
- `extra_data_seq` - Auto-increment ID for extra_data table

### Design Notes

- **Unified table design**: All operations (check, create, execute, update) are stored in the `operations` table
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
  - `payment_session_id`: Generated during CHECK operation and used to link subsequent payment operations
- **Constraints**: Check constraints enforce valid values for operation_type, transfer_direction, customer_type, qr_type, and currency_code

**Note:** Signature/encryption keys and JWKS are not stored in the database — they are placed on the server file system and cached in memory/Redis.


