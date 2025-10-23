-- Oracle DDL (baseline) - PSP Service Schema

-- Table for storing check request operations
CREATE TABLE check_requests (
    id NUMBER(19) NOT NULL,
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
    request_hash VARCHAR2(255),
    api_version VARCHAR2(10),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    processed_at TIMESTAMP,
    response_status VARCHAR2(20),
    error_message VARCHAR2(500),
    CONSTRAINT pk_check_requests PRIMARY KEY (id)
);

-- Table for storing transaction operations (create/execute/update)
CREATE TABLE transactions (
    id NUMBER(19) NOT NULL,
    transaction_id VARCHAR2(32) NOT NULL,
    psp_transaction_id VARCHAR2(50) NOT NULL,
    receipt_id VARCHAR2(20) NOT NULL,
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
    transaction_type NUMBER(10) NOT NULL,
    status NUMBER(10) NOT NULL,
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
    ip_address VARCHAR2(45),
    user_agent VARCHAR2(500),
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT uk_transactions_transaction_id UNIQUE (transaction_id),
    CONSTRAINT uk_transactions_psp_transaction_id UNIQUE (psp_transaction_id),
    CONSTRAINT uk_transactions_receipt_id UNIQUE (receipt_id),
    CONSTRAINT uk_transactions_qr_transaction_id UNIQUE (qr_transaction_id)
);

-- Table for storing extra key-value data
CREATE TABLE extra_data (
    id NUMBER(19) NOT NULL,
    key_name VARCHAR2(64) NOT NULL,
    value_data VARCHAR2(256) NOT NULL,
    order_index NUMBER(10),
    check_request_id NUMBER(19),
    transaction_id NUMBER(19),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_active NUMBER(1) NOT NULL DEFAULT 1,
    data_type VARCHAR2(20),
    description VARCHAR2(200),
    CONSTRAINT pk_extra_data PRIMARY KEY (id),
    CONSTRAINT fk_extra_data_check_request FOREIGN KEY (check_request_id) REFERENCES check_requests(id),
    CONSTRAINT fk_extra_data_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

-- Create indexes for better performance
CREATE INDEX idx_check_requests_created_at ON check_requests(created_at);
CREATE INDEX idx_check_requests_qr_link_hash ON check_requests(qr_link_hash);
CREATE INDEX idx_check_requests_merchant_code ON check_requests(merchant_code);
CREATE INDEX idx_check_requests_qr_transaction_id ON check_requests(qr_transaction_id);

-- Additional indexes for transactions table
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_executed_at ON transactions(executed_at);
CREATE INDEX idx_transactions_merchant_code ON transactions(merchant_code);
CREATE INDEX idx_transactions_qr_link_hash ON transactions(qr_link_hash);

-- Indexes for extra_data table
CREATE INDEX idx_extra_data_check_request_id ON extra_data(check_request_id);
CREATE INDEX idx_extra_data_transaction_id ON extra_data(transaction_id);
CREATE INDEX idx_extra_data_key_name ON extra_data(key_name);

-- Grant permissions to application user (replace 'psp_user' with actual username)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON check_requests TO psp_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON transactions TO psp_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON extra_data TO psp_user;


