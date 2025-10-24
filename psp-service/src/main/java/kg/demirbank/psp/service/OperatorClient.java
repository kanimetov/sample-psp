package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.outgoing.request.CheckRequestDto;
import kg.demirbank.psp.dto.outgoing.request.CreateRequestDto;
import kg.demirbank.psp.dto.common.UpdateDto;
import kg.demirbank.psp.dto.outgoing.response.CheckResponseDto;
import kg.demirbank.psp.dto.outgoing.response.CreateResponseDto;
import kg.demirbank.psp.dto.outgoing.response.ExecuteResponseDto;
import kg.demirbank.psp.dto.outgoing.response.StatusDto;
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
    Mono<ExecuteResponseDto> execute(String transactionId);

    /**
     * Update transaction status
     */
    Mono<Void> update(String transactionId, UpdateDto request);
}
