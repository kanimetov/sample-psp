package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.incoming.request.IncomingCheckRequestDto;
import kg.demirbank.psp.dto.incoming.request.IncomingCreateRequestDto;
import kg.demirbank.psp.dto.common.UpdateDto;
import kg.demirbank.psp.dto.incoming.response.IncomingCheckResponseDto;
import kg.demirbank.psp.dto.incoming.response.IncomingTransactionResponseDto;
import kg.demirbank.psp.enums.Status;
import kg.demirbank.psp.enums.TransactionType;
import kg.demirbank.psp.exception.*;
import kg.demirbank.psp.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implementation of incoming service
 * Contains business logic for incoming transaction operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IncomingServiceImpl implements kg.demirbank.psp.service.IncomingService {
    
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public Mono<IncomingCheckResponseDto> checkTransaction(IncomingCheckRequestDto request) {
        String pspTransactionId = UUID.randomUUID().toString();
        
        // Set transaction context for logging
        LoggingUtil.setTransactionContext(pspTransactionId, null, null, "CHECK", 
                "IN", request.getMerchantCode(), 
                request.getAmount(), null);
        
        LoggingUtil.logOperationStart("CHECK", pspTransactionId, 
                "IN", request.getMerchantCode());
        
        return Mono.fromCallable(() -> {
            // Business logic validation
            validateCheckRequest(request);
            
            // Simulate beneficiary lookup based on merchant data
            String beneficiaryName = lookupBeneficiaryName(request);
            TransactionType transactionType = determineTransactionType(request);
            
            IncomingCheckResponseDto response = new IncomingCheckResponseDto();
            response.setBeneficiaryName(beneficiaryName);
            response.setTransactionType(transactionType);
            
            // Log successful response
            LoggingUtil.logOperationSuccess("CHECK", pspTransactionId, null, "SUCCESS");
            
            return response;
        })
        .doOnError(error -> {
            // Log error with structured data - no stack trace for business errors
            String errorCode = getErrorCode(error);
            String errorMessage = error.getMessage();
            LoggingUtil.logError("CHECK", pspTransactionId, errorCode, errorMessage, error);
        });
    }
    
    @Override
    public Mono<IncomingTransactionResponseDto> createTransaction(IncomingCreateRequestDto request) {
        String pspTransactionId = UUID.randomUUID().toString();
        
        // Set transaction context for logging
        LoggingUtil.setTransactionContext(pspTransactionId, request.getTransactionId().toString(), 
                request.getSenderReceiptId(), "CREATE", "IN", 
                request.getMerchantCode(), request.getAmount(), "CREATED");
        
        LoggingUtil.logOperationStart("CREATE", pspTransactionId, 
                "IN", request.getMerchantCode());
        
        return Mono.fromCallable(() -> {
            // Business logic validation
            validateCreateRequest(request);
            
            // Generate transaction ID if not provided
            String transactionId = request.getTransactionId() != null ? 
                    request.getTransactionId().toString() : generateTransactionId();
            
            // Simulate transaction creation
            IncomingTransactionResponseDto response = new IncomingTransactionResponseDto();
            response.setTransactionId(transactionId);
            response.setStatus(Status.CREATED); // Status 10 for created
            response.setAmount(request.getAmount());
            response.setBeneficiaryName("Sample Beneficiary");
            response.setCustomerType("1"); // Default to Individual customer type
            response.setReceiptId(request.getSenderReceiptId());
            response.setCreatedDate(LocalDateTime.now().format(ISO_DATE_TIME) + "Z");
            response.setExecutedDate(null);
            
            // Log successful response
            LoggingUtil.logOperationSuccess("CREATE", pspTransactionId, transactionId, "CREATED");
            
            return response;
        })
        .doOnError(error -> {
            // Log error with structured data - no stack trace for business errors
            String errorCode = getErrorCode(error);
            String errorMessage = error.getMessage();
            LoggingUtil.logError("CREATE", pspTransactionId, errorCode, errorMessage, error);
        });
    }
    
    @Override
    public Mono<IncomingTransactionResponseDto> executeTransaction(String transactionId) {
        String pspTransactionId = UUID.randomUUID().toString();
        
        // Set transaction context for logging
        LoggingUtil.setTransactionContext(pspTransactionId, transactionId, null, "EXECUTE", 
                "IN", null, 40000L, "IN_PROCESS");
        
        LoggingUtil.logOperationStart("EXECUTE", pspTransactionId, "IN", null);
        
        return Mono.fromCallable(() -> {
            // Validate transaction ID
            if (transactionId == null || transactionId.trim().isEmpty()) {
                throw new BadRequestException("Transaction ID is required");
            }
            
            // Simulate transaction execution
            IncomingTransactionResponseDto response = new IncomingTransactionResponseDto();
            response.setTransactionId(transactionId);
            response.setStatus(Status.IN_PROCESS); // Status 20 for success
            response.setAmount(40000L);
            response.setBeneficiaryName("c***e A***o");
            response.setCustomerType("1");
            response.setReceiptId("7218199");
            response.setCreatedDate("2022-11-01T12:00:00Z");
            response.setExecutedDate(LocalDateTime.now().format(ISO_DATE_TIME) + "Z");
            
            // Log successful response
            LoggingUtil.logOperationSuccess("EXECUTE", pspTransactionId, transactionId, "IN_PROCESS");
            
            return response;
        })
        .doOnError(error -> {
            // Log error with structured data - no stack trace for business errors
            String errorCode = getErrorCode(error);
            String errorMessage = error.getMessage();
            LoggingUtil.logError("EXECUTE", pspTransactionId, errorCode, errorMessage, error);
        });
    }
    
    @Override
    public Mono<Void> updateTransaction(String transactionId, UpdateDto updateRequest) {
        String pspTransactionId = UUID.randomUUID().toString();
        
        // Set transaction context for logging
        LoggingUtil.setTransactionContext(pspTransactionId, transactionId, null, "UPDATE", 
                "IN", null, null, updateRequest.getStatus().name());
        
        LoggingUtil.logOperationStart("UPDATE", pspTransactionId, "IN", null);
        
        return Mono.fromCallable(() -> {
            // Validate transaction ID
            if (transactionId == null || transactionId.trim().isEmpty()) {
                throw new BadRequestException("Transaction ID is required");
            }
            
            // Validate update request
            validateUpdateRequest(updateRequest);
            
            // Simulate transaction update
            LoggingUtil.logAuditTrail("UPDATE", pspTransactionId, "Transaction updated");
            return null; // Return null for Void
        })
        .then()
        .doOnSuccess(__ -> LoggingUtil.logOperationSuccess("UPDATE", pspTransactionId, transactionId, updateRequest.getStatus().name()))
        .doOnError(error -> {
            // Log error with structured data - no stack trace for business errors
            String errorCode = getErrorCode(error);
            String errorMessage = error.getMessage();
            LoggingUtil.logError("UPDATE", pspTransactionId, errorCode, errorMessage, error);
        });
    }
    
    /**
     * Validate check request business rules
     */
    private void validateCheckRequest(IncomingCheckRequestDto request) {
        // Validate amount limits
        if (request.getAmount() < 100) {
            throw new MinAmountNotValidException("Minimum amount is 100");
        }
        if (request.getAmount() > 1000000) {
            throw new MaxAmountNotValidException("Maximum amount is 1000000");
        }
        
        // Validate merchant code
        if (request.getMerchantCode() < 0 || request.getMerchantCode() > 9999) {
            throw new IncorrectRequestDataException("Merchant code must be between 0 and 9999");
        }
        
        // Validate currency code
        if (!"417".equals(request.getCurrencyCode())) {
            throw new IncorrectRequestDataException("Only KGS currency (417) is supported");
        }
    }
    
    /**
     * Validate create request business rules
     */
    private void validateCreateRequest(IncomingCreateRequestDto request) {
        // Validate amount limits
        if (request.getAmount() < 100) {
            throw new MinAmountNotValidException("Minimum amount is 100");
        }
        if (request.getAmount() > 1000000) {
            throw new MaxAmountNotValidException("Maximum amount is 1000000");
        }
        
        // Validate merchant code
        if (request.getMerchantCode() < 0 || request.getMerchantCode() > 9999) {
            throw new IncorrectRequestDataException("Merchant code must be between 0 and 9999");
        }
        
        // Validate currency code
        if (!"417".equals(request.getCurrencyCode())) {
            throw new IncorrectRequestDataException("Only KGS currency (417) is supported");
        }
        
        // Validate sender transaction ID format
        if (request.getSenderTransactionId() == null || request.getSenderTransactionId().trim().isEmpty()) {
            throw new IncorrectRequestDataException("Sender transaction ID is required");
        }
        
        // Validate sender receipt ID format
        if (request.getSenderReceiptId() == null || request.getSenderReceiptId().trim().isEmpty()) {
            throw new IncorrectRequestDataException("Sender receipt ID is required");
        }
    }
    
    /**
     * Validate update request business rules
     */
    private void validateUpdateRequest(UpdateDto request) {
        if (request.getStatus() == null) {
            throw new IncorrectRequestDataException("Status is required");
        }
        
        if (request.getUpdateDate() == null || request.getUpdateDate().trim().isEmpty()) {
            throw new IncorrectRequestDataException("Update date is required");
        }
    }
    
    /**
     * Lookup beneficiary name based on merchant data
     */
    private String lookupBeneficiaryName(IncomingCheckRequestDto request) {
        // Simulate beneficiary lookup logic
        // In real implementation, this would query merchant database
        return "c***e A***o";
    }
    
    /**
     * Determine transaction type based on request data
     */
    private TransactionType determineTransactionType(IncomingCheckRequestDto request) {
        // Simulate transaction type determination
        // In real implementation, this would be based on business rules
        return TransactionType.C2C;
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Get error code from exception for structured logging
     */
    private String getErrorCode(Throwable error) {
        if (error instanceof MinAmountNotValidException) {
            return "MIN_AMOUNT_INVALID";
        } else if (error instanceof MaxAmountNotValidException) {
            return "MAX_AMOUNT_INVALID";
        } else if (error instanceof IncorrectRequestDataException) {
            return "INCORRECT_REQUEST_DATA";
        } else if (error instanceof BadRequestException) {
            return "BAD_REQUEST";
        } else if (error instanceof SignatureVerificationException) {
            return "SIGNATURE_VERIFICATION_FAILED";
        } else if (error instanceof ValidationException) {
            return "VALIDATION_ERROR";
        } else if (error instanceof AccessDeniedException) {
            return "ACCESS_DENIED";
        } else if (error instanceof ResourceNotFoundException) {
            return "RESOURCE_NOT_FOUND";
        } else if (error instanceof UnprocessableEntityException) {
            return "UNPROCESSABLE_ENTITY";
        } else if (error instanceof NetworkException) {
            return "NETWORK_ERROR";
        } else if (error instanceof ExternalServerNotAvailableException) {
            return "EXTERNAL_SERVER_UNAVAILABLE";
        } else if (error instanceof SupplierNotAvailableException) {
            return "SUPPLIER_UNAVAILABLE";
        } else if (error instanceof SystemErrorException) {
            return "SYSTEM_ERROR";
        } else {
            return "UNKNOWN_ERROR";
        }
    }
}