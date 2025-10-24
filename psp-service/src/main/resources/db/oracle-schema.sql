-- Oracle Database Schema for PSP Service
-- Execute these scripts as a DBA user or user with CREATE TABLE privileges

-- Consolidated table for storing all transaction operations (check/create/execute/update)
-- This single table design allows easy querying of all transaction data for GET status operations
CREATE TABLE transactions (
    id NUMBER(19) NOT NULL,
    transaction_id VARCHAR2(32),
    psp_transaction_id VARCHAR2(50),
    receipt_id VARCHAR2(20),
    qr_type VARCHAR2(32) NOT NULL,
    merchant_provider VARCHAR2(32) NOT NULL,
    merchant_id VARCHAR2(32),
    service_id VARCHAR2(32),
    service_name VARCHAR2(32),
    beneficiary_account_number VARCHAR2(32),
    merchant_code NUMBER(10) NOT NULL,
    currency_code VARCHAR2(3) NOT NULL,
    qr_transaction_id VARCHAR2(32),
    qr_comment VARCHAR2(32),
    customer_type VARCHAR2(1) NOT NULL,
    amount NUMBER(19) NOT NULL,
    qr_link_hash VARCHAR2(4) NOT NULL,
    transaction_type NUMBER(10),
    status NUMBER(10),
    beneficiary_name VARCHAR2(100),
    request_hash VARCHAR2(255),
    api_version VARCHAR2(10),
    transfer_direction VARCHAR2(3) NOT NULL, -- IN, OUT, OWN
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    executed_at TIMESTAMP,
    last_status_update_at TIMESTAMP,
    processed_at TIMESTAMP,
    response_status VARCHAR2(20),
    error_message VARCHAR2(500),
    retry_count NUMBER(10) NOT NULL DEFAULT 0,
    max_retries NUMBER(10) NOT NULL DEFAULT 3,
    is_final NUMBER(1) NOT NULL DEFAULT 0,
    created_by VARCHAR2(50),
    updated_by VARCHAR2(50),
    ip_address VARCHAR2(45),
    user_agent VARCHAR2(500),
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT uk_transactions_transaction_id UNIQUE (transaction_id),
    CONSTRAINT uk_transactions_psp_transaction_id UNIQUE (psp_transaction_id),
    CONSTRAINT uk_transactions_receipt_id UNIQUE (receipt_id),
    CONSTRAINT uk_transactions_qr_transaction_id UNIQUE (qr_transaction_id),
    CONSTRAINT chk_transfer_direction CHECK (transfer_direction IN ('IN', 'OUT', 'OWN'))
);

-- Table for storing extra key-value data
CREATE TABLE extra_data (
    id NUMBER(19) NOT NULL,
    key_name VARCHAR2(64) NOT NULL,
    value_data VARCHAR2(256) NOT NULL,
    order_index NUMBER(10),
    transaction_id NUMBER(19) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_active NUMBER(1) NOT NULL DEFAULT 1,
    data_type VARCHAR2(20),
    description VARCHAR2(200),
    CONSTRAINT pk_extra_data PRIMARY KEY (id),
    CONSTRAINT fk_extra_data_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

-- Create indexes for better performance
-- Unique constraints automatically create unique indexes for:
-- - transaction_id (uk_transactions_transaction_id)
-- - psp_transaction_id (uk_transactions_psp_transaction_id) 
-- - receipt_id (uk_transactions_receipt_id)
-- - qr_transaction_id (uk_transactions_qr_transaction_id)

-- Performance indexes for transactions table
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_executed_at ON transactions(executed_at);
CREATE INDEX idx_transactions_merchant_code ON transactions(merchant_code);
CREATE INDEX idx_transactions_qr_link_hash ON transactions(qr_link_hash);
CREATE INDEX idx_transactions_transfer_direction ON transactions(transfer_direction);
CREATE INDEX idx_transactions_customer_type ON transactions(customer_type);
CREATE INDEX idx_transactions_processed_at ON transactions(processed_at);

-- Performance indexes for extra_data table
CREATE INDEX idx_extra_data_transaction_id ON extra_data(transaction_id);
CREATE INDEX idx_extra_data_key_name ON extra_data(key_name);

-- Grant permissions to application user (replace 'psp_user' with actual username)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON transactions TO psp_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON extra_data TO psp_user;
