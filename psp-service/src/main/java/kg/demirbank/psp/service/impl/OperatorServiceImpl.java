package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.*;
import kg.demirbank.psp.exception.*;
import kg.demirbank.psp.service.OperatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Implementation of OperatorService for handling operator interactions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorServiceImpl implements OperatorService {

    private final WebClient.Builder webClientBuilder;

    @Value("${operator.base-url}")
    private String operatorBaseUrl;

    @Value("${operator.version}")
    private String version;

    @Value("${operator.signing-version}")
    private String signingVersion;

    @Value("${operator.psp.token}")
    private String pspToken;

    @Value("${operator.psp.id}")
    private String pspId;

    private WebClient.RequestBodySpec addHeaders(WebClient.RequestBodySpec spec) {
        return spec
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("H-PSP-TOKEN", pspToken)
                .header("H-PSP-ID", pspId)
                .header("H-SIGNING-VERSION", signingVersion);
        // Note: hash is not included as it's typically calculated per request
    }

    @Override
    public Mono<CheckResponseDto> check(CheckRequestDto request) {
        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/check", operatorBaseUrl, version);
        
        WebClient webClient = webClientBuilder.baseUrl(operatorBaseUrl).build();
        
        return addHeaders(webClient.post().uri(url))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CheckResponseDto.class)
                .onErrorMap(this::mapOperatorError);
    }

    @Override
    public Mono<CreateResponseDto> create(CreateRequestDto request) {
        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/create", operatorBaseUrl, version);
        
        WebClient webClient = webClientBuilder.baseUrl(operatorBaseUrl).build();
        
        return addHeaders(webClient.post().uri(url))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CreateResponseDto.class)
                .onErrorMap(this::mapOperatorError);
    }

    @Override
    public Mono<StatusDto> execute(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            return Mono.error(new BadRequestException("Transaction ID not specified"));
        }

        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/execute/%s", operatorBaseUrl, version, transactionId);
        
        WebClient webClient = webClientBuilder.baseUrl(operatorBaseUrl).build();
        
        return addHeaders(webClient.post().uri(url))
                .retrieve()
                .bodyToMono(StatusDto.class)
                .onErrorMap(this::mapOperatorError);
    }

    @Override
    public Mono<Void> update(String transactionId, UpdateDto request) {
        if (transactionId == null || transactionId.isBlank()) {
            return Mono.error(new BadRequestException("Transaction ID not specified"));
        }

        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/update/%s", operatorBaseUrl, version, transactionId);
        
        WebClient webClient = webClientBuilder.baseUrl(operatorBaseUrl).build();
        
        return addHeaders(webClient.post().uri(url))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorMap(this::mapOperatorError);
    }

    /**
     * Map operator errors to our custom exceptions
     */
    private Throwable mapOperatorError(Throwable error) {
        if (error instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) error;
            int statusCode = ex.getStatusCode().value();
            String responseBody = ex.getResponseBodyAsString();
            
            log.error("Operator error: status={}, body={}", statusCode, responseBody);
            
            // Map operator error codes to our custom exceptions
            switch (statusCode) {
                case 400:
                    return new BadRequestException("The request is invalid or malformed. The server cannot process it");
                case 404:
                    return new ResourceNotFoundException("The requested resource does not exist");
                case 422:
                    return new UnprocessableEntityException("The request is well-formed but contains invalid data that cannot be processed");
                case 452:
                    return new RecipientDataIncorrectException("The recipient's data is incorrect");
                case 453:
                    return new AccessDeniedException("Access to the system is denied");
                case 454:
                    return new IncorrectRequestDataException("Incorrect data in the request");
                case 455:
                    return new MinAmountNotValidException("Min amount not valid");
                case 456:
                    return new MaxAmountNotValidException("Max amount not valid");
                case 500:
                    return new SystemErrorException("System error");
                case 523:
                    return new SupplierNotAvailableException("Supplier not available");
                case 524:
                    return new ExternalServerNotAvailableException("External server is not available");
                default:
                    return new SystemErrorException("Unexpected error from operator: " + statusCode);
            }
        }
        
        // For non-WebClient errors, wrap in SystemErrorException
        log.error("Unexpected error in operator service", error);
        return new SystemErrorException("Unexpected system error: " + error.getMessage(), error);
    }
}