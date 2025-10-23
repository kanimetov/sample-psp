package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.*;
import kg.demirbank.psp.service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Implementation of OperatorService for handling operator interactions
 */
@Service
@RequiredArgsConstructor
public class OperatorServiceImpl implements OperatorService {

    private final RestTemplate restTemplate;

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

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        headers.set("H-PSP-TOKEN", pspToken);
        headers.set("H-PSP-ID", pspId);
        headers.set("H-SIGNING-VERSION", signingVersion);
        // Note: hash is not included as it's typically calculated per request
        
        return headers;
    }

    @Override
    public CheckResponseDto check(CheckRequestDto request) {
        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/check", operatorBaseUrl, version);
        HttpEntity<CheckRequestDto> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<CheckResponseDto> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            CheckResponseDto.class
        );

        return response.getBody();
    }

    @Override
    public CreateResponseDto create(CreateRequestDto request) {
        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/create", operatorBaseUrl, version);
        HttpEntity<CreateRequestDto> entity = new HttpEntity<>(request, createHeaders());

        ResponseEntity<CreateResponseDto> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            CreateResponseDto.class
        );

        return response.getBody();
    }

    @Override
    public StatusDto execute(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID not specified");
        }

        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/execute/%s", operatorBaseUrl, version, transactionId);
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<StatusDto> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            StatusDto.class
        );

        return response.getBody();
    }

    @Override
    public void update(String transactionId, UpdateDto request) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID not specified");
        }

        String url = String.format("%s/psp/api/v1/payment/qr/%s/tx/update/%s", operatorBaseUrl, version, transactionId);
        HttpEntity<UpdateDto> entity = new HttpEntity<>(request, createHeaders());

        restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            Void.class
        );
    }
}