package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.client.request.ClientCheckRequestDto;
import kg.demirbank.psp.dto.client.request.ClientMakePaymentRequestDto;
import kg.demirbank.psp.dto.client.response.ClientCheckResponseDto;
import kg.demirbank.psp.dto.client.response.ClientMakePaymentResponseDto;
import reactor.core.publisher.Mono;

/**
 * Service for operator operations
 * Handles external operator interactions and transaction management
 */
public interface OperatorService {
    
    /**
     * Check QR payment details using operator client
     * 
     * @param request Client check request with QR URI
     * @return Check response with session ID, ELQR data, and beneficiary info
     */
    Mono<ClientCheckResponseDto> checkQrPayment(ClientCheckRequestDto request);
    
    /**
     * Make payment using operator client
     * 
     * @param request Client make payment request with session ID and amount
     * @return Payment response with receipt ID and transaction details
     */
    Mono<ClientMakePaymentResponseDto> makePayment(ClientMakePaymentRequestDto request);
}
