package kg.demirbank.psp.service.clients.impl;

import kg.demirbank.psp.dto.bank.request.BankCheckRequestDto;
import kg.demirbank.psp.dto.bank.request.BankCreateRequestDto;
import kg.demirbank.psp.dto.bank.response.BankCheckResponseDto;
import kg.demirbank.psp.dto.bank.response.BankTransactionResponseDto;
import kg.demirbank.psp.enums.Status;
import kg.demirbank.psp.enums.TransactionType;
import kg.demirbank.psp.service.clients.BankClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implementation of bank client service
 * Mock implementation for internal bank operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankClientImpl implements BankClient {
    
    @Value("${bank.service.base-url:}")
    private String bankServiceBaseUrl;
    
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public Mono<BankCheckResponseDto> checkAccount(BankCheckRequestDto request) {
        return Mono.fromCallable(() -> {
            log.info("Checking bank account for merchant: {}, account: {}", 
                    request.getMerchantId(), request.getBeneficiaryAccountNumber());
            
            // Mock bank account validation
            BankCheckResponseDto response = new BankCheckResponseDto();
            response.setBeneficiaryName("c***e A***o"); // Masked beneficiary name
            response.setTransactionType(TransactionType.C2C); // Default to C2C
            response.setAccountValid(true); // Mock: account is always valid
            
            log.info("Bank account check completed for account: {}", request.getBeneficiaryAccountNumber());
            return response;
        });
    }
    
    @Override
    public Mono<BankTransactionResponseDto> createTransaction(BankCreateRequestDto request) {
        return Mono.fromCallable(() -> {
            log.info("Creating bank transaction for merchant: {}, amount: {}", 
                    request.getMerchantId(), request.getAmount());
            
            // Mock bank transaction creation
            BankTransactionResponseDto response = new BankTransactionResponseDto();
            response.setTransactionId(UUID.randomUUID().toString()); // Generate bank transaction ID
            response.setStatus(Status.CREATED); // Default status
            response.setCreatedDate(LocalDateTime.now().format(ISO_DATE_TIME) + "Z");
            
            log.info("Bank transaction created with ID: {}", response.getTransactionId());
            return response;
        });
    }
}
