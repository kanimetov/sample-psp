package kg.demirbank.psp.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * WebClient filter to add signatures to outgoing requests
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SignatureInterceptor implements ExchangeFilterFunction {

    private final SignatureService signatureService;
    private final KeyManagementService keyManagementService;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        // Skip signature generation if disabled
        if (!keyManagementService.isSignatureVerificationEnabled()) {
            log.debug("Signature generation disabled, skipping for URL: {}", request.url());
            return next.exchange(request);
        }

        // Handle different request types
        if (request.body() != null && request.method() == HttpMethod.POST) {
            // For POST requests with body - capture and sign the body
            return handlePostRequestWithBody(request, next);
        } else {
            // For GET/DELETE requests or requests without body - sign the URI
            return handleRequestWithUri(request, next);
        }
    }

    /**
     * Handle POST requests with body
     */
    private Mono<ClientResponse> handlePostRequestWithBody(ClientRequest request, ExchangeFunction next) {
        String uri = request.url().toString();
        
        // For now, we'll sign the URI for POST requests
        // Full body capture and signing requires a more complex WebClient implementation
        // This is a limitation of the current WebClient architecture
        log.debug("POST request detected, signing URI instead of body: {}", uri);
        
        String signature = signatureService.generateSignature(null, uri);
        log.debug("Generated signature for POST request URI: {}", uri);
        
        // Create new request with H-HASH header
        ClientRequest signedRequest = ClientRequest.from(request)
                .header("H-HASH", signature)
                .build();

        return next.exchange(signedRequest);
    }

    /**
     * Handle GET/DELETE requests or requests without body
     */
    private Mono<ClientResponse> handleRequestWithUri(ClientRequest request, ExchangeFunction next) {
        String uri = request.url().toString();
        String signature = signatureService.generateSignature(null, uri);
        
        log.debug("Generated signature for URI: {}", uri);
        
        // Create new request with H-HASH header
        ClientRequest signedRequest = ClientRequest.from(request)
                .header("H-HASH", signature)
                .build();

        return next.exchange(signedRequest);
    }
}
