package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.client.request.ClientCheckRequestDto;
import kg.demirbank.psp.dto.client.request.ClientMakePaymentRequestDto;
import kg.demirbank.psp.dto.client.response.ClientCheckResponseDto;
import kg.demirbank.psp.dto.client.response.ClientMakePaymentResponseDto;
import kg.demirbank.psp.exception.*;
import kg.demirbank.psp.repository.OperationRepository;
import kg.demirbank.psp.service.BankService;
import kg.demirbank.psp.service.ClientService;
import kg.demirbank.psp.service.OperatorService;
import kg.demirbank.psp.service.clients.QrDecoderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * Implementation of client service
 * Contains business logic for client check and make payment operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    
    
    private final QrDecoderClient qrDecoderClient;
    private final BankService bankService;
    private final OperatorService operatorService;
    private final OperationRepository operationRepository;
    
    @Value("${merchant.provider}")
    private String configuredMerchantProvider;
    
    @Override
    public Mono<ClientCheckResponseDto> checkQrPayment(ClientCheckRequestDto request) {
        log.info("Starting QR payment check for URI: {}", request.getQrUri());
        
        return qrDecoderClient.decodeQrUri(request.getQrUri())
                .flatMap(elqrData -> {
                    log.debug("QR decoded successfully, ELQR data: {}", elqrData);
                    
                    // Determine which service to use based on merchant provider
                    String merchantProvider = elqrData.getMerchantProvider();
                    log.info("Routing QR check to service based on merchant provider: {}", merchantProvider);
                    
                    if (isBankProvider(merchantProvider)) {
                        log.debug("Using bank service for QR check");
                        return bankService.checkQrPayment(request);
                    } else {
                        log.debug("Using operator service for QR check");
                        return operatorService.checkQrPayment(request);
                    }
                })
                .onErrorMap(Exception.class, e -> {
                    log.error("Error during QR check: {}", e.getMessage(), e);
                    return new SystemErrorException("Failed to process QR check request");
                });
    }
    
    @Override
    public Mono<ClientMakePaymentResponseDto> makePayment(ClientMakePaymentRequestDto request) {
        log.info("Starting payment for session: {} with amount: {}", 
                request.getPaymentSessionId(), request.getAmount());
        
        // Get operation by paymentSessionId to determine merchant provider
        return Mono.fromCallable(() -> operationRepository.findByPaymentSessionId(request.getPaymentSessionId()))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.error(new ResourceNotFoundException("Session not found"))))
                .flatMap(operation -> {
                    log.debug("Found operation: {} with merchant provider: {}", operation.getId(), operation.getMerchantProvider());
                    
                    // Determine which service to use based on merchant provider
                    String merchantProvider = operation.getMerchantProvider();
                    log.info("Routing payment to service based on merchant provider: {}", merchantProvider);
                    
                    if (isBankProvider(merchantProvider)) {
                        log.debug("Using bank service for payment");
                        return bankService.makePayment(request);
                    } else {
                        log.debug("Using operator service for payment");
                        return operatorService.makePayment(request);
                    }
                })
                .onErrorMap(Exception.class, e -> {
                    log.error("Error during payment: {}", e.getMessage(), e);
                    if (e instanceof PspException) {
                        return e;
                    }
                    return new SystemErrorException("Failed to process payment request");
                });
    }
    
    /**
     * Determine if the merchant provider should use bank service
     * 
     * @param merchantProvider The merchant provider identifier
     * @return true if should use bank service, false for operator service
     */
    private boolean isBankProvider(String merchantProvider) {
        // Use bank service for configured provider (e.g., "demirbank")
        // Use operator service for all other providers
        return configuredMerchantProvider.equals(merchantProvider);
    }
}
