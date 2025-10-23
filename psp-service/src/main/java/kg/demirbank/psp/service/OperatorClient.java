package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.*;
import reactor.core.publisher.Mono;

/**
 * Client for handling operator interactions
 */
public interface OperatorClient {
    /**
     * Validate transaction details
     */
    Mono<CheckResponseDto> check(CheckRequestDto request);

    /**
     * Create new transaction
     */
    Mono<CreateResponseDto> create(CreateRequestDto request);

    /**
     * Execute transaction
     */
    Mono<StatusDto> execute(String transactionId);

    /**
     * Update transaction status
     */
    Mono<Void> update(String transactionId, UpdateDto request);
}
