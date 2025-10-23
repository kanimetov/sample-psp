package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.*;
import kg.demirbank.psp.service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Implementation of OperatorService for handling operator interactions
 */
@Service
@RequiredArgsConstructor
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
                .bodyToMono(CheckResponseDto.class);
    }

    @Override
    public Mono<CreateResponseDto> create(CreateRequestDto request) {
        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/create", operatorBaseUrl, version);
        
        WebClient webClient = webClientBuilder.baseUrl(operatorBaseUrl).build();
        
        return addHeaders(webClient.post().uri(url))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CreateResponseDto.class);
    }

    @Override
    public Mono<StatusDto> execute(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Transaction ID not specified"));
        }

        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/execute/%s", operatorBaseUrl, version, transactionId);
        
        WebClient webClient = webClientBuilder.baseUrl(operatorBaseUrl).build();
        
        return addHeaders(webClient.post().uri(url))
                .retrieve()
                .bodyToMono(StatusDto.class);
    }

    @Override
    public Mono<Void> update(String transactionId, UpdateDto request) {
        if (transactionId == null || transactionId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Transaction ID not specified"));
        }

        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/update/%s", operatorBaseUrl, version, transactionId);
        
        WebClient webClient = webClientBuilder.baseUrl(operatorBaseUrl).build();
        
        return addHeaders(webClient.post().uri(url))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class);
    }
}