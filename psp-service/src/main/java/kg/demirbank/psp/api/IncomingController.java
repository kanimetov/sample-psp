package kg.demirbank.psp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.demirbank.psp.dto.*;
import kg.demirbank.psp.security.SignatureService;
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
    private final ObjectMapper objectMapper;
    
    @PostMapping("/in/qr/{version}/tx/check")
    public Mono<ResponseEntity<?>> inboundCheck(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        try {
            // Verify signature first
            byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String uri = "/in/qr/" + version + "/tx/check";
            
            SignatureService.SignatureVerificationResult verificationResult = 
                    signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
            
            if (!verificationResult.isSuccess()) {
                log.warn("Signature verification failed: {}", verificationResult.getErrorMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(verificationResult.getErrorMessage()));
            }
            
            // Deserialize JSON after successful signature verification
            CheckRequestDto body = objectMapper.readValue(rawBody, CheckRequestDto.class);
            log.debug("Successfully deserialized CheckRequestDto: {}", body);
            
            // Process the request
            CheckResponseDto resp = new CheckResponseDto();
            resp.setBeneficiaryName("c***e A***o");
            resp.setTransactionType(null); // TODO: set proper value
            return Mono.just(ResponseEntity.ok(resp));
            
        } catch (Exception e) {
            log.error("Error processing inbound check request", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error"));
        }
    }

    @PostMapping("/in/qr/{version}/tx/create")
    public Mono<ResponseEntity<?>> inboundCreate(
            @PathVariable String version,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        try {
            // Verify signature first
            byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String uri = "/in/qr/" + version + "/tx/create";
            
            SignatureService.SignatureVerificationResult verificationResult = 
                    signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
            
            if (!verificationResult.isSuccess()) {
                log.warn("Signature verification failed: {}", verificationResult.getErrorMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(verificationResult.getErrorMessage()));
            }
            
            // Deserialize JSON after successful signature verification
            CreateRequestDto body = objectMapper.readValue(rawBody, CreateRequestDto.class);
            
            // Process the request
            CreateResponseDto resp = new CreateResponseDto();
            resp.setTransactionId("fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7");
            resp.setStatus(null); // TODO: set proper status
            resp.setTransactionType(body.getTransactionType());
            resp.setAmount(body.getAmount());
            resp.setBeneficiaryName("Sample Beneficiary");
            resp.setCustomerType(1); // TODO: set proper value
            resp.setReceiptId(body.getReceiptId());
            resp.setCreatedDate("2022-11-01T12:00:00Z");
            resp.setExecutedDate("");
            return Mono.just(ResponseEntity.ok(resp));
            
        } catch (Exception e) {
            log.error("Error processing inbound create request", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error"));
        }
    }

    @PostMapping("/in/qr/{version}/tx/execute/{transactionId}")
    public Mono<ResponseEntity<?>> inboundExecute(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash) {
        
        try {
            // Verify signature for URI (no body for this request)
            String uri = "/in/qr/" + version + "/tx/execute/" + transactionId;
            
            SignatureService.SignatureVerificationResult verificationResult = 
                    signatureService.verifySignatureWithDetails(null, hash, uri);
            
            if (!verificationResult.isSuccess()) {
                log.warn("Signature verification failed: {}", verificationResult.getErrorMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(verificationResult.getErrorMessage()));
            }
            
            // Process the request
            StatusDto resp = new StatusDto();
            resp.setTransactionId(transactionId);
            resp.setStatus(null); // TODO: set proper Status enum value
            resp.setTransactionType(null); // TODO: set proper CustomerType enum value
            resp.setAmount(40000L);
            resp.setBeneficiaryName("c***e A***o");
            resp.setCustomerType("1");
            resp.setReceiptId("7218199");
            resp.setCreatedDate("2022-11-01T12:00:00Z");
            resp.setExecutedDate("2022-11-01T12:02:00Z");
            return Mono.just(ResponseEntity.ok(resp));
            
        } catch (Exception e) {
            log.error("Error processing inbound execute request", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error"));
        }
    }

    @PostMapping("/in/qr/{version}/tx/update/{transactionId}")
    public Mono<ResponseEntity<?>> inboundUpdate(
            @PathVariable String version,
            @PathVariable String transactionId,
            @RequestHeader(name = "H-HASH", required = false) String hash,
            @RequestBody String rawBody) {
        
        try {
            if (version == null || version.isBlank()) {
                return Mono.just(ResponseEntity.badRequest().body("QR version not specified"));
            }
            if (transactionId == null || transactionId.isBlank()) {
                return Mono.just(ResponseEntity.badRequest().body("Transaction ID not specified"));
            }
            
            // Verify signature first
            byte[] bodyBytes = rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String uri = "/in/qr/" + version + "/tx/update/" + transactionId;
            
            SignatureService.SignatureVerificationResult verificationResult = 
                    signatureService.verifySignatureWithDetails(bodyBytes, hash, uri);
            
            if (!verificationResult.isSuccess()) {
                log.warn("Signature verification failed: {}", verificationResult.getErrorMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(verificationResult.getErrorMessage()));
            }
            
            // Deserialize JSON after successful signature verification
            UpdateDto body = objectMapper.readValue(rawBody, UpdateDto.class);
            log.debug("Successfully deserialized UpdateDto: {}", body);
            
            // Process the request - ACK response (200 OK empty body)
            return Mono.just(ResponseEntity.ok().build());
            
        } catch (Exception e) {
            log.error("Error processing inbound update request", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error"));
        }
    }
}

