package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.bank.request.BankCheckRequestDto;
import kg.demirbank.psp.dto.bank.request.BankCreateRequestDto;
import kg.demirbank.psp.dto.client.request.ClientCheckRequestDto;
import kg.demirbank.psp.dto.client.request.ClientMakePaymentRequestDto;
import kg.demirbank.psp.dto.client.response.ClientCheckResponseDto;
import kg.demirbank.psp.dto.client.response.ClientMakePaymentResponseDto;
import kg.demirbank.psp.dto.common.ELQRData;
import kg.demirbank.psp.entity.OperationEntity;
import kg.demirbank.psp.enums.CustomerType;
import kg.demirbank.psp.enums.OperationType;
import kg.demirbank.psp.enums.Status;
import kg.demirbank.psp.exception.*;
import kg.demirbank.psp.repository.OperationRepository;
import kg.demirbank.psp.service.BankClient;
import kg.demirbank.psp.service.ClientService;
import kg.demirbank.psp.service.OperatorClient;
import kg.demirbank.psp.service.QrDecoderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of client service
 * Contains business logic for client check and make payment operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    
    
    private final QrDecoderClient qrDecoderClient;
    private final BankClient bankClient;
    private final OperatorClient operatorClient;
    private final OperationRepository operationRepository;
    
    @Override
    public Mono<ClientCheckResponseDto> checkQrPayment(ClientCheckRequestDto request) {
        log.info("Starting QR payment check for URI: {}", request.getQrUri());
        
        return qrDecoderClient.decodeQrUri(request.getQrUri())
                .flatMap(elqrData -> {
                    log.debug("QR decoded successfully, ELQR data: {}", elqrData);
                    
                    // Create operation entity for tracking
                    OperationEntity operation = createOperationEntity(
                            OperationType.CHECK, 
                            request.getQrUri(), 
                            elqrData
                    );
                    
                    return Mono.fromCallable(() -> operationRepository.save(operation))
                            .flatMap(savedOperation -> {
                                log.debug("Operation saved with ID: {}", savedOperation.getId());
                                
                                // Create response
                                ClientCheckResponseDto response = new ClientCheckResponseDto();
                                response.setCheckSessionId(savedOperation.getPspTransactionId());
                                response.setBeneficiaryName(elqrData.getMerchantId());
                                response.setQrType(elqrData.getQrType());
                                response.setMerchantProvider(elqrData.getMerchantProvider());
                                response.setMerchantId(elqrData.getMerchantId());
                                response.setServiceId(elqrData.getServiceId());
                                response.setServiceName(elqrData.getServiceName());
                                response.setBeneficiaryAccountNumber(elqrData.getBeneficiaryAccountNumber());
                                response.setMerchantCode(elqrData.getMerchantCode());
                                response.setCurrencyCode(elqrData.getCurrencyCode());
                                response.setQrTransactionId(elqrData.getQrTransactionId());
                                response.setQrComment(elqrData.getQrComment());
                                response.setQrLinkHash(elqrData.getQrLinkHash());
                                response.setExtra(elqrData.getExtra());
                                
                                log.info("QR check completed successfully for session: {}", response.getCheckSessionId());
                                return Mono.just(response);
                            });
                })
                .onErrorMap(Exception.class, e -> {
                    log.error("Error during QR check: {}", e.getMessage(), e);
                    return new SystemErrorException("Failed to process QR check request");
                });
    }
    
    @Override
    public Mono<ClientMakePaymentResponseDto> makePayment(ClientMakePaymentRequestDto request) {
        log.info("Starting payment for session: {} with amount: {}", 
                request.getCheckSessionId(), request.getAmount());
        
        return Mono.fromCallable(() -> operationRepository.findByPspTransactionId(request.getCheckSessionId()))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.error(new ResourceNotFoundException("Session not found"))))
                .flatMap(operation -> {
                    log.debug("Found operation: {}", operation.getId());
                    
                    // Update operation status
                    operation.setStatus(Status.IN_PROCESS);
                    operation.setAmount(request.getAmount());
                    operation.setUpdatedAt(LocalDateTime.now());
                    
                    return Mono.fromCallable(() -> operationRepository.save(operation))
                            .flatMap(updatedOperation -> {
                                log.debug("Operation updated, proceeding with payment");
                                
                                // Create bank check request
                                BankCheckRequestDto bankCheckRequest = new BankCheckRequestDto();
                                bankCheckRequest.setMerchantId(operation.getMerchantId());
                                bankCheckRequest.setBeneficiaryAccountNumber(operation.getBeneficiaryAccountNumber());
                                bankCheckRequest.setMerchantCode(operation.getMerchantCode());
                                bankCheckRequest.setAmount(request.getAmount());
                                
                                return bankClient.checkAccount(bankCheckRequest)
                                        .flatMap(bankCheckResponse -> {
                                            log.debug("Bank check completed: {}", bankCheckResponse.getAccountValid());
                                            
                                            if (!Boolean.TRUE.equals(bankCheckResponse.getAccountValid())) {
                                                return Mono.error(new BadRequestException("Account check failed"));
                                            }
                                            
                                            // Create bank transaction request
                                            BankCreateRequestDto bankCreateRequest = new BankCreateRequestDto();
                                            bankCreateRequest.setAmount(request.getAmount());
                                            bankCreateRequest.setCustomerType(operation.getCustomerType());
                                            bankCreateRequest.setQrType(operation.getQrType());
                                            bankCreateRequest.setMerchantProvider(operation.getMerchantProvider());
                                            bankCreateRequest.setMerchantId(operation.getMerchantId());
                                            bankCreateRequest.setServiceId(operation.getServiceId());
                                            bankCreateRequest.setServiceName(operation.getServiceName());
                                            bankCreateRequest.setBeneficiaryAccountNumber(operation.getBeneficiaryAccountNumber());
                                            bankCreateRequest.setMerchantCode(operation.getMerchantCode());
                                            bankCreateRequest.setCurrencyCode(operation.getCurrencyCode());
                                            bankCreateRequest.setQrTransactionId(operation.getQrTransactionId());
                                            bankCreateRequest.setQrComment(operation.getQrComment());
                                            bankCreateRequest.setQrLinkHash(operation.getQrLinkHash());
                                            
                                            return bankClient.createTransaction(bankCreateRequest)
                                                    .flatMap(bankTransactionResponse -> {
                                                        log.debug("Bank transaction created: {}", bankTransactionResponse.getTransactionId());
                                                        
                                                        // Update operation with transaction details
                                                        updatedOperation.setStatus(Status.SUCCESS);
                                                        updatedOperation.setTransactionId(bankTransactionResponse.getTransactionId());
                                                        updatedOperation.setReceiptId(bankTransactionResponse.getTransactionId()); // Using transaction ID as receipt ID
                                                        updatedOperation.setUpdatedAt(LocalDateTime.now());
                                                        
                                                        return Mono.fromCallable(() -> operationRepository.save(updatedOperation))
                                                                .map(finalOperation -> {
                                                                    log.info("Payment completed successfully for session: {}", request.getCheckSessionId());
                                                                    
                                                                    ClientMakePaymentResponseDto response = new ClientMakePaymentResponseDto();
                                                                    response.setReceiptId(finalOperation.getReceiptId());
                                                                    response.setTransactionId(finalOperation.getTransactionId());
                                                                    response.setAmount(finalOperation.getAmount());
                                                                    response.setStatus(finalOperation.getStatus());
                                                                    response.setCreatedDate(finalOperation.getUpdatedAt().toString());
                                                                    return response;
                                                                });
                                                    });
                                        });
                            });
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
     * Create operation entity for tracking
     */
    private OperationEntity createOperationEntity(OperationType type, String qrUri, ELQRData elqrData) {
        OperationEntity operation = new OperationEntity();
        operation.setPspTransactionId(UUID.randomUUID().toString());
        operation.setOperationType(type);
        operation.setTransferDirection("OUT"); // Outgoing from PSP
        operation.setQrType(elqrData.getQrType());
        operation.setMerchantProvider(elqrData.getMerchantProvider());
        operation.setMerchantId(elqrData.getMerchantId());
        operation.setServiceId(elqrData.getServiceId());
        operation.setServiceName(elqrData.getServiceName());
        operation.setBeneficiaryAccountNumber(elqrData.getBeneficiaryAccountNumber());
        operation.setMerchantCode(elqrData.getMerchantCode());
        operation.setCurrencyCode(elqrData.getCurrencyCode());
        operation.setQrTransactionId(elqrData.getQrTransactionId());
        operation.setQrComment(elqrData.getQrComment());
        operation.setCustomerType(CustomerType.INDIVIDUAL); // Default to individual
        operation.setAmount(0L); // Amount will be set later in makePayment
        operation.setQrLinkHash(elqrData.getQrLinkHash());
        operation.setStatus(Status.CREATED);
        return operation;
    }
}
