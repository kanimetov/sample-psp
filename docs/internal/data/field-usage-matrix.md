# Field Usage Matrix for Operations Table

This document describes which fields are populated for each operation type and transfer direction in the unified `operations` table.

## Operation Types

- **CHECK (10)**: Verification of QR code details before creating transaction
- **CREATE (20)**: Creating new transaction in the system
- **EXECUTE (30)**: Executing created transaction
- **UPDATE (40)**: Updating transaction status

## Transfer Directions

- **IN**: Incoming to PSP (Operator → PSP, beneficiary side)
- **OUT**: Outgoing from PSP (PSP → Operator, sender side)
- **OWN**: Internal PSP transfers

## Field Usage Matrix

| Field | CHECK-IN | CHECK-OUT | CREATE-IN | CREATE-OUT | EXECUTE | UPDATE |
|-------|----------|-----------|-----------|------------|---------|--------|
| **Core Fields** |
| `id` | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) |
| `psp_transaction_id` | ✓ (generated) | ✓ (generated) | ✓ (from sender) | ✓ (generated) | ✓ (existing) | ✓ (existing) |
| `operation_type` | CHECK | CHECK | CREATE | CREATE | EXECUTE | UPDATE |
| `transfer_direction` | IN | OUT | IN | OUT | IN/OUT | IN/OUT |
| `transaction_id` | - | - | ✓ (from op) | ✓ (to op) | ✓ (existing) | ✓ (existing) |
| `receipt_id` | - | - | ✓ (from sender) | ✓ (generated) | - | - |
| **QR Data Fields** |
| `qr_type` | ✓ | ✓ | ✓ | ✓ | - | - |
| `merchant_provider` | ✓ | ✓ | ✓ | ✓ | - | - |
| `merchant_id` | ✓ | ✓ | ✓ | ✓ | - | - |
| `service_id` | ✓ | ✓ | ✓ | ✓ | - | - |
| `service_name` | ✓ | ✓ | ✓ | ✓ | - | - |
| `beneficiary_account_number` | ✓ | ✓ | ✓ | ✓ | - | - |
| `merchant_code` | ✓ | ✓ | ✓ | ✓ | - | - |
| `currency_code` | ✓ | ✓ | ✓ | ✓ | - | - |
| `qr_transaction_id` | ✓ | ✓ | ✓ | ✓ | - | - |
| `qr_comment` | ✓ | ✓ | ✓ | ✓ | - | - |
| `amount` | ✓ | ✓ | ✓ | ✓ | - | - |
| `qr_link_hash` | ✓ | ✓ | ✓ | ✓ | - | - |
| **Transaction Fields** |
| `customer_type` | ✓ | ✓ | ✓ | ✓ | - | - |
| `transaction_type` | - | - | ✓ | ✓ | - | - |
| `status` | - | - | ✓ | ✓ | ✓ | ✓ |
| `beneficiary_name` | ✓ (response) | - | ✓ (response) | - | ✓ (response) | - |
| **System Fields** |
| `request_hash` | ✓ | ✓ | ✓ | ✓ | - | - |
| `api_version` | ✓ | ✓ | ✓ | ✓ | - | - |
| `created_at` | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) |
| `updated_at` | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) | ✓ (auto) |
| `executed_at` | - | - | - | - | ✓ | - |
| `last_status_update_at` | - | - | - | - | - | ✓ |
| **Error Handling** |
| `error_message` | ✓ (if error) | ✓ (if error) | ✓ (if error) | ✓ (if error) | ✓ (if error) | ✓ (if error) |
| `retry_count` | ✓ (default 0) | ✓ (default 0) | ✓ (default 0) | ✓ (default 0) | ✓ (existing) | ✓ (existing) |
| `max_retries` | ✓ (default 3) | ✓ (default 3) | ✓ (default 3) | ✓ (default 3) | ✓ (existing) | ✓ (existing) |
| `is_final` | ✓ (default false) | ✓ (default false) | ✓ (default false) | ✓ (default false) | ✓ (existing) | ✓ (existing) |
| **Audit Fields** |
| `created_by` | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) |
| `updated_by` | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) |
| `ip_address` | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) |
| `user_agent` | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) | ✓ (optional) |
| **Extra Data** |
| `extra_data` | ✓ (if present) | ✓ (if present) | ✓ (if present) | ✓ (if present) | - | - |

## ID Generation Strategy

### psp_transaction_id (UNIQUE, NOT NULL)
- **CHECK-IN**: Generated UUID (e.g., "CHECK-12345")
- **CHECK-OUT**: Generated UUID (e.g., "CHECK-67890")
- **CREATE-IN**: Uses `senderTransactionId` from incoming request
- **CREATE-OUT**: Generated UUID (e.g., "PSP-TX-abc123")
- **EXECUTE**: Uses existing psp_transaction_id from CREATE operation
- **UPDATE**: Uses existing psp_transaction_id from CREATE operation

### transaction_id (UNIQUE when present)
- **CHECK**: NULL (no operator transaction yet)
- **CREATE-IN**: Populated from operator's response
- **CREATE-OUT**: Populated from operator's response
- **EXECUTE**: Uses existing transaction_id from CREATE operation
- **UPDATE**: Uses existing transaction_id from CREATE operation

### receipt_id (UNIQUE when present)
- **CHECK**: NULL (no receipt yet)
- **CREATE-IN**: Uses `senderReceiptId` from incoming request
- **CREATE-OUT**: Generated receipt ID (e.g., "RCP-001")
- **EXECUTE**: NULL (no new receipt)
- **UPDATE**: NULL (no new receipt)

## Business Rules

1. **CHECK operations** can exist independently without leading to CREATE
2. **CREATE operations** must have all QR data fields populated
3. **EXECUTE operations** reference existing CREATE operations
4. **UPDATE operations** reference existing CREATE operations
5. **psp_transaction_id** is always generated/assigned by PSP system
6. **transaction_id** is assigned by operator system
7. **receipt_id** follows the direction: INCOMING uses sender's receipt, OUTGOING generates own receipt

## Validation Rules

- All operations must have `psp_transaction_id`, `operation_type`, `transfer_direction`
- CHECK operations must have all QR data fields
- CREATE operations must have all QR data fields + transaction fields
- EXECUTE/UPDATE operations must reference existing operations
- Customer type must be '1' (Individual) or '2' (Corporate)
- Currency code must be '417' (KGS)
- QR type must be 'staticQr' or 'dynamicQr'
