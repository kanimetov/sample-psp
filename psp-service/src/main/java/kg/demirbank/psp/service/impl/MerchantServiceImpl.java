package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.merchant.request.MerchantCheckRequestDto;
import kg.demirbank.psp.dto.merchant.request.MerchantMakePaymentRequestDto;
import kg.demirbank.psp.dto.merchant.response.MerchantCheckResponseDto;
import kg.demirbank.psp.dto.merchant.response.MerchantMakePaymentResponseDto;
import kg.demirbank.psp.exception.*;
import kg.demirbank.psp.repository.OperationRepository;
import kg.demirbank.psp.service.BankService;
import kg.demirbank.psp.service.MerchantService;
import kg.demirbank.psp.service.OperatorService;
import kg.demirbank.psp.service.clients.QrDecoderClient;
import kg.demirbank.psp.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * Implementation of merchant service
 * Contains business logic for merchant check and make payment operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantServiceImpl implements MerchantService {
    
    
    private final QrDecoderClient qrDecoderClient;
    private final BankService bankService;
    private final OperatorService operatorService;
    private final OperationRepository operationRepository;
    
    @Value("${merchant.provider}")
    private String configuredMerchantProvider;
    
    @Override
    public Mono<MerchantCheckResponseDto> checkQrPayment(MerchantCheckRequestDto request) {
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
                .onErrorMap(throwable -> {
                    // Log error with structured data - no stack trace for business errors
                    boolean isPspException = throwable instanceof PspException;
                    String errorMessage = throwable.getMessage();
                    String errorCode = isPspException ? 
                        ((PspException) throwable).getCode().toString() : "UNKNOWN_ERROR";
                    LoggingUtil.logError("QR_CHECK", null, errorCode, errorMessage, throwable);
                    
                    if (isPspException) {
                        return throwable; // Preserve original PspException
                    }
                    log.error("Unexpected error during QR check: {}", throwable.getMessage(), throwable);
                    return new SystemErrorException("Failed to process QR check request", throwable);
                });
    }
    
    @Override
    public Mono<MerchantMakePaymentResponseDto> makePayment(MerchantMakePaymentRequestDto request) {
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
                .onErrorMap(throwable -> {
                    // Log error with structured data - no stack trace for business errors
                    boolean isPspException = throwable instanceof PspException;
                    String errorMessage = throwable.getMessage();
                    String errorCode = isPspException ? 
                        ((PspException) throwable).getCode().toString() : "UNKNOWN_ERROR";
                    LoggingUtil.logError("MAKE_PAYMENT", null, errorCode, errorMessage, throwable);
                    
                    if (isPspException) {
                        return throwable; // Preserve original PspException
                    }
                    log.error("Unexpected error during payment: {}", throwable.getMessage(), throwable);
                    return new SystemErrorException("Failed to process payment request", throwable);
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
