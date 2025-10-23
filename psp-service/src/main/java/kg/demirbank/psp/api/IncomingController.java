package kg.demirbank.psp.api;

import kg.demirbank.psp.dto.*;
import kg.demirbank.psp.exception.BadRequestException;
import kg.demirbank.psp.exception.SignatureVerificationException;
import kg.demirbank.psp.security.SignatureService;
import kg.demirbank.psp.service.PspTransactionService;
import kg.demirbank.psp.util.JsonUtil;
import kg.demirbank.psp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final PspTransactionService pspTransactionService;
    
    @PostMapping("/in/qr/{version}/tx/check")
    public Mono<ResponseEntity<CheckResponseDto>> inboundCheck(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/check";
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            log.warn("Signature verification failed: {}", verificationResult.getErrorMessage());
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        // Deserialize JSON after successful signature verification
        CheckRequestDto body = jsonUtil.fromJson(rawBody, CheckRequestDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        log.debug("Successfully deserialized and validated CheckRequestDto: {}", body);
        
        // Process the request using business service
        return pspTransactionService.checkTransaction(body)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error processing check transaction", e);
                    return Mono.error(e); // Let GlobalExceptionHandler handle it
                });
    }

    @PostMapping("/in/qr/{version}/tx/create")
    public Mono<ResponseEntity<CreateResponseDto>> inboundCreate(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/create";
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            log.warn("Signature verification failed: {}", verificationResult.getErrorMessage());
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        // Deserialize JSON after successful signature verification
        CreateRequestDto body = jsonUtil.fromJson(rawBody, CreateRequestDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        log.debug("Successfully deserialized and validated CreateRequestDto: {}", body);
        
        // Process the request using business service
        return pspTransactionService.createTransaction(body)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error processing create transaction", e);
                    return Mono.error(e); // Let GlobalExceptionHandler handle it
                });
    }

    @PostMapping("/in/qr/{version}/tx/execute/{transactionId}")
    public Mono<ResponseEntity<StatusDto>> inboundExecute(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash) {
        
        // Verify signature for URI (no body for this request)
        String uri = "/in/qr/" + version + "/tx/execute/" + transactionId;
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(null, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            log.warn("Signature verification failed: {}", verificationResult.getErrorMessage());
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        // Process the request using business service
        return pspTransactionService.executeTransaction(transactionId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error processing execute transaction", e);
                    return Mono.error(e); // Let GlobalExceptionHandler handle it
                });
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
        
        // Verify signature first
        byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String uri = "/in/qr/" + version + "/tx/update/" + transactionId;
        
        SignatureService.SignatureVerificationResult verificationResult = 
                signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
        
        if (!verificationResult.isSuccess()) {
            log.warn("Signature verification failed: {}", verificationResult.getErrorMessage());
            return Mono.error(new SignatureVerificationException(verificationResult.getErrorMessage()));
        }
        
        // Deserialize JSON after successful signature verification
        UpdateDto body = jsonUtil.fromJson(rawBody, UpdateDto.class);
        
        // Validate DTO using utility method
        validationUtil.validateDto(body);
        
        log.debug("Successfully deserialized and validated UpdateDto: {}", body);
        
        // Process the request using business service - ACK response (200 OK empty body)
        return pspTransactionService.updateTransaction(transactionId, body)
                .then(Mono.just(ResponseEntity.ok("OK")))
                .onErrorResume(e -> {
                    log.error("Error processing update transaction", e);
                    return Mono.error(e);
                });
    }
}

