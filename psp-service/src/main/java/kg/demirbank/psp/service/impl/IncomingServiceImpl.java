package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.incoming.request.IncomingCheckRequestDto;
import kg.demirbank.psp.dto.incoming.request.IncomingCreateRequestDto;
import kg.demirbank.psp.dto.common.UpdateDto;
import kg.demirbank.psp.dto.incoming.response.IncomingCheckResponseDto;
import kg.demirbank.psp.dto.incoming.response.IncomingTransactionResponseDto;
import kg.demirbank.psp.exception.*;
import kg.demirbank.psp.service.BankService;
import kg.demirbank.psp.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementation of incoming service
 * Contains business logic for incoming transaction operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IncomingServiceImpl implements kg.demirbank.psp.service.IncomingService {
    
    private final BankService bankService;
    
    @Override
    public Mono<IncomingCheckResponseDto> checkTransaction(IncomingCheckRequestDto request) {
        
        // Set transaction context for logging
        LoggingUtil.setTransactionContext(null, null, null, "CHECK", 
                "IN", request.getMerchantCode(), 
                request.getAmount(), null);
        
        LoggingUtil.logOperationStart("CHECK", null, 
                "IN", request.getMerchantCode());
        
        return bankService.checkIncomingTransaction(request)
                .doOnSuccess(_ -> {
                    // Log successful response
                    LoggingUtil.logOperationSuccess("CHECK", null, null, "SUCCESS");
                })
        .doOnError(error -> {
            // Log error with structured data - no stack trace for business errors
            String errorCode = getErrorCode(error);
            String errorMessage = error.getMessage();
            LoggingUtil.logError("CHECK", null, errorCode, errorMessage, error);
        });
    }
    
    @Override
    public Mono<IncomingTransactionResponseDto> createTransaction(IncomingCreateRequestDto request) {
        // Use transactionId from request as pspTransactionId for incoming transactions
        String pspTransactionId = request.getTransactionId();
        
        // Set transaction context for logging
        LoggingUtil.setTransactionContext(pspTransactionId, request.getTransactionId(), 
                request.getSenderReceiptId(), "CREATE", "IN", 
                request.getMerchantCode(), request.getAmount(), "CREATED");
        
        LoggingUtil.logOperationStart("CREATE", pspTransactionId, 
                "IN", request.getMerchantCode());
        
        return bankService.createIncomingTransaction(request)
                .doOnSuccess(response -> {
                    // Log successful response
                    LoggingUtil.logOperationSuccess("CREATE", pspTransactionId, 
                            response.getTransactionId(), "CREATED");
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
        
        return bankService.executeIncomingTransaction(transactionId)
                .doOnSuccess(response -> {
                    // Log successful response
                    LoggingUtil.logOperationSuccess("EXECUTE", pspTransactionId, transactionId, response.getStatus().name());
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
            // Simulate transaction update
            LoggingUtil.logAuditTrail("UPDATE", pspTransactionId, "Transaction updated");
            return null; // Return null for Void
        })
        .then()
        .doOnSuccess(_ -> LoggingUtil.logOperationSuccess("UPDATE", pspTransactionId, transactionId, updateRequest.getStatus().name()))
        .doOnError(error -> {
            // Log error with structured data - no stack trace for business errors
            String errorCode = getErrorCode(error);
            String errorMessage = error.getMessage();
            LoggingUtil.logError("UPDATE", pspTransactionId, errorCode, errorMessage, error);
        });
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