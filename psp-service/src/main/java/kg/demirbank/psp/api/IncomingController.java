package kg.demirbank.psp.api;

import kg.demirbank.psp.dto.*;
import kg.demirbank.psp.exception.BadRequestException;
import kg.demirbank.psp.exception.SignatureVerificationException;
import kg.demirbank.psp.security.SignatureService;
import kg.demirbank.psp.service.IncomingService;
import kg.demirbank.psp.util.JsonUtil;
import kg.demirbank.psp.util.LoggingUtil;
import kg.demirbank.psp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Incoming controller handles:
 * 1. External API from clients (PSP external endpoints)
 * 2. Incoming requests from Operator (beneficiary side)
 * 
 * Note: Signature verification is performed inside controller methods
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class IncomingController {
    
    private final SignatureService signatureService;
    private final JsonUtil jsonUtil;
    private final ValidationUtil validationUtil;
    private final IncomingService incomingService;
    
    @PostMapping("/in/qr/{version}/tx/check")
    public Mono<ResponseEntity<CheckResponseDto>> inboundCheck(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        long startTime = System.currentTimeMillis();
        
        // Set operation context
        LoggingUtil.setOperationContext("CHECK_TRANSACTION", null, null, null, version);
        
        // Create properties for structured logging
        Map<String, Object> properties = new HashMap<>();
        properties.put("operationType", "CHECK_TRANSACTION");
        properties.put("apiVersion", version);
        properties.put("uri", "/in/qr/" + version + "/tx/check");
        
        LoggingUtil.logOperationStart("CHECK_TRANSACTION", properties);
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/check";
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            LoggingUtil.logSignatureVerification(false, verificationResult.getErrorMessage(), properties);
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        LoggingUtil.logSignatureVerification(true, "Signature verified successfully", properties);
        
        // Deserialize JSON after successful signature verification
        CheckRequestDto body = jsonUtil.fromJson(rawBody, CheckRequestDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        // Add request properties to logging context
        Map<String, Object> requestProperties = new HashMap<>(properties);
        requestProperties.put("merchantProvider", body.getMerchantProvider());
        requestProperties.put("merchantCode", body.getMerchantCode());
        requestProperties.put("qrType", body.getQrType());
        requestProperties.put("amount", body.getAmount());
        requestProperties.put("currencyCode", body.getCurrencyCode());
        
        LoggingUtil.logOperationStart("CHECK_TRANSACTION", requestProperties);
        
        // Process the request using business service
        return incomingService.checkTransaction(body)
                .map(response -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    LoggingUtil.setResponseTime(responseTime);
                    LoggingUtil.logOperationSuccess("CHECK_TRANSACTION", requestProperties);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    LoggingUtil.setResponseTime(responseTime);
                    LoggingUtil.logError("CHECK_TRANSACTION", "CONTROLLER_ERROR", 
                            e.getMessage(), e, requestProperties);
                    return Mono.error(e); // Let GlobalExceptionHandler handle it
                });
    }

    @PostMapping("/in/qr/{version}/tx/create")
    public Mono<ResponseEntity<CreateResponseDto>> inboundCreate(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        long startTime = System.currentTimeMillis();
        
        // Set operation context
        LoggingUtil.setOperationContext("CREATE_TRANSACTION", null, null, null, version);
        
        // Create properties for structured logging
        Map<String, Object> properties = new HashMap<>();
        properties.put("operationType", "CREATE_TRANSACTION");
        properties.put("apiVersion", version);
        properties.put("uri", "/in/qr/" + version + "/tx/create");
        
        LoggingUtil.logOperationStart("CREATE_TRANSACTION", properties);
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/create";
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            LoggingUtil.logSignatureVerification(false, verificationResult.getErrorMessage(), properties);
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        LoggingUtil.logSignatureVerification(true, "Signature verified successfully", properties);
        
        // Deserialize JSON after successful signature verification
        CreateRequestDto body = jsonUtil.fromJson(rawBody, CreateRequestDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        // Add request properties to logging context
        Map<String, Object> requestProperties = new HashMap<>(properties);
        requestProperties.put("transactionId", body.getTransactionId());
        requestProperties.put("pspTransactionId", body.getPspTransactionId());
        requestProperties.put("receiptId", body.getReceiptId());
        requestProperties.put("merchantProvider", body.getMerchantProvider());
        requestProperties.put("merchantCode", body.getMerchantCode());
        requestProperties.put("qrType", body.getQrType());
        requestProperties.put("amount", body.getAmount());
        requestProperties.put("currencyCode", body.getCurrencyCode());
        
        LoggingUtil.logOperationStart("CREATE_TRANSACTION", requestProperties);
        
        // Process the request using business service
        return incomingService.createTransaction(body)
                .map(response -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    LoggingUtil.setResponseTime(responseTime);
                    LoggingUtil.logOperationSuccess("CREATE_TRANSACTION", requestProperties);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    LoggingUtil.setResponseTime(responseTime);
                    LoggingUtil.logError("CREATE_TRANSACTION", "CONTROLLER_ERROR", 
                            e.getMessage(), e, requestProperties);
                    return Mono.error(e); // Let GlobalExceptionHandler handle it
                });
    }

    @PostMapping("/in/qr/{version}/tx/execute/{transactionId}")
    public Mono<ResponseEntity<StatusDto>> inboundExecute(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash) {
        
        long startTime = System.currentTimeMillis();
        
        // Set operation context
        LoggingUtil.setOperationContext("EXECUTE_TRANSACTION", null, null, null, version);
        LoggingUtil.setTransactionContext(transactionId, null, null, null, null, null);
        
        // Create properties for structured logging
        Map<String, Object> properties = new HashMap<>();
        properties.put("operationType", "EXECUTE_TRANSACTION");
        properties.put("apiVersion", version);
        properties.put("transactionId", transactionId);
        properties.put("uri", "/in/qr/" + version + "/tx/execute/" + transactionId);
        
        LoggingUtil.logOperationStart("EXECUTE_TRANSACTION", properties);
        
        // Verify signature for URI (no body for this request)
        String uri = "/in/qr/" + version + "/tx/execute/" + transactionId;
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(null, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            LoggingUtil.logSignatureVerification(false, verificationResult.getErrorMessage(), properties);
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        LoggingUtil.logSignatureVerification(true, "Signature verified successfully", properties);
        
        // Process the request using business service
        return incomingService.executeTransaction(transactionId)
                .map(response -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    LoggingUtil.setResponseTime(responseTime);
                    LoggingUtil.logOperationSuccess("EXECUTE_TRANSACTION", properties);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    LoggingUtil.setResponseTime(responseTime);
                    LoggingUtil.logError("EXECUTE_TRANSACTION", "CONTROLLER_ERROR", 
                            e.getMessage(), e, properties);
                    return Mono.error(e); // Let GlobalExceptionHandler handle it
                });
    }

    @PostMapping("/in/qr/{version}/tx/update/{transactionId}")
    public Mono<ResponseEntity<String>> inboundUpdate(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        long startTime = System.currentTimeMillis();
        
        if (version == null || version.isBlank()) {
            return Mono.error(new BadRequestException("QR version not specified"));
        }
        if (transactionId == null || transactionId.isBlank()) {
            return Mono.error(new BadRequestException("Transaction ID not specified"));
        }
        
        // Set operation context
        LoggingUtil.setOperationContext("UPDATE_TRANSACTION", null, null, null, version);
        LoggingUtil.setTransactionContext(transactionId, null, null, null, null, null);
        
        // Create properties for structured logging
        Map<String, Object> properties = new HashMap<>();
        properties.put("operationType", "UPDATE_TRANSACTION");
        properties.put("apiVersion", version);
        properties.put("transactionId", transactionId);
        properties.put("uri", "/in/qr/" + version + "/tx/update/" + transactionId);
        
        LoggingUtil.logOperationStart("UPDATE_TRANSACTION", properties);
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/update/" + transactionId;
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            LoggingUtil.logSignatureVerification(false, verificationResult.getErrorMessage(), properties);
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        LoggingUtil.logSignatureVerification(true, "Signature verified successfully", properties);
        
        // Deserialize JSON after successful signature verification
        UpdateDto body = jsonUtil.fromJson(rawBody, UpdateDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        // Add request properties to logging context
        Map<String, Object> requestProperties = new HashMap<>(properties);
        requestProperties.put("newStatus", body.getStatus().name());
        requestProperties.put("updateDate", body.getUpdateDate());
        
        LoggingUtil.logOperationStart("UPDATE_TRANSACTION", requestProperties);
        
        // Process the request using business service - ACK response (200 OK empty body)
        return incomingService.updateTransaction(transactionId, body)
                .then(Mono.just(ResponseEntity.ok("OK")))
                .doOnSuccess(unused -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    LoggingUtil.setResponseTime(responseTime);
                    LoggingUtil.logOperationSuccess("UPDATE_TRANSACTION", requestProperties);
                })
                .onErrorResume(e -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    LoggingUtil.setResponseTime(responseTime);
                    LoggingUtil.logError("UPDATE_TRANSACTION", "CONTROLLER_ERROR", 
                            e.getMessage(), e, requestProperties);
                    return Mono.error(e);
                });
    }
}

