package kg.demirbank.psp.service.clients;

import kg.demirbank.psp.dto.bank.request.BankCheckRequestDto;
import kg.demirbank.psp.dto.bank.request.BankCreateRequestDto;
import kg.demirbank.psp.dto.bank.response.BankCheckResponseDto;
import kg.demirbank.psp.dto.bank.response.BankTransactionResponseDto;
import reactor.core.publisher.Mono;

/**
 * Service for bank operations
 * Handles internal bank transactions and account validation
 */
public interface BankClient {
    
    /**
     * Check beneficiary account in our bank
     * 
     * @param request Bank check request with account details
     * @return Mono containing bank check response with beneficiary info
     */
    Mono<BankCheckResponseDto> checkAccount(BankCheckRequestDto request);
    
    /**
     * Create internal bank transaction
     * 
     * @param request Bank create request with transaction details
     * @return Mono containing bank transaction response
     */
    Mono<BankTransactionResponseDto> createTransaction(BankCreateRequestDto request);
}
