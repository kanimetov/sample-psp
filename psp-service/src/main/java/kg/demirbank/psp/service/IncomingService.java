package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.incoming.request.CheckRequestDto;
import kg.demirbank.psp.dto.incoming.request.CreateRequestDto;
import kg.demirbank.psp.dto.common.UpdateDto;
import kg.demirbank.psp.dto.incoming.response.IncomingCheckResponseDto;
import kg.demirbank.psp.dto.incoming.response.IncomingTransactionResponseDto;
import reactor.core.publisher.Mono;

/**
 * Service for incoming transaction business logic
 * Handles all incoming transaction operations: check, create, execute, update
 */
public interface IncomingService {
    
    /**
     * Check transaction request
     * Validates transaction data and returns beneficiary information
     * 
     * @param request Check request DTO
     * @return Check response with beneficiary name and transaction type
     */
    Mono<IncomingCheckResponseDto> checkTransaction(CheckRequestDto request);
    
    /**
     * Create new transaction
     * Processes transaction creation and returns transaction details
     * 
     * @param request Create request DTO
     * @return Create response with transaction ID and status
     */
    Mono<IncomingTransactionResponseDto> createTransaction(CreateRequestDto request);
    
    /**
     * Execute transaction
     * Processes transaction execution and returns current status
     * 
     * @param transactionId Transaction ID to execute
     * @return Status response with transaction details
     */
    Mono<IncomingTransactionResponseDto> executeTransaction(String transactionId);
    
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
