package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.client.request.ClientCheckRequestDto;
import kg.demirbank.psp.dto.client.request.ClientMakePaymentRequestDto;
import kg.demirbank.psp.dto.client.response.ClientCheckResponseDto;
import kg.demirbank.psp.dto.client.response.ClientMakePaymentResponseDto;
import reactor.core.publisher.Mono;

/**
 * Service for bank operations
 * Handles internal bank transactions and account validation
 */
public interface BankService {
    
    /**
     * Check QR payment details using bank client
     * 
     * @param request Client check request with QR URI
     * @return Check response with session ID, ELQR data, and beneficiary info
     */
    Mono<ClientCheckResponseDto> checkQrPayment(ClientCheckRequestDto request);
    
    /**
     * Make payment using bank client
     * 
     * @param request Client make payment request with session ID and amount
     * @return Payment response with receipt ID and transaction details
     */
    Mono<ClientMakePaymentResponseDto> makePayment(ClientMakePaymentRequestDto request);
}
