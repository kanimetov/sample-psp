package kg.demirbank.psp.dto.webhook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Webhook message DTO for RabbitMQ
 * Contains all information needed to deliver webhook to merchant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookMessageDto {
    
    /**
     * Target URL where webhook should be delivered
     */
    private String targetUrl;
    
    /**
     * API key name for HTTP header authentication
     */
    private String apiKeyName;
    
    /**
     * API key value for HTTP header authentication
     */
    private String apiKeyValue;
    
    /**
     * Webhook payload containing transaction status
     */
    private WebhookPayloadDto payload;
}

