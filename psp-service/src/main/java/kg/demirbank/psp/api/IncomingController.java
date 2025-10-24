package kg.demirbank.psp.api;

import kg.demirbank.psp.dto.incoming.request.IncomingCheckRequestDto;
import kg.demirbank.psp.dto.incoming.request.IncomingCreateRequestDto;
import kg.demirbank.psp.dto.common.UpdateDto;
import kg.demirbank.psp.dto.incoming.response.IncomingCheckResponseDto;
import kg.demirbank.psp.dto.incoming.response.IncomingTransactionResponseDto;
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
    public Mono<ResponseEntity<IncomingCheckResponseDto>> inboundCheck(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        // Generate correlation ID for this request
        LoggingUtil.generateAndSetCorrelationId();
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/check";
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        // Deserialize JSON after successful signature verification
        IncomingCheckRequestDto body = jsonUtil.fromJson(rawBody, IncomingCheckRequestDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        // Process the request using business service
        return incomingService.checkTransaction(body)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.error(e)); // Let GlobalExceptionHandler handle it
    }

    @PostMapping("/in/qr/{version}/tx/create")
    public Mono<ResponseEntity<IncomingTransactionResponseDto>> inboundCreate(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        // Generate correlation ID for this request
        LoggingUtil.generateAndSetCorrelationId();
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/create";
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        // Deserialize JSON after successful signature verification
        IncomingCreateRequestDto body = jsonUtil.fromJson(rawBody, IncomingCreateRequestDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        // Process the request using business service
        return incomingService.createTransaction(body)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.error(e)); // Let GlobalExceptionHandler handle it
    }

    @PostMapping("/in/qr/{version}/tx/execute/{transactionId}")
    public Mono<ResponseEntity<IncomingTransactionResponseDto>> inboundExecute(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash) {
        
        // Validate transaction ID
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new BadRequestException("Transaction ID is required");
        }
        
        // Generate correlation ID for this request
        LoggingUtil.generateAndSetCorrelationId();
        
        // Verify signature for URI (no body for this request)
        String uri = "/in/qr/" + version + "/tx/execute/" + transactionId;
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(null, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        // Process the request using business service
        return incomingService.executeTransaction(transactionId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.error(e)); // Let GlobalExceptionHandler handle it
    }

    @PostMapping("/in/qr/{version}/tx/update/{transactionId}")
    public Mono<ResponseEntity<String>> inboundUpdate(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        if (version == null || version.isBlank()) {
            return Mono.error(new BadRequestException("QR version not specified"));
        }
        if (transactionId == null || transactionId.isBlank()) {
            return Mono.error(new BadRequestException("Transaction ID not specified"));
        }
        
        // Generate correlation ID for this request
        LoggingUtil.generateAndSetCorrelationId();
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/update/" + transactionId;
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        // Deserialize JSON after successful signature verification
        UpdateDto body = jsonUtil.fromJson(rawBody, UpdateDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        // Process the request using business service - ACK response (200 OK empty body)
        return incomingService.updateTransaction(transactionId, body)
                .then(Mono.just(ResponseEntity.ok("OK")))
                .onErrorResume(e -> Mono.error(e));
    }
}