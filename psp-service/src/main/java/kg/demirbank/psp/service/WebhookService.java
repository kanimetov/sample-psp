package kg.demirbank.psp.service;

import kg.demirbank.psp.entity.OperationEntity;

/**
 * Service for managing webhook notifications to merchants
 * Handles webhook event creation and publishing to RabbitMQ
 */
public interface WebhookService {
    
    /**
     * Send webhook notification asynchronously to registered merchant
     * Checks if merchant exists with matching serviceName and sends webhook for eligible statuses
     * 
     * @param operation operation entity containing transaction details
     */
    void sendWebhookAsync(OperationEntity operation);
}

