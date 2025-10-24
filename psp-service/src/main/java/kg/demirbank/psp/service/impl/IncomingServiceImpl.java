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
                .onErrorMap(throwable -> {
                   // Log error with structured data - no stack trace for business errors
                   boolean isPspException = throwable instanceof PspException;
                   String errorMessage = throwable.getMessage();
                   String errorCode = isPspException ? 
                       ((PspException) throwable).getCode().toString() : "UNKNOWN_ERROR";
                    LoggingUtil.logError("CHECK", null, errorCode, errorMessage, throwable);
                    
                    if (isPspException) {
                        return throwable; // Preserve original PspException
                    }
                    log.error("Unexpected error during incoming transaction check: {}", throwable.getMessage(), throwable);
                    return new SystemErrorException("Failed to process incoming transaction check request", throwable);
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
                .onErrorMap(throwable -> {
                    // Log error with structured data - no stack trace for business errors
                    boolean isPspException = throwable instanceof PspException;
                    String errorMessage = throwable.getMessage();
                    String errorCode = isPspException ? 
                        ((PspException) throwable).getCode().toString() : "UNKNOWN_ERROR";
                    LoggingUtil.logError("CREATE", pspTransactionId, errorCode, errorMessage, throwable);
                    
                    if (isPspException) {
                        return throwable; // Preserve original PspException
                    }
                    log.error("Unexpected error during incoming transaction creation: {}", throwable.getMessage(), throwable);
                    return new SystemErrorException("Failed to process incoming transaction creation request", throwable);
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
                .onErrorMap(throwable -> {
                    // Log error with structured data - no stack trace for business errors
                    boolean isPspException = throwable instanceof PspException;
                    String errorMessage = throwable.getMessage();
                    String errorCode = isPspException ? 
                        ((PspException) throwable).getCode().toString() : "UNKNOWN_ERROR";
                    LoggingUtil.logError("EXECUTE", pspTransactionId, errorCode, errorMessage, throwable);
                    
                    if (isPspException) {
                        return throwable;
                    }
                    
                    log.error("Unexpected error during incoming transaction execution: {}", throwable.getMessage(), throwable);
                    return new SystemErrorException("Failed to process incoming transaction execution request", throwable);
                });
    }
    
    @Override
    public Mono<Void> updateTransaction(String transactionId, UpdateDto updateRequest) {
        
        // Set transaction context for logging
        LoggingUtil.setTransactionContext(null, transactionId, null, "UPDATE", 
                "IN", null, null, updateRequest.getStatus().name());
        
        LoggingUtil.logOperationStart("UPDATE", null, "IN", null);
        
        return bankService.updateIncomingTransaction(transactionId, updateRequest)
        .doOnSuccess(_ -> LoggingUtil.logOperationSuccess("UPDATE", null, transactionId, updateRequest.getStatus().name()))
        .onErrorMap(throwable -> {
            // Log error with structured data - no stack trace for business errors
            boolean isPspException = throwable instanceof PspException;
            String errorMessage = throwable.getMessage();
            String errorCode = isPspException ? 
                ((PspException) throwable).getCode().toString() : "UNKNOWN_ERROR";
            LoggingUtil.logError("UPDATE", null, errorCode, errorMessage, throwable);
            
            if (isPspException) {
                return throwable; // Preserve original PspException
            }
            log.error("Unexpected error during incoming transaction update: {}", throwable.getMessage(), throwable);
            return new SystemErrorException("Failed to process incoming transaction update request", throwable);
        });
    }
    
    
    
    
}