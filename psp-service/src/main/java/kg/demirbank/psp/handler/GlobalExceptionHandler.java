package kg.demirbank.psp.handler;

import kg.demirbank.psp.dto.common.ErrorResponseDto;
import kg.demirbank.psp.exception.PspException;
import kg.demirbank.psp.exception.validation.*;
import kg.demirbank.psp.exception.network.*;
import kg.demirbank.psp.exception.security.*;
import kg.demirbank.psp.exception.business.*;
import kg.demirbank.psp.util.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for PSP Service
 * Handles all exceptions and returns standardized error responses
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    

    /**
     * Handle all PSP custom exceptions
     */
    @ExceptionHandler(PspException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handlePspException(
            PspException ex,
            ServerWebExchange exchange) {
        
        log.error("PSP Exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(ex.getStatus())
                .body(errorResponse));
    }

    /**
     * Handle Bad Request Exception (400)
     */
    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleBadRequestException(
            BadRequestException ex,
            ServerWebExchange exchange) {
        
        LoggingUtil.logError("GLOBAL_EXCEPTION_HANDLER", null, "BAD_REQUEST", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(400)
                .message("The request is invalid or malformed. The server cannot process it")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse));
    }

    /**
     * Handle Resource Not Found Exception (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            ServerWebExchange exchange) {
        
        log.error("Resource Not Found: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(404)
                .message("The requested resource does not exist")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse));
    }

    /**
     * Handle Unprocessable Entity Exception (422)
     */
    @ExceptionHandler(UnprocessableEntityException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleUnprocessableEntityException(
            UnprocessableEntityException ex,
            ServerWebExchange exchange) {
        
        log.error("Unprocessable Entity: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(422)
                .message("The request is well-formed but contains invalid data that cannot be processed")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorResponse));
    }

    /**
     * Handle Recipient Data Incorrect Exception (452)
     */
    @ExceptionHandler(RecipientDataIncorrectException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleRecipientDataIncorrectException(
            RecipientDataIncorrectException ex,
            ServerWebExchange exchange) {
        
        log.error("Recipient Data Incorrect: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(452)
                .message("The recipient's data is incorrect")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.valueOf(452))
                .body(errorResponse));
    }

    /**
     * Handle Access Denied Exception (453)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleAccessDeniedException(
            AccessDeniedException ex,
            ServerWebExchange exchange) {
        
        log.error("Access Denied: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(453)
                .message("Access to the system is denied")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.valueOf(453))
                .body(errorResponse));
    }

    /**
     * Handle Incorrect Request Data Exception (454)
     */
    @ExceptionHandler(IncorrectRequestDataException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleIncorrectRequestDataException(
            IncorrectRequestDataException ex,
            ServerWebExchange exchange) {
        
        log.error("Incorrect Request Data: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(454)
                .message("Incorrect data in the request")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.valueOf(454))
                .body(errorResponse));
    }

    /**
     * Handle Min Amount Not Valid Exception (455)
     */
    @ExceptionHandler(MinAmountNotValidException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleMinAmountNotValidException(
            MinAmountNotValidException ex,
            ServerWebExchange exchange) {
        
        LoggingUtil.logError("GLOBAL_EXCEPTION_HANDLER", null, "MIN_AMOUNT_INVALID", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(455)
                .message("Min amount not valid")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.valueOf(455))
                .body(errorResponse));
    }

    /**
     * Handle Max Amount Not Valid Exception (456)
     */
    @ExceptionHandler(MaxAmountNotValidException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleMaxAmountNotValidException(
            MaxAmountNotValidException ex,
            ServerWebExchange exchange) {
        
        log.error("Max Amount Not Valid: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(456)
                .message("Max amount not valid")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.valueOf(456))
                .body(errorResponse));
    }

    /**
     * Handle System Error Exception (500)
     */
    @ExceptionHandler(SystemErrorException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleSystemErrorException(
            SystemErrorException ex,
            ServerWebExchange exchange) {
        
        LoggingUtil.logError("GLOBAL_EXCEPTION_HANDLER", null, "SYSTEM_ERROR", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(500)
                .message("System error")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse));
    }

    /**
     * Handle Supplier Not Available Exception (523)
     */
    @ExceptionHandler(SupplierNotAvailableException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleSupplierNotAvailableException(
            SupplierNotAvailableException ex,
            ServerWebExchange exchange) {
        
        log.error("Supplier Not Available: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(523)
                .message("Supplier not available")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.valueOf(523))
                .body(errorResponse));
    }

    /**
     * Handle External Server Not Available Exception (524)
     */
    @ExceptionHandler(ExternalServerNotAvailableException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleExternalServerNotAvailableException(
            ExternalServerNotAvailableException ex,
            ServerWebExchange exchange) {
        
        log.error("External Server Not Available: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(524)
                .message("External server is not available")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.valueOf(524))
                .body(errorResponse));
    }

    /**
     * Handle Network Timeout Exception (504)
     */
    @ExceptionHandler(NetworkTimeoutException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleNetworkTimeoutException(
            NetworkTimeoutException ex,
            ServerWebExchange exchange) {
        
        log.error("Network Timeout: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(504)
                .message("Request timeout")
                .details("The request to the external service timed out. Please try again later.")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(errorResponse));
    }

    /**
     * Handle Network Connection Exception (503)
     */
    @ExceptionHandler(NetworkConnectionException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleNetworkConnectionException(
            NetworkConnectionException ex,
            ServerWebExchange exchange) {
        
        log.error("Network Connection Error: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(503)
                .message("Service temporarily unavailable")
                .details("Unable to connect to the external service. Please try again later.")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse));
    }

    /**
     * Handle Generic Network Exception (502)
     */
    @ExceptionHandler(NetworkException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleNetworkException(
            NetworkException ex,
            ServerWebExchange exchange) {
        
        log.error("Network Error: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(502)
                .message("Network error")
                .details("A network error occurred while communicating with external service.")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(errorResponse));
    }

    /**
     * Handle WebClient Response Exceptions (from operator calls)
     */
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleWebClientResponseException(
            WebClientResponseException ex,
            ServerWebExchange exchange) {
        
        log.error("WebClient Error: Status={}, Body={}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        
        // Try to map operator error codes to our custom exceptions
        int statusCode = ex.getStatusCode().value();
        String message = ex.getResponseBodyAsString();
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(statusCode)
                .message("Error from operator")
                .details(message)
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(ex.getStatusCode())
                .body(errorResponse));
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleValidationException(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {
        
        log.error("Validation Error: {}", ex.getMessage(), ex);
        
        String details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(400)
                .message("Validation failed")
                .details(details)
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse));
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            ServerWebExchange exchange) {
        
        log.error("Illegal Argument: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(400)
                .message("Invalid argument")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse));
    }

    /**
     * Handle Validation Exception (400)
     */
    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleValidationException(
            ValidationException ex,
            ServerWebExchange exchange) {
        
        log.warn("DTO Validation Failed: {}", ex.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(400)
                .message("Request validation failed")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse));
    }

    /**
     * Handle Signature Verification Exception (403)
     */
    @ExceptionHandler(SignatureVerificationException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleSignatureVerificationException(
            SignatureVerificationException ex,
            ServerWebExchange exchange) {
        
        LoggingUtil.logError("GLOBAL_EXCEPTION_HANDLER", null, "SIGNATURE_VERIFICATION_FAILED", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(403)
                .message("Signature verification failed")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse));
    }

    /**
     * Handle all other unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {
        
        LoggingUtil.logError("GLOBAL_EXCEPTION_HANDLER", null, "UNEXPECTED_ERROR", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .code(500)
                .message("System error")
                .details("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse));
    }
}

