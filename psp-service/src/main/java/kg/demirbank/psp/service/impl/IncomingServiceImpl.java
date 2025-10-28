package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.incoming.request.IncomingCheckRequestDto;
import kg.demirbank.psp.dto.incoming.request.IncomingCreateRequestDto;
import kg.demirbank.psp.dto.common.UpdateDto;
import kg.demirbank.psp.dto.incoming.response.IncomingCheckResponseDto;
import kg.demirbank.psp.dto.incoming.response.IncomingTransactionResponseDto;
import kg.demirbank.psp.exception.PspException;
import kg.demirbank.psp.exception.network.SystemErrorException;
import kg.demirbank.psp.service.BankService;
import kg.demirbank.psp.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


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
        log.info("Starting incoming transaction check for merchant: {} with amount: {}", 
                request.getMerchantCode(), request.getAmount());
        
        return bankService.checkIncomingTransaction(request)
                .doOnSuccess(response -> {
                    log.info("Incoming transaction check completed successfully for merchant: {}", 
                            request.getMerchantCode());
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
        log.info("Starting incoming transaction creation for transaction: {} with amount: {}", 
                request.getTransactionId(), request.getAmount());
        
        return bankService.createIncomingTransaction(request)
                .doOnSuccess(response -> {
                    log.info("Incoming transaction created successfully for transaction: {}", 
                            request.getTransactionId());
                })
                .onErrorMap(throwable -> {
                    // Log error with structured data - no stack trace for business errors
                    boolean isPspException = throwable instanceof PspException;
                    String errorMessage = throwable.getMessage();
                    String errorCode = isPspException ? 
                        ((PspException) throwable).getCode().toString() : "UNKNOWN_ERROR";
                    LoggingUtil.logError("CREATE", null, errorCode, errorMessage, throwable);
                    
                    if (isPspException) {
                        return throwable; // Preserve original PspException
                    }
                    log.error("Unexpected error during incoming transaction creation: {}", throwable.getMessage(), throwable);
                    return new SystemErrorException("Failed to process incoming transaction creation request", throwable);
                });
    }
    
    @Override
    public Mono<IncomingTransactionResponseDto> executeTransaction(String transactionId) {
        log.info("Starting incoming transaction execution for transaction: {}", transactionId);
        
        return bankService.executeIncomingTransaction(transactionId)
                .doOnSuccess(response -> {
                    log.info("Incoming transaction executed successfully for transaction: {}", transactionId);
                })
                .onErrorMap(throwable -> {
                    // Log error with structured data - no stack trace for business errors
                    boolean isPspException = throwable instanceof PspException;
                    String errorMessage = throwable.getMessage();
                    String errorCode = isPspException ? 
                        ((PspException) throwable).getCode().toString() : "UNKNOWN_ERROR";
                    LoggingUtil.logError("EXECUTE", transactionId, errorCode, errorMessage, throwable);
                    
                    if (isPspException) {
                        return throwable;
                    }
                    
                    log.error("Unexpected error during incoming transaction execution: {}", throwable.getMessage(), throwable);
                    return new SystemErrorException("Failed to process incoming transaction execution request", throwable);
                });
    }
    
    @Override
    public Mono<Void> updateTransaction(String transactionId, UpdateDto updateRequest) {
        log.info("Starting incoming transaction update for transaction: {} with status: {}", 
                transactionId, updateRequest.getStatus());
        
        return bankService.updateIncomingTransaction(transactionId, updateRequest)
        .doOnSuccess(result -> log.info("Incoming transaction updated successfully for transaction: {} with status: {}", 
                transactionId, updateRequest.getStatus()))
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