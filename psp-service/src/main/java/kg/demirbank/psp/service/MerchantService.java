package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.client.request.ClientCheckRequestDto;
import kg.demirbank.psp.dto.client.request.ClientMakePaymentRequestDto;
import kg.demirbank.psp.dto.client.response.ClientCheckResponseDto;
import kg.demirbank.psp.dto.client.response.ClientMakePaymentResponseDto;
import reactor.core.publisher.Mono;

/**
 * Service for merchant transaction business logic
 * Handles merchant check and make payment operations
 */
public interface MerchantService {
    
    /**
     * Check QR payment details
     * Decodes QR, validates data, and returns beneficiary information
     * 
     * @param request Client check request with QR URI
     * @return Check response with session ID, ELQR data, and beneficiary info
     */
    Mono<ClientCheckResponseDto> checkQrPayment(ClientCheckRequestDto request);
    
    /**
     * Make payment after checking QR details
     * Creates transaction and returns payment confirmation
     * 
     * @param request Client make payment request with QR URI and amount
     * @return Payment response with receipt ID and transaction details
     */
    Mono<ClientMakePaymentResponseDto> makePayment(ClientMakePaymentRequestDto request);
}
