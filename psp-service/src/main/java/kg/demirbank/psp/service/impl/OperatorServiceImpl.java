package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.client.request.ClientCheckRequestDto;
import kg.demirbank.psp.dto.client.request.ClientMakePaymentRequestDto;
import kg.demirbank.psp.dto.client.response.ClientCheckResponseDto;
import kg.demirbank.psp.dto.client.response.ClientMakePaymentResponseDto;
import kg.demirbank.psp.dto.common.ELQRData;
import kg.demirbank.psp.dto.outgoing.request.OutgoingCheckRequestDto;
import kg.demirbank.psp.dto.outgoing.request.OutgoingCreateRequestDto;
import kg.demirbank.psp.entity.OperationEntity;
import kg.demirbank.psp.enums.CustomerType;
import kg.demirbank.psp.enums.OperationType;
import kg.demirbank.psp.enums.Status;
import kg.demirbank.psp.exception.*;
import kg.demirbank.psp.repository.OperationRepository;
import kg.demirbank.psp.service.clients.OperatorClient;
import kg.demirbank.psp.service.OperatorService;
import kg.demirbank.psp.service.clients.QrDecoderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of operator service
 * Handles full client operations using operator client
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorServiceImpl implements OperatorService {
    
    private final OperatorClient operatorClient;
    private final QrDecoderClient qrDecoderClient;
    private final OperationRepository operationRepository;
    
    @Override
    public Mono<ClientCheckResponseDto> checkQrPayment(ClientCheckRequestDto request) {
        log.info("Starting operator QR payment check for URI: {}", request.getQrUri());
        
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
                                response.setPaymentSessionId(savedOperation.getPaymentSessionId());
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
                                
                                log.info("Operator QR check completed successfully for session: {}", response.getPaymentSessionId());
                                return Mono.just(response);
                            });
                })
                .onErrorMap(Exception.class, e -> {
                    log.error("Error during operator QR check: {}", e.getMessage(), e);
                    return new SystemErrorException("Failed to process operator QR check request");
                });
    }
    
    @Override
    public Mono<ClientMakePaymentResponseDto> makePayment(ClientMakePaymentRequestDto request) {
        log.info("Starting operator payment for session: {} with amount: {}", 
                request.getPaymentSessionId(), request.getAmount());
        
        return Mono.fromCallable(() -> operationRepository.findByPaymentSessionId(request.getPaymentSessionId()))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.error(new ResourceNotFoundException("Session not found"))))
                .flatMap(operation -> {
                    log.debug("Found operation: {}", operation.getId());
                    
                    // Check if this operation should be handled by operator service
                    if ("demirbank".equals(operation.getMerchantProvider())) {
                        log.debug("Operation is for bank service, merchant provider: {}", operation.getMerchantProvider());
                        return Mono.error(new ResourceNotFoundException("Session not found for operator service"));
                    }
                    
                    // Update operation status
                    operation.setStatus(Status.IN_PROCESS);
                    operation.setAmount(request.getAmount());
                    operation.setUpdatedAt(LocalDateTime.now());
                    
                    return Mono.fromCallable(() -> operationRepository.save(operation))
                            .flatMap(updatedOperation -> {
                                log.debug("Operation updated, proceeding with operator payment");
                                
                                // Create outgoing check request
                                OutgoingCheckRequestDto outgoingCheckRequest = new OutgoingCheckRequestDto();
                                outgoingCheckRequest.setMerchantId(operation.getMerchantId());
                                outgoingCheckRequest.setBeneficiaryAccountNumber(operation.getBeneficiaryAccountNumber());
                                outgoingCheckRequest.setMerchantCode(operation.getMerchantCode());
                                outgoingCheckRequest.setAmount(request.getAmount());
                                
                                return operatorClient.check(outgoingCheckRequest)
                                        .flatMap(outgoingCheckResponse -> {
                                            log.debug("Operator check completed for account: {}, beneficiary: {}", 
                                                    operation.getBeneficiaryAccountNumber(), outgoingCheckResponse.getBeneficiaryName());
                                            
                                            // Create outgoing transaction request
                                            OutgoingCreateRequestDto outgoingCreateRequest = new OutgoingCreateRequestDto();
                                            outgoingCreateRequest.setAmount(request.getAmount());
                                            outgoingCreateRequest.setCustomerType(operation.getCustomerType());
                                            outgoingCreateRequest.setQrType(operation.getQrType());
                                            outgoingCreateRequest.setMerchantProvider(operation.getMerchantProvider());
                                            outgoingCreateRequest.setMerchantId(operation.getMerchantId());
                                            outgoingCreateRequest.setServiceId(operation.getServiceId());
                                            outgoingCreateRequest.setServiceName(operation.getServiceName());
                                            outgoingCreateRequest.setBeneficiaryAccountNumber(operation.getBeneficiaryAccountNumber());
                                            outgoingCreateRequest.setMerchantCode(operation.getMerchantCode());
                                            outgoingCreateRequest.setCurrencyCode(operation.getCurrencyCode());
                                            outgoingCreateRequest.setQrTransactionId(operation.getQrTransactionId());
                                            outgoingCreateRequest.setQrComment(operation.getQrComment());
                                            outgoingCreateRequest.setQrLinkHash(operation.getQrLinkHash());
                                            
                                            return operatorClient.create(outgoingCreateRequest)
                                                    .flatMap(outgoingTransactionResponse -> {
                                                        log.debug("Operator transaction created: {}", outgoingTransactionResponse.getTransactionId());
                                                        
                                                        // Update operation with transaction details
                                                        updatedOperation.setStatus(Status.SUCCESS);
                                                        updatedOperation.setTransactionId(outgoingTransactionResponse.getTransactionId());
                                                        updatedOperation.setReceiptId(outgoingTransactionResponse.getTransactionId());
                                                        updatedOperation.setUpdatedAt(LocalDateTime.now());
                                                        
                                                        return Mono.fromCallable(() -> operationRepository.save(updatedOperation))
                                                                .map(finalOperation -> {
                                                                    log.info("Operator payment completed successfully for session: {}", request.getPaymentSessionId());
                                                                    
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
                    log.error("Error during operator payment: {}", e.getMessage(), e);
                    if (e instanceof PspException) {
                        return e;
                    }
                    return new SystemErrorException("Failed to process operator payment request");
                });
    }
    
    /**
     * Create operation entity for tracking
     */
    private OperationEntity createOperationEntity(OperationType type, String qrUri, ELQRData elqrData) {
        OperationEntity operation = new OperationEntity();
        operation.setPspTransactionId(UUID.randomUUID().toString());
        operation.setPaymentSessionId(UUID.randomUUID().toString()); // Generate payment session ID
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
