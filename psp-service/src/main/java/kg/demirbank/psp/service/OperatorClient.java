package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.outgoing.request.CheckRequestDto;
import kg.demirbank.psp.dto.outgoing.request.CreateRequestDto;
import kg.demirbank.psp.dto.common.UpdateDto;
import kg.demirbank.psp.dto.outgoing.response.OutgoingCheckResponseDto;
import kg.demirbank.psp.dto.outgoing.response.OutgoingTransactionResponseDto;
import reactor.core.publisher.Mono;

/**
 * Client for handling operator interactions
 */
public interface OperatorClient {
    /**
     * Validate transaction details
     */
    Mono<OutgoingCheckResponseDto> check(CheckRequestDto request);

    /**
     * Create new transaction
     */
    Mono<OutgoingTransactionResponseDto> create(CreateRequestDto request);

    /**
     * Execute transaction
     */
    Mono<OutgoingTransactionResponseDto> execute(String transactionId);

    /**
     * Update transaction status
     */
    Mono<Void> update(String transactionId, UpdateDto request);
}
