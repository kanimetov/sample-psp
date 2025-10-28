-- Oracle Database Sequences for PSP Service
-- Execute these scripts as a DBA user or user with CREATE SEQUENCE privileges

-- Sequence for operations table (replaces check_requests and transactions)
CREATE SEQUENCE OPERATIONS_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
    NOCYCLE
    CACHE 20;

-- Sequence for extra_data table
CREATE SEQUENCE EXTRA_DATA_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
    NOCYCLE
    CACHE 20;

-- Sequence for merchant_webhooks table
CREATE SEQUENCE MERCHANT_WEBHOOKS_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
    NOCYCLE
    CACHE 20;

-- Grant permissions to application user (replace 'psp_user' with actual username)
-- GRANT SELECT ON OPERATIONS_SEQ TO psp_user;
-- GRANT SELECT ON EXTRA_DATA_SEQ TO psp_user;
-- GRANT SELECT ON MERCHANT_WEBHOOKS_SEQ TO psp_user;
