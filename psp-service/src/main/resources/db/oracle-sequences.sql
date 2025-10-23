-- Oracle Database Sequences for PSP Service
-- Execute these scripts as a DBA user or user with CREATE SEQUENCE privileges

-- Sequence for check_requests table
CREATE SEQUENCE CHECK_REQUESTS_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMAXVALUE
    NOCYCLE
    CACHE 20;

-- Sequence for transactions table  
CREATE SEQUENCE TRANSACTIONS_SEQ
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

-- Grant permissions to application user (replace 'psp_user' with actual username)
-- GRANT SELECT ON CHECK_REQUESTS_SEQ TO psp_user;
-- GRANT SELECT ON TRANSACTIONS_SEQ TO psp_user;
-- GRANT SELECT ON EXTRA_DATA_SEQ TO psp_user;
