package kg.demirbank.psp.service;

import kg.demirbank.psp.dto.merchant.request.MerchantCheckRequestDto;
import kg.demirbank.psp.dto.merchant.request.MerchantMakePaymentRequestDto;
import kg.demirbank.psp.dto.merchant.response.MerchantCheckResponseDto;
import kg.demirbank.psp.dto.merchant.response.MerchantMakePaymentResponseDto;
import kg.demirbank.psp.dto.incoming.request.IncomingCheckRequestDto;
import kg.demirbank.psp.dto.incoming.request.IncomingCreateRequestDto;
import kg.demirbank.psp.dto.incoming.response.IncomingCheckResponseDto;
import kg.demirbank.psp.dto.incoming.response.IncomingTransactionResponseDto;
import kg.demirbank.psp.dto.common.ELQRData;
import reactor.core.publisher.Mono;

/**
 * Service for bank operations
 * Handles internal bank transactions and account validation
 */
public interface BankService {
    
    /**
     * Check QR payment details using bank client
     * 
     * @param request Merchant check request with QR URI
     * @param elqrData Decoded ELQR data from QR URI
     * @return Check response with session ID, ELQR data, and beneficiary info
     */
    Mono<MerchantCheckResponseDto> checkQrPayment(MerchantCheckRequestDto request, ELQRData elqrData);
    
    /**
     * Make payment using bank client
     * 
     * @param request Merchant make payment request with session ID and amount
     * @return Payment response with receipt ID and transaction details
     */
    Mono<MerchantMakePaymentResponseDto> makePayment(MerchantMakePaymentRequestDto request);
    
    /**
     * Check incoming transaction using direct parameters
     * 
     * @param request Incoming check request with all transaction parameters
     * @return Check response with beneficiary information
     */
    Mono<IncomingCheckResponseDto> checkIncomingTransaction(IncomingCheckRequestDto request);
    
    /**
     * Create incoming transaction using direct parameters
     * 
     * @param request Incoming create request with all transaction parameters
     * @return Transaction response with transaction details
     */
    Mono<IncomingTransactionResponseDto> createIncomingTransaction(IncomingCreateRequestDto request);
    
    /**
     * Execute incoming transaction using operator's transaction ID
     * 
     * @param transactionId Operator's transaction ID to execute
     * @return Transaction response with execution status
     */
    Mono<IncomingTransactionResponseDto> executeIncomingTransaction(String transactionId);
    
    /**
     * Update incoming transaction status
     * 
     * @param transactionId Transaction ID to update
     * @param updateRequest Update request with new status
     * @return Void when update is successful
     */
    Mono<Void> updateIncomingTransaction(String transactionId, kg.demirbank.psp.dto.common.UpdateDto updateRequest);
}
