package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.merchant.request.MerchantCheckRequestDto;
import kg.demirbank.psp.dto.merchant.request.MerchantMakePaymentRequestDto;
import kg.demirbank.psp.dto.merchant.response.MerchantCheckResponseDto;
import kg.demirbank.psp.dto.merchant.response.MerchantMakePaymentResponseDto;
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
     * @param request Merchant check request with QR URI
     * @return Check response with session ID, ELQR data, and beneficiary info
     */
    Mono<MerchantCheckResponseDto> checkQrPayment(MerchantCheckRequestDto request);
    
    /**
     * Make payment after checking QR details
     * Creates transaction and returns payment confirmation
     * 
     * @param request Merchant make payment request with session ID and amount
     * @return Payment response with receipt ID and transaction details
     */
    Mono<MerchantMakePaymentResponseDto> makePayment(MerchantMakePaymentRequestDto request);
}
