package kg.demirbank.psp.dto.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Webhook payload DTO containing transaction status information
 * This payload is sent to merchant's target URL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPayloadDto {
    
    /**
     * Transaction status code
     * 10 - CREATED/PENDING
     * 30 - ERROR
     * 40 - CANCELED
     * 50 - SUCCESS
     */
    @JsonProperty("status")
    private Integer status;
    
    /**
     * QR transaction ID from the operation
     */
    @JsonProperty("qrTransactionId")
    private String qrTransactionId;
}

