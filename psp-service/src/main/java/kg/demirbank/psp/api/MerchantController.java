package kg.demirbank.psp.api;

import kg.demirbank.psp.dto.merchant.request.MerchantCheckRequestDto;
import kg.demirbank.psp.dto.merchant.request.MerchantMakePaymentRequestDto;
import kg.demirbank.psp.dto.merchant.response.MerchantCheckResponseDto;
import kg.demirbank.psp.dto.merchant.response.MerchantMakePaymentResponseDto;
import kg.demirbank.psp.service.MerchantService;
import kg.demirbank.psp.util.LoggingUtil;
import kg.demirbank.psp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Merchant controller handles merchant requests for QR payment operations
 * Provides endpoints for checking QR details and making payments
 * 
 * Note: No signature verification required for merchant endpoints
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class MerchantController {
    
    private final MerchantService merchantService;
    private final ValidationUtil validationUtil;
    
    /**
     * Check QR payment details
     * Decodes QR URI and returns beneficiary information
     * 
     * @param version API version
     * @param request Merchant check request with QR URI
     * @return Check response with session ID, ELQR data, and beneficiary info
     */
    @PostMapping("/merchant/qr/{version}/check")
    public Mono<ResponseEntity<MerchantCheckResponseDto>> checkQrPayment(
            @PathVariable String version,
            @RequestBody MerchantCheckRequestDto request) {
        
        // Generate correlation ID for this request
        LoggingUtil.generateAndSetCorrelationId();
        
        // Validate DTO
        validationUtil.validateDto(request);
        
        // Process the request using business service
        return merchantService.checkQrPayment(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.error(e)); // Let GlobalExceptionHandler handle it
    }
    
    /**
     * Make payment after checking QR details
     * Creates transaction and returns payment confirmation
     * 
     * @param version API version
     * @param request Merchant make payment request with QR URI and amount
     * @return Payment response with receipt ID and transaction details
     */
    @PostMapping("/merchant/qr/{version}/makePayment")
    public Mono<ResponseEntity<MerchantMakePaymentResponseDto>> makePayment(
            @PathVariable String version,
            @RequestBody MerchantMakePaymentRequestDto request) {
        
        // Generate correlation ID for this request
        LoggingUtil.generateAndSetCorrelationId();
        
        // Validate DTO
        validationUtil.validateDto(request);
        
        // Process the request using business service
        return merchantService.makePayment(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.error(e)); // Let GlobalExceptionHandler handle it
    }
}
