## Database Migrations

### Migration Strategy for Schema Consolidation

This document outlines the migration strategy for consolidating the `check_requests` table into the `transactions` table and adding new fields to support three transfer directions (IN, OUT, OWN).

### Migration Steps

#### 1. Add New Columns to Transactions Table

```sql
-- Add new columns to existing transactions table
ALTER TABLE transactions ADD (
    transfer_direction VARCHAR2(3) DEFAULT 'OUT' NOT NULL,
    processed_at TIMESTAMP,
    response_status VARCHAR2(20)
);

-- Add check constraints
ALTER TABLE transactions ADD CONSTRAINT chk_transfer_direction 
    CHECK (transfer_direction IN ('IN', 'OUT', 'OWN'));
```

#### 2. Migrate Data from check_requests to transactions

```sql
-- Migrate check_requests data to transactions table
INSERT INTO transactions (
    id, qr_type, merchant_provider, merchant_id, service_id, service_name,
    beneficiary_account_number, merchant_code, currency_code, qr_transaction_id,
    qr_comment, customer_type, amount, qr_link_hash, request_hash, api_version,
    transfer_direction, created_at, updated_at, processed_at,
    response_status, error_message
)
SELECT 
    id, qr_type, merchant_provider, merchant_id, service_id, service_name,
    beneficiary_account_number, merchant_code, currency_code, qr_transaction_id,
    qr_comment, customer_type, amount, qr_link_hash, request_hash, api_version,
    'IN' as transfer_direction, created_at, updated_at, 
    processed_at, response_status, error_message
FROM check_requests;
```

#### 3. Update Extra Data References

```sql
-- Update extra_data table to remove check_request_id references
-- First, migrate extra_data records from check_requests to transactions
UPDATE extra_data ed 
SET transaction_id = (
    SELECT t.id 
    FROM transactions t 
    WHERE t.transfer_direction = 'IN' 
    AND t.qr_link_hash = (
        SELECT cr.qr_link_hash 
        FROM check_requests cr 
        WHERE cr.id = ed.check_request_id
    )
)
WHERE ed.check_request_id IS NOT NULL;

-- Remove check_request_id column
ALTER TABLE extra_data DROP COLUMN check_request_id;
```

#### 4. Create New Indexes

```sql
-- Add new indexes for performance
CREATE INDEX idx_transactions_transfer_direction ON transactions(transfer_direction);
CREATE INDEX idx_transactions_customer_type ON transactions(customer_type);
CREATE INDEX idx_transactions_processed_at ON transactions(processed_at);
```

#### 5. Drop Deprecated Objects

```sql
-- Drop check_requests table (after data migration)
DROP TABLE check_requests CASCADE CONSTRAINTS;

-- Drop check_requests sequence
DROP SEQUENCE CHECK_REQUESTS_SEQ;
```

### Rollback Strategy

If rollback is needed:

```sql
-- Recreate check_requests table
CREATE TABLE check_requests AS 
SELECT * FROM transactions WHERE transfer_direction = 'IN';

-- Restore extra_data check_request_id column
ALTER TABLE extra_data ADD check_request_id NUMBER(19);

-- Restore foreign key constraints
ALTER TABLE extra_data ADD CONSTRAINT fk_extra_data_check_request 
    FOREIGN KEY (check_request_id) REFERENCES check_requests(id);

-- Recreate sequence
CREATE SEQUENCE CHECK_REQUESTS_SEQ START WITH 1 INCREMENT BY 1;
```

### Application Code Changes

1. **Entity Updates**: TransactionEntity now includes `customerType`, `transferDirection`
2. **Deprecated Entity**: CheckRequestEntity is marked as @Deprecated
3. **Service Layer**: Update services to use consolidated transactions table
4. **Repository Layer**: Update repositories to query by transfer_direction

### Testing Strategy

1. **Data Integrity**: Verify all check_requests data migrated correctly
2. **Performance**: Test query performance with new indexes
3. **Application**: Test all CRUD operations on consolidated table
4. **Rollback**: Test rollback procedure in staging environment

### Migration Timeline

- **Development**: Schema changes applied
- **Staging**: Full migration testing
- **Production**: Scheduled maintenance window for migration
- **Post-Migration**: Monitor application performance and data integrity


