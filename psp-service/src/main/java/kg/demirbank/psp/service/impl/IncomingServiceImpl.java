package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.*;
import kg.demirbank.psp.enums.CustomerType;
import kg.demirbank.psp.enums.Status;
import kg.demirbank.psp.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implementation of incoming service
 * Contains business logic for incoming transaction operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IncomingServiceImpl implements kg.demirbank.psp.service.IncomingService {
    
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public Mono<CheckResponseDto> checkTransaction(CheckRequestDto request) {
        log.debug("Processing check transaction request: {}", request);
        
        return Mono.fromCallable(() -> {
            // Business logic validation
            validateCheckRequest(request);
            
            // Simulate beneficiary lookup based on merchant data
            String beneficiaryName = lookupBeneficiaryName(request);
            CustomerType transactionType = determineTransactionType(request);
            
            CheckResponseDto response = new CheckResponseDto();
            response.setBeneficiaryName(beneficiaryName);
            response.setTransactionType(transactionType);
            
            log.debug("Check transaction response: {}", response);
            return response;
        })
        .doOnSuccess(unused -> log.info("Check transaction completed successfully for merchant: {}", 
                request.getMerchantProvider()))
        .doOnError(error -> log.error("Check transaction failed for merchant: {}", 
                request.getMerchantProvider(), error));
    }
    
    @Override
    public Mono<CreateResponseDto> createTransaction(CreateRequestDto request) {
        log.debug("Processing create transaction request: {}", request);
        
        return Mono.fromCallable(() -> {
            // Business logic validation
            validateCreateRequest(request);
            
            // Generate transaction ID if not provided
            String transactionId = request.getTransactionId() != null ? 
                    request.getTransactionId() : generateTransactionId();
            
            // Simulate transaction creation
            CreateResponseDto response = new CreateResponseDto();
            response.setTransactionId(transactionId);
            response.setStatus(Status.CREATED);
            response.setTransactionType(request.getTransactionType());
            response.setAmount(request.getAmount());
            response.setBeneficiaryName("Sample Beneficiary");
            response.setCustomerType(Integer.parseInt(request.getCustomerType()));
            response.setReceiptId(request.getReceiptId());
            response.setCreatedDate(LocalDateTime.now().format(ISO_DATE_TIME) + "Z");
            response.setExecutedDate("");
            
            log.debug("Create transaction response: {}", response);
            return response;
        })
        .doOnSuccess(unused -> log.info("Create transaction completed successfully"))
        .doOnError(error -> log.error("Create transaction failed for PSP transaction ID: {}", 
                request.getPspTransactionId(), error));
    }
    
    @Override
    public Mono<StatusDto> executeTransaction(String transactionId) {
        log.debug("Processing execute transaction request for ID: {}", transactionId);
        
        return Mono.fromCallable(() -> {
            // Validate transaction ID
            if (transactionId == null || transactionId.trim().isEmpty()) {
                throw new BadRequestException("Transaction ID is required");
            }
            
            // Simulate transaction execution
            StatusDto response = new StatusDto();
            response.setTransactionId(transactionId);
            response.setStatus(Status.SUCCESS);
            response.setTransactionType(CustomerType.C2C);
            response.setAmount(40000L);
            response.setBeneficiaryName("c***e A***o");
            response.setCustomerType("1");
            response.setReceiptId("7218199");
            response.setCreatedDate("2022-11-01T12:00:00Z");
            response.setExecutedDate(LocalDateTime.now().format(ISO_DATE_TIME) + "Z");
            
            log.debug("Execute transaction response: {}", response);
            return response;
        })
        .doOnSuccess(response -> log.info("Execute transaction completed successfully for ID: {}", 
                transactionId))
        .doOnError(error -> log.error("Execute transaction failed for ID: {}", 
                transactionId, error));
    }
    
    @Override
    public Mono<Void> updateTransaction(String transactionId, UpdateDto updateRequest) {
        log.debug("Processing update transaction request for ID: {} with status: {}", 
                transactionId, updateRequest.getStatus());
        
        return Mono.fromCallable(() -> {
            // Validate transaction ID
            if (transactionId == null || transactionId.trim().isEmpty()) {
                throw new BadRequestException("Transaction ID is required");
            }
            
            // Validate update request
            validateUpdateRequest(updateRequest);
            
            // Simulate transaction update
            log.info("Transaction {} updated to status: {} at {}", 
                    transactionId, updateRequest.getStatus(), updateRequest.getUpdateDate());
            return null; // Return null for Void
        })
        .then()
        .doOnSuccess(unused -> log.info("Update transaction completed successfully for ID: {}", 
                transactionId))
        .doOnError(error -> log.error("Update transaction failed for ID: {}", 
                transactionId, error));
    }
    
    /**
     * Validate check request business rules
     */
    private void validateCheckRequest(CheckRequestDto request) {
        // Validate amount limits
        if (request.getAmount() < 100) {
            throw new MinAmountNotValidException("Minimum amount is 100");
        }
        if (request.getAmount() > 1000000) {
            throw new MaxAmountNotValidException("Maximum amount is 1000000");
        }
        
        // Validate merchant code
        if (request.getMerchantCode() < 0 || request.getMerchantCode() > 9999) {
            throw new IncorrectRequestDataException("Merchant code must be between 0 and 9999");
        }
        
        // Validate currency code
        if (!"417".equals(request.getCurrencyCode())) {
            throw new IncorrectRequestDataException("Only KGS currency (417) is supported");
        }
    }
    
    /**
     * Validate create request business rules
     */
    private void validateCreateRequest(CreateRequestDto request) {
        // Validate amount limits
        if (request.getAmount() < 100) {
            throw new MinAmountNotValidException("Minimum amount is 100");
        }
        if (request.getAmount() > 1000000) {
            throw new MaxAmountNotValidException("Maximum amount is 1000000");
        }
        
        // Validate merchant code
        if (request.getMerchantCode() < 0 || request.getMerchantCode() > 9999) {
            throw new IncorrectRequestDataException("Merchant code must be between 0 and 9999");
        }
        
        // Validate currency code
        if (!"417".equals(request.getCurrencyCode())) {
            throw new IncorrectRequestDataException("Only KGS currency (417) is supported");
        }
        
        // Validate PSP transaction ID format
        if (request.getPspTransactionId() == null || request.getPspTransactionId().trim().isEmpty()) {
            throw new IncorrectRequestDataException("PSP transaction ID is required");
        }
        
        // Validate receipt ID format
        if (request.getReceiptId() == null || request.getReceiptId().trim().isEmpty()) {
            throw new IncorrectRequestDataException("Receipt ID is required");
        }
    }
    
    /**
     * Validate update request business rules
     */
    private void validateUpdateRequest(UpdateDto request) {
        if (request.getStatus() == null) {
            throw new IncorrectRequestDataException("Status is required");
        }
        
        if (request.getUpdateDate() == null || request.getUpdateDate().trim().isEmpty()) {
            throw new IncorrectRequestDataException("Update date is required");
        }
    }
    
    /**
     * Lookup beneficiary name based on merchant data
     */
    private String lookupBeneficiaryName(CheckRequestDto request) {
        // Simulate beneficiary lookup logic
        // In real implementation, this would query merchant database
        return "c***e A***o";
    }
    
    /**
     * Determine transaction type based on request data
     */
    private CustomerType determineTransactionType(CheckRequestDto request) {
        // Simulate transaction type determination
        // In real implementation, this would be based on business rules
        return CustomerType.C2C;
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}