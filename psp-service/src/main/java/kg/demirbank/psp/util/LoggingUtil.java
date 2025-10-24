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
 * Simplified utility class for structured logging in PSP service.
 * Focuses on essential data needed for error analysis and debugging.
 */
@Component
@Slf4j
public class LoggingUtil {
    
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // Specialized loggers
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT");
    
    // Essential MDC Keys for log correlation and analysis
    public static final String CORRELATION_ID = "correlationId";
    public static final String PSP_TRANSACTION_ID = "pspTransactionId";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String RECEIPT_ID = "receiptId";
    public static final String OPERATION_TYPE = "operationType";
    public static final String TRANSFER_DIRECTION = "transferDirection";
    public static final String MERCHANT_CODE = "merchantCode";
    public static final String AMOUNT = "amount";
    public static final String STATUS = "status";
    public static final String ERROR_CODE = "errorCode";
    
    /**
     * Generate and set new correlation ID
     */
    public static String generateAndSetCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_ID, correlationId);
        return correlationId;
    }
    
    /**
     * Set correlation ID for request tracing
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null) {
            MDC.put(CORRELATION_ID, correlationId);
        }
    }
    
    /**
     * Set essential transaction context in MDC for log correlation
     */
    public static void setTransactionContext(String pspTransactionId, String transactionId, 
                                           String receiptId, String operationType,
                                           String transferDirection, Integer merchantCode, 
                                           Long amount, String status) {
        if (pspTransactionId != null) MDC.put(PSP_TRANSACTION_ID, pspTransactionId);
        if (transactionId != null) MDC.put(TRANSACTION_ID, transactionId);
        if (receiptId != null) MDC.put(RECEIPT_ID, receiptId);
        if (operationType != null) MDC.put(OPERATION_TYPE, operationType);
        if (transferDirection != null) MDC.put(TRANSFER_DIRECTION, transferDirection);
        if (merchantCode != null) MDC.put(MERCHANT_CODE, merchantCode.toString());
        if (amount != null) MDC.put(AMOUNT, amount.toString());
        if (status != null) MDC.put(STATUS, status);
    }
    
    /**
     * Set error context in MDC
     */
    public static void setErrorContext(String errorCode) {
        if (errorCode != null) MDC.put(ERROR_CODE, errorCode);
    }
    
    /**
     * Clear all MDC context
     */
    public static void clearContext() {
        MDC.clear();
    }
    
    
    /**
     * Log operation success - simplified version
     */
    public static void logOperationSuccess(String operationType, String pspTransactionId, 
                                         String transactionId, String status) {
        Map<String, Object> logData = createBaseLogData("operation_success", operationType);
        logData.put("pspTransactionId", pspTransactionId);
        logData.put("transactionId", transactionId);
        logData.put("status", status);
        
        log.info("Operation completed: {}", logData);
    }
    
    /**
     * Log operation error - simplified version with automatic exception type detection
     */
    public static void logOperationError(String operationType, String pspTransactionId, 
                                       String errorCode, String errorMessage, Throwable throwable) {
        Map<String, Object> logData = createBaseLogData("operation_error", operationType);
        logData.put("pspTransactionId", pspTransactionId);
        logData.put("errorCode", errorCode);
        logData.put("errorMessage", errorMessage);
        
        if (isBusinessException(throwable)) {
            log.warn("Business error: {}", logData);
        } else {
            log.error("System error: {} - Exception: {}", logData, throwable.getMessage(), throwable);
        }
    }
    
    /**
     * Log audit trail - simplified version
     */
    public static void logAuditTrail(String action, String pspTransactionId, String details) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", "audit_trail");
        logData.put("action", action);
        logData.put("pspTransactionId", pspTransactionId);
        logData.put("details", details);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        
        AUDIT_LOGGER.info("Audit: {}", logData);
    }
    
    /**
     * Create base log data with common fields
     */
    private static Map<String, Object> createBaseLogData(String event, String operationType) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("event", event);
        logData.put("operationType", operationType);
        logData.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
        return logData;
    }
    
    /**
     * Check if exception is a known business exception (should not log stack trace)
     */
    public static boolean isBusinessException(Throwable throwable) {
        return throwable instanceof kg.demirbank.psp.exception.validation.MinAmountNotValidException ||
               throwable instanceof kg.demirbank.psp.exception.validation.MaxAmountNotValidException ||
               throwable instanceof kg.demirbank.psp.exception.validation.IncorrectRequestDataException ||
               throwable instanceof kg.demirbank.psp.exception.validation.BadRequestException ||
               throwable instanceof kg.demirbank.psp.exception.validation.ValidationException ||
               throwable instanceof kg.demirbank.psp.exception.security.AccessDeniedException ||
               throwable instanceof kg.demirbank.psp.exception.business.ResourceNotFoundException ||
               throwable instanceof kg.demirbank.psp.exception.business.UnprocessableEntityException ||
               throwable instanceof kg.demirbank.psp.exception.validation.RecipientDataIncorrectException ||
               throwable instanceof kg.demirbank.psp.exception.security.SignatureVerificationException;
    }
    
    /**
     * Log error with automatic exception type detection - simplified version
     */
    public static void logError(String operationType, String pspTransactionId, String errorCode, 
                              String errorMessage, Throwable throwable) {
        logOperationError(operationType, pspTransactionId, errorCode, errorMessage, throwable);
    }
}
