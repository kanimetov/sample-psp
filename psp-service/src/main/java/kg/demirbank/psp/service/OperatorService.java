package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.merchant.request.MerchantCheckRequestDto;
import kg.demirbank.psp.dto.merchant.request.MerchantMakePaymentRequestDto;
import kg.demirbank.psp.dto.merchant.response.MerchantCheckResponseDto;
import kg.demirbank.psp.dto.merchant.response.MerchantMakePaymentResponseDto;
import kg.demirbank.psp.dto.common.ELQRData;
import reactor.core.publisher.Mono;

/**
 * Service for operator operations
 * Handles external operator interactions and transaction management
 */
public interface OperatorService {
    
    /**
     * Check QR payment details using operator client
     * 
     * @param request Merchant check request with QR URI
     * @param elqrData Decoded ELQR data from QR URI
     * @return Check response with session ID, ELQR data, and beneficiary info
     */
    Mono<MerchantCheckResponseDto> checkQrPayment(MerchantCheckRequestDto request, ELQRData elqrData);
    
    /**
     * Make payment using operator client
     * 
     * @param request Merchant make payment request with session ID and amount
     * @return Payment response with receipt ID and transaction details
     */
    Mono<MerchantMakePaymentResponseDto> makePayment(MerchantMakePaymentRequestDto request);
}
