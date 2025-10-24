package kg.demirbank.psp.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for structured logging in PSP service.
 * Provides methods to log with key properties for log analysis.
 */
@Component
@Slf4j
public class LoggingUtil {
    
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // Specialized loggers for different types of logs
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT");
    private static final Logger PERFORMANCE_LOGGER = LoggerFactory.getLogger("PERFORMANCE");
    
    // MDC Keys for log correlation
    public static final String CORRELATION_ID = "correlationId";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String PSP_TRANSACTION_ID = "pspTransactionId";
    public static final String RECEIPT_ID = "receiptId";
    public static final String MERCHANT_PROVIDER = "merchantProvider";
    public static final String MERCHANT_CODE = "merchantCode";
    public static final String QR_TYPE = "qrType";
    public static final String OPERATION_TYPE = "operationType";
    public static final String STATUS = "status";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY_CODE = "currencyCode";
    public static final String API_VERSION = "apiVersion";
    public static final String RESPONSE_TIME_MS = "responseTimeMs";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String IP_ADDRESS = "ipAddress";
    public static final String USER_AGENT = "userAgent";
    public static final String REQUEST_HASH = "requestHash";
    public static final String SIGNATURE_VERIFIED = "signatureVerified";
    
    /**
     * Set correlation ID for request tracing
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null) {
            MDC.put(CORRELATION_ID, correlationId);
        }
    }
    
    /**
     * Generate and set new correlation ID
     */
    public static String generateAndSetCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        setCorrelationId(correlationId);
        return correlationId;
    }
    
    /**
     * Clear correlation ID
     */
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID);
    }
    
    /**
     * Set transaction-related properties in MDC
     */
    public static void setTransactionContext(String transactionId, String pspTransactionId, 
                                           String receiptId, String merchantProvider, 
                                           Integer merchantCode, String qrType) {
        if (transactionId != null) MDC.put(TRANSACTION_ID, transactionId);
        if (pspTransactionId != null) MDC.put(PSP_TRANSACTION_ID, pspTransactionId);
        if (receiptId != null) MDC.put(RECEIPT_ID, receiptId);
        if (merchantProvider != null) MDC.put(MERCHANT_PROVIDER, merchantProvider);
        if (merchantCode != null) MDC.put(MERCHANT_CODE, merchantCode.toString());
        if (qrType != null) MDC.put(QR_TYPE, qrType);
    }
    
    /**
     * Set operation context in MDC
     */
    public static void setOperationContext(String operationType, String status, 
                                         Long amount, String currencyCode, 
                                         String apiVersion) {
        if (operationType != null) MDC.put(OPERATION_TYPE, operationType);
        if (status != null) MDC.put(STATUS, status);
        if (amount != null) MDC.put(AMOUNT, amount.toString());
        if (currencyCode != null) MDC.put(CURRENCY_CODE, currencyCode);
        if (apiVersion != null) MDC.put(API_VERSION, apiVersion);
    }
    
    /**
     * Set error context in MDC
     */
    public static void setErrorContext(String errorCode, String errorMessage) {
        if (errorCode != null) MDC.put(ERROR_CODE, errorCode);
        if (errorMessage != null) MDC.put(ERROR_MESSAGE, errorMessage);
    }
    
    /**
     * Set request context in MDC
     */
    public static void setRequestContext(String ipAddress, String userAgent, 
                                       String requestHash, Boolean signatureVerified) {
        if (ipAddress != null) MDC.put(IP_ADDRESS, ipAddress);
        if (userAgent != null) MDC.put(USER_AGENT, userAgent);
        if (requestHash != null) MDC.put(REQUEST_HASH, requestHash);
        if (signatureVerified != null) MDC.put(SIGNATURE_VERIFIED, signatureVerified.toString());
    }
    
    /**
     * Set response time in MDC
     */
    public static void setResponseTime(Long responseTimeMs) {
        if (responseTimeMs != null) {
            MDC.put(RESPONSE_TIME_MS, responseTimeMs.toString());
        }
    }
    
    /**
     * Clear all MDC context
     */
    public static void clearContext() {
        MDC.clear();
    }
    
    /**
     * Log operation start with structured data
     */
    public static void logOperationStart(String operationType, Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "operation_start");
        logData.put("operationType", operationType);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        log.info("Operation started: {}", logData);
    }
    
    /**
     * Log operation success with structured data
     */
    public static void logOperationSuccess(String operationType, Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "operation_success");
        logData.put("operationType", operationType);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        log.info("Operation completed successfully: {}", logData);
    }
    
    /**
     * Log operation error with structured data
     */
    public static void logOperationError(String operationType, String errorCode, 
                                       String errorMessage, Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "operation_error");
        logData.put("operationType", operationType);
        logData.put("errorCode", errorCode);
        logData.put("errorMessage", errorMessage);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        log.error("Operation failed: {}", logData);
    }
    
    /**
     * Log business error without stack trace (known business exceptions)
     */
    public static void logBusinessError(String operationType, String errorCode, 
                                      String errorMessage, Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "business_error");
        logData.put("operationType", operationType);
        logData.put("errorCode", errorCode);
        logData.put("errorMessage", errorMessage);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        log.warn("Business error: {}", logData);
    }
    
    /**
     * Log system error with stack trace (unexpected system exceptions)
     */
    public static void logSystemError(String operationType, String errorCode, 
                                    String errorMessage, Throwable throwable, 
                                    Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "system_error");
        logData.put("operationType", operationType);
        logData.put("errorCode", errorCode);
        logData.put("errorMessage", errorMessage);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        log.error("System error: {} - Exception: {}", logData, throwable.getMessage(), throwable);
    }
    
    /**
     * Log business validation with structured data
     */
    public static void logBusinessValidation(String validationType, boolean isValid, 
                                           String details, Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "business_validation");
        logData.put("validationType", validationType);
        logData.put("isValid", isValid);
        logData.put("details", details);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        if (isValid) {
            log.debug("Business validation passed: {}", logData);
        } else {
            log.warn("Business validation failed: {}", logData);
        }
    }
    
    /**
     * Log signature verification with structured data
     */
    public static void logSignatureVerification(boolean isVerified, String details, 
                                              Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "signature_verification");
        logData.put("isVerified", isVerified);
        logData.put("details", details);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        if (isVerified) {
            log.debug("Signature verification successful: {}", logData);
        } else {
            log.warn("Signature verification failed: {}", logData);
        }
    }
    
    /**
     * Log performance metrics with structured data
     */
    public static void logPerformanceMetrics(String operationType, Long responseTimeMs, 
                                           Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "performance_metrics");
        logData.put("operationType", operationType);
        logData.put("responseTimeMs", responseTimeMs);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        PERFORMANCE_LOGGER.info("Performance metrics: {}", logData);
    }
    
    /**
     * Log audit trail with structured data
     */
    public static void logAuditTrail(String action, String entityType, String entityId, 
                                   Map<String, Object> properties) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "audit_trail");
        logData.put("action", action);
        logData.put("entityType", entityType);
        logData.put("entityId", entityId);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        logData.putAll(properties);
        
        AUDIT_LOGGER.info("Audit trail: {}", logData);
    }
    
    /**
     * Create properties map for common transaction fields
     */
    public static Map<String, Object> createTransactionProperties(String transactionId, 
                                                                String pspTransactionId,
                                                                String receiptId,
                                                                String merchantProvider,
                                                                Integer merchantCode,
                                                                String qrType,
                                                                Long amount,
                                                                String currencyCode) {
        Map<String, Object> properties = new HashMap<>();
        if (transactionId != null) properties.put("transactionId", transactionId);
        if (pspTransactionId != null) properties.put("pspTransactionId", pspTransactionId);
        if (receiptId != null) properties.put("receiptId", receiptId);
        if (merchantProvider != null) properties.put("merchantProvider", merchantProvider);
        if (merchantCode != null) properties.put("merchantCode", merchantCode);
        if (qrType != null) properties.put("qrType", qrType);
        if (amount != null) properties.put("amount", amount);
        if (currencyCode != null) properties.put("currencyCode", currencyCode);
        return properties;
    }
    
    /**
     * Check if exception is a known business exception (should not log stack trace)
     */
    public static boolean isBusinessException(Throwable throwable) {
        return throwable instanceof kg.demirbank.psp.exception.MinAmountNotValidException ||
               throwable instanceof kg.demirbank.psp.exception.MaxAmountNotValidException ||
               throwable instanceof kg.demirbank.psp.exception.IncorrectRequestDataException ||
               throwable instanceof kg.demirbank.psp.exception.BadRequestException ||
               throwable instanceof kg.demirbank.psp.exception.ValidationException ||
               throwable instanceof kg.demirbank.psp.exception.AccessDeniedException ||
               throwable instanceof kg.demirbank.psp.exception.ResourceNotFoundException ||
               throwable instanceof kg.demirbank.psp.exception.UnprocessableEntityException ||
               throwable instanceof kg.demirbank.psp.exception.RecipientDataIncorrectException;
    }
    
    /**
     * Check if exception is a signature/security related exception (should not log stack trace)
     */
    public static boolean isSecurityException(Throwable throwable) {
        return throwable instanceof kg.demirbank.psp.exception.SignatureVerificationException;
    }
    
    /**
     * Check if exception is a system/network exception (should log stack trace)
     */
    public static boolean isSystemException(Throwable throwable) {
        return throwable instanceof kg.demirbank.psp.exception.NetworkException ||
               throwable instanceof kg.demirbank.psp.exception.NetworkConnectionException ||
               throwable instanceof kg.demirbank.psp.exception.NetworkTimeoutException ||
               throwable instanceof kg.demirbank.psp.exception.ExternalServerNotAvailableException ||
               throwable instanceof kg.demirbank.psp.exception.SupplierNotAvailableException ||
               throwable instanceof kg.demirbank.psp.exception.SystemErrorException ||
               throwable instanceof RuntimeException ||
               throwable instanceof Exception;
    }
    
    /**
     * Log error with appropriate level based on exception type
     */
    public static void logError(String operationType, String errorCode, String errorMessage, 
                              Throwable throwable, Map<String, Object> properties) {
        if (isBusinessException(throwable) || isSecurityException(throwable)) {
            // Business and security errors - log as WARN without stack trace
            logBusinessError(operationType, errorCode, errorMessage, properties);
        } else if (isSystemException(throwable)) {
            // System errors - log as ERROR with stack trace
            logSystemError(operationType, errorCode, errorMessage, throwable, properties);
        } else {
            // Unknown errors - log as ERROR with stack trace
            logSystemError(operationType, "UNKNOWN_ERROR", errorMessage, throwable, properties);
        }
    }
}
