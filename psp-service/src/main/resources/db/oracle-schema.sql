-- Oracle Database Schema for PSP Service
-- Execute these scripts as a DBA user or user with CREATE TABLE privileges

-- Unified table for storing all PSP operations (check/create/execute/update)
-- This single table design allows easy querying of all operation data with proper direction tracking
CREATE TABLE operations (
    id NUMBER(19) NOT NULL,
    psp_transaction_id VARCHAR2(50) NOT NULL,
    payment_session_id VARCHAR2(50),
    operation_type NUMBER(1) NOT NULL,
    transfer_direction VARCHAR2(3) NOT NULL,
    transaction_id VARCHAR2(32),
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
    qr_comment VARCHAR2(99),
    customer_type VARCHAR2(1) NOT NULL,
    amount NUMBER(19) NOT NULL,
    qr_link_hash VARCHAR2(4) NOT NULL,
    transaction_type NUMBER(1),
    status NUMBER(1),
    beneficiary_name VARCHAR2(100),
    request_hash VARCHAR2(255),
    api_version VARCHAR2(10),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    executed_at TIMESTAMP,
    last_status_update_at TIMESTAMP,
    error_message VARCHAR2(500),
    retry_count NUMBER(10) NOT NULL DEFAULT 0,
    max_retries NUMBER(10) NOT NULL DEFAULT 3,
    is_final NUMBER(1) NOT NULL DEFAULT 0,
    created_by VARCHAR2(50),
    updated_by VARCHAR2(50),
    CONSTRAINT pk_operations PRIMARY KEY (id),
    CONSTRAINT uk_operations_psp_transaction_id UNIQUE (psp_transaction_id),
    CONSTRAINT uk_operations_payment_session_id UNIQUE (payment_session_id),
    CONSTRAINT uk_operations_transaction_id UNIQUE (transaction_id),
    CONSTRAINT uk_operations_receipt_id UNIQUE (receipt_id),
    CONSTRAINT chk_operations_operation_type CHECK (operation_type IN (10, 20, 30, 40)),
    CONSTRAINT chk_operations_transfer_direction CHECK (transfer_direction IN ('IN', 'OUT', 'OWN')),
    CONSTRAINT chk_operations_customer_type CHECK (customer_type IN ('1', '2')),
    CONSTRAINT chk_operations_qr_type CHECK (qr_type IN ('staticQr', 'dynamicQr')),
    CONSTRAINT chk_operations_currency_code CHECK (currency_code = '417')
);

-- Table for storing extra key-value data
CREATE TABLE extra_data (
    id NUMBER(19) NOT NULL,
    key_name VARCHAR2(64) NOT NULL,
    value_data VARCHAR2(256) NOT NULL,
    order_index NUMBER(10),
    operation_id NUMBER(19) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_active NUMBER(1) NOT NULL DEFAULT 1,
    data_type VARCHAR2(20),
    description VARCHAR2(200),
    CONSTRAINT pk_extra_data PRIMARY KEY (id),
    CONSTRAINT fk_extra_data_operation FOREIGN KEY (operation_id) REFERENCES operations(id)
);

-- Table for storing merchant webhook configurations
CREATE TABLE merchant_webhooks (
    id NUMBER(19) NOT NULL,
    merchant_name VARCHAR2(100) NOT NULL,
    app_id VARCHAR2(32) NOT NULL,
    api_key_name VARCHAR2(100) NOT NULL,
    api_key_value VARCHAR2(255) NOT NULL,
    target_url VARCHAR2(500) NOT NULL,
    is_active NUMBER(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR2(50),
    updated_at TIMESTAMP,
    updated_by VARCHAR2(50),
    CONSTRAINT pk_merchant_webhooks PRIMARY KEY (id),
    CONSTRAINT uk_merchant_webhooks_app_id UNIQUE (app_id),
    CONSTRAINT chk_merchant_webhooks_active CHECK (is_active IN (0, 1))
);

-- Create sequences
CREATE SEQUENCE operations_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE SEQUENCE extra_data_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create indexes for better performance
-- Unique constraints automatically create unique indexes for:
-- - psp_transaction_id (uk_operations_psp_transaction_id)
-- - payment_session_id (uk_operations_payment_session_id)
-- - transaction_id (uk_operations_transaction_id) 
-- - receipt_id (uk_operations_receipt_id)

-- Performance indexes for operations table
CREATE INDEX idx_operations_operation_type ON operations(operation_type);
CREATE INDEX idx_operations_transfer_direction ON operations(transfer_direction);
CREATE INDEX idx_operations_status ON operations(status);
CREATE INDEX idx_operations_created_at ON operations(created_at);
CREATE INDEX idx_operations_executed_at ON operations(executed_at);
CREATE INDEX idx_operations_merchant_code ON operations(merchant_code);
CREATE INDEX idx_operations_qr_link_hash ON operations(qr_link_hash);
CREATE INDEX idx_operations_customer_type ON operations(customer_type);
CREATE INDEX idx_operations_psp_transaction_id ON operations(psp_transaction_id);
CREATE INDEX idx_operations_payment_session_id ON operations(payment_session_id);

-- Performance indexes for extra_data table
CREATE INDEX idx_extra_data_operation_id ON extra_data(operation_id);
CREATE INDEX idx_extra_data_key_name ON extra_data(key_name);

-- Performance indexes for merchant_webhooks table
CREATE INDEX idx_merchant_webhooks_app_id ON merchant_webhooks(app_id);
CREATE INDEX idx_merchant_webhooks_active ON merchant_webhooks(is_active);

-- Grant permissions to application user (replace 'psp_user' with actual username)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON operations TO psp_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON extra_data TO psp_user;
