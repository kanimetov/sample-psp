package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.*;
import reactor.core.publisher.Mono;

/**
 * Service for PSP transaction business logic
 * Handles all PSP transaction operations: check, create, execute, update
 */
public interface PspTransactionService {
    
    /**
     * Check transaction request
     * Validates transaction data and returns beneficiary information
     * 
     * @param request Check request DTO
     * @return Check response with beneficiary name and transaction type
     */
    Mono<CheckResponseDto> checkTransaction(CheckRequestDto request);
    
    /**
     * Create new transaction
     * Processes transaction creation and returns transaction details
     * 
     * @param request Create request DTO
     * @return Create response with transaction ID and status
     */
    Mono<CreateResponseDto> createTransaction(CreateRequestDto request);
    
    /**
     * Execute transaction
     * Processes transaction execution and returns current status
     * 
     * @param transactionId Transaction ID to execute
     * @return Status response with transaction details
     */
    Mono<StatusDto> executeTransaction(String transactionId);
    
    /**
     * Update transaction status
     * Updates transaction status and returns acknowledgment
     * 
     * @param transactionId Transaction ID to update
     * @param updateRequest Update request DTO
     * @return Mono<Void> for acknowledgment
     */
    Mono<Void> updateTransaction(String transactionId, UpdateDto updateRequest);
}
