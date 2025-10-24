package kg.demirbank.psp.api;

import kg.demirbank.psp.dto.client.request.ClientCheckRequestDto;
import kg.demirbank.psp.dto.client.request.ClientMakePaymentRequestDto;
import kg.demirbank.psp.dto.client.response.ClientCheckResponseDto;
import kg.demirbank.psp.dto.client.response.ClientMakePaymentResponseDto;
import kg.demirbank.psp.service.ClientService;
import kg.demirbank.psp.util.LoggingUtil;
import kg.demirbank.psp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Client controller handles client requests for QR payment operations
 * Provides endpoints for checking QR details and making payments
 * 
 * Note: No signature verification required for client endpoints
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    
    private final ClientService clientService;
    private final ValidationUtil validationUtil;
    
    /**
     * Check QR payment details
     * Decodes QR URI and returns beneficiary information
     * 
     * @param version API version
     * @param request Client check request with QR URI
     * @return Check response with session ID, ELQR data, and beneficiary info
     */
    @PostMapping("/out/qr/{version}/check")
    public Mono<ResponseEntity<ClientCheckResponseDto>> checkQrPayment(
            @PathVariable String version,
            @RequestBody ClientCheckRequestDto request) {
        
        // Generate correlation ID for this request
        LoggingUtil.generateAndSetCorrelationId();
        
        // Validate DTO
        validationUtil.validateDto(request);
        
        // Process the request using business service
        return clientService.checkQrPayment(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.error(e)); // Let GlobalExceptionHandler handle it
    }
    
    /**
     * Make payment after checking QR details
     * Creates transaction and returns payment confirmation
     * 
     * @param version API version
     * @param request Client make payment request with QR URI and amount
     * @return Payment response with receipt ID and transaction details
     */
    @PostMapping("/out/qr/{version}/makePayment")
    public Mono<ResponseEntity<ClientMakePaymentResponseDto>> makePayment(
            @PathVariable String version,
            @RequestBody ClientMakePaymentRequestDto request) {
        
        // Generate correlation ID for this request
        LoggingUtil.generateAndSetCorrelationId();
        
        // Validate DTO
        validationUtil.validateDto(request);
        
        // Process the request using business service
        return clientService.makePayment(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.error(e)); // Let GlobalExceptionHandler handle it
    }
}
