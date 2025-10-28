package kg.demirbank.psp.service.impl;

import kg.demirbank.psp.dto.webhook.WebhookMessageDto;
import kg.demirbank.psp.dto.webhook.WebhookPayloadDto;
import kg.demirbank.psp.entity.MerchantWebhookEntity;
import kg.demirbank.psp.entity.OperationEntity;
import kg.demirbank.psp.enums.Status;
import kg.demirbank.psp.repository.MerchantWebhookRepository;
import kg.demirbank.psp.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of webhook service
 * Publishes webhook events to RabbitMQ for asynchronous delivery
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookServiceImpl implements WebhookService {
    
    private final MerchantWebhookRepository merchantWebhookRepository;
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${webhook.rabbitmq.exchange}")
    private String exchangeName;
    
    @Value("${webhook.rabbitmq.routing-key}")
    private String routingKey;
    
    @Override
    public void sendWebhookAsync(OperationEntity operation) {
        try {
            // Check if status is eligible for webhook notification
            if (!Status.isWebhookEligible(operation.getStatus())) {
                return;
            }
            
            // Only send webhooks for IN or OWN direction
            String direction = operation.getTransferDirection();
            if (!"IN".equals(direction) && !"OWN".equals(direction)) {
                return;
            }
            
            // Find merchant by serviceName (case-insensitive match with appId)
            Optional<MerchantWebhookEntity> merchantOpt = merchantWebhookRepository
                    .findByAppIdIgnoreCaseAndIsActive(operation.getServiceName(), true);
            
            if (merchantOpt.isEmpty()) {
                log.debug("No active merchant found for serviceName: {}", operation.getServiceName());
                return;
            }
            
            MerchantWebhookEntity merchant = merchantOpt.get();
            
            // Create webhook payload
            WebhookPayloadDto payload = WebhookPayloadDto.builder()
                    .status(operation.getStatus().getCode())
                    .qrTransactionId(operation.getQrTransactionId())
                    .build();
            
            // Create webhook message
            WebhookMessageDto message = WebhookMessageDto.builder()
                    .targetUrl(merchant.getTargetUrl())
                    .apiKeyName(merchant.getApiKeyName())
                    .apiKeyValue(merchant.getApiKeyValue())
                    .payload(payload)
                    .build();
            
            // Publish to RabbitMQ
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            
            log.info("Webhook event published for merchant: {}, appId: {}, status: {}", 
                    merchant.getMerchantName(), merchant.getAppId(), operation.getStatus());
                    
        } catch (Exception e) {
            log.error("Failed to publish webhook event for operation: {}", 
                    operation.getPspTransactionId(), e);
            // Don't throw exception - webhook failure shouldn't affect transaction
        }
    }
}

