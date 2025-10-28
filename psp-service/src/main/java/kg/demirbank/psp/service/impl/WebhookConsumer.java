package kg.demirbank.psp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.demirbank.psp.dto.webhook.WebhookMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Webhook consumer for processing webhook messages from RabbitMQ
 * Sends HTTP POST requests to merchant target URLs
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookConsumer {
    
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    
    @Value("${webhook.http.timeout-ms:5000}")
    private int httpTimeoutMs;
    
    @RabbitListener(queues = "${webhook.rabbitmq.queue}")
    public void processWebhook(WebhookMessageDto message) {
        try {
            log.info("Processing webhook for target URL: {}", message.getTargetUrl());
            
            // Create headers with API key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set(message.getApiKeyName(), message.getApiKeyValue());
            
            // Create request entity with payload
            String payloadJson = objectMapper.writeValueAsString(message.getPayload());
            HttpEntity<String> requestEntity = new HttpEntity<>(payloadJson, headers);
            
            // Send HTTP POST request
            ResponseEntity<String> response = restTemplate.exchange(
                    message.getTargetUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            
            // Check response status
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Webhook delivered successfully to: {}", message.getTargetUrl());
            } else {
                log.warn("Webhook delivery returned non-2xx status: {} for URL: {}", 
                        response.getStatusCode(), message.getTargetUrl());
            }
            
        } catch (ResourceAccessException e) {
            log.error("Timeout or connection error delivering webhook to: {}", 
                    message.getTargetUrl(), e);
            // Don't throw - let RabbitMQ retry logic handle it
        } catch (RestClientException e) {
            log.error("HTTP client error delivering webhook to: {}", 
                    message.getTargetUrl(), e);
            // Don't throw - let RabbitMQ retry logic handle it
        } catch (Exception e) {
            log.error("Unexpected error processing webhook for URL: {}", 
                    message.getTargetUrl(), e);
            // Don't throw - let RabbitMQ retry logic handle it
        }
    }
}

