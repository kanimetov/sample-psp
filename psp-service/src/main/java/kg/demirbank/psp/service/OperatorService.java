package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.*;

/**
 * Service for handling operator interactions
 */
public interface OperatorService {
    /**
     * Validate transaction details
     */
    CheckResponseDto check(CheckRequestDto request);

    /**
     * Create new transaction
     */
    CreateResponseDto create(CreateRequestDto request);

    /**
     * Execute transaction
     */
    StatusDto execute(String transactionId);

    /**
     * Update transaction status
     */
    void update(String transactionId, UpdateDto request);
}