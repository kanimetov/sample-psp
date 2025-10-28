## Webhook Notifications

### Overview

PSP service sends webhook notifications to registered merchants when payment transaction status changes. Webhooks are delivered asynchronously via RabbitMQ for reliability and performance.

### Registration

Merchants must register their webhook configuration in the `merchant_webhooks` table:

```sql
INSERT INTO merchant_webhooks (
    merchant_name,
    app_id,
    api_key_name,
    api_key_value,
    target_url,
    is_active,
    created_by
) VALUES (
    'Merchant Name',
    'SERVICE_NAME',  -- Must match serviceName from QR code (case-insensitive)
    'Authorization',  -- HTTP header name
    'Bearer secret-key',  -- HTTP header value
    'https://merchant.example.com/webhooks',  -- Target URL
    1,  -- Active
    'admin'
);
```

### Matching Logic

- Merchant webhook is found by matching `app_id` (case-insensitive) with `service_name` from operations
- Only active merchants (`is_active = 1`) receive webhook notifications
- Webhooks are sent for IN and OWN direction transactions only

### Webhook Triggers

Webhooks are sent for the following status changes:

1. **CREATED (10)** - After successful CREATE operation (IN or OWN direction)
   - Sent immediately after operation is saved to database
   
2. **ERROR (30)** - Transaction failed with error
3. **CANCELED (40)** - Transaction was canceled
4. **SUCCESS (50)** - Transaction completed successfully

**Note:** IN_PROCESS (20) status does not trigger webhooks.

### Payload Format

Webhook payload sent to merchant's target URL:

```json
{
  "status": 10,
  "qrTransactionId": "TXN123456"
}
```

**Status Codes:**
- `10` - CREATED (PENDING)
- `30` - ERROR
- `40` - CANCELED
- `50` - SUCCESS

### HTTP Request Format

**Method:** POST

**Headers:**
```
Content-Type: application/json
{api_key_name}: {api_key_value}
```

Example:
```
Content-Type: application/json
Authorization: Bearer secret-key
```

**Body:**
JSON payload with `status` and `qrTransactionId` fields.

### Expected Response

Merchant should respond with HTTP status codes:
- `200-299` - Success (webhook processed successfully)
- `300-499` - Client error (message moved to DLQ)
- `500-599` - Server error (RabbitMQ will retry)

### Delivery Guarantees

- **At-least-once delivery** - RabbitMQ guarantees message delivery
- **Retry mechanism** - Up to 3 retry attempts with exponential backoff (30s, 2m, 10m)
- **Dead Letter Queue** - Failed messages after max retries are moved to DLQ
- **Asynchronous delivery** - Webhook delivery does not block transaction processing
- **Transactional integrity** - Transaction commits successfully even if webhook publishing fails

### Configuration

Webhook system can be configured in `application.yml`:

```yaml
webhook:
  rabbitmq:
    exchange: webhook.merchant.notify
    queue: webhook.merchant.notify.queue
    dlq: webhook.merchant.notify.dlq
    routing-key: merchant.webhook
  http:
    timeout-ms: 5000  # HTTP timeout for webhook delivery
    max-retries: 3    # Maximum retry attempts
  enabled: true       # Enable/disable webhook system
```

### Monitoring

Key metrics to monitor:
- Webhook delivery success rate
- Webhook delivery latency (p95, p99)
- DLQ message count
- Retry attempt distribution
- Failed webhook destinations

### Security Considerations

1. **API Key Storage** - API keys are stored in database (consider encryption for production)
2. **HTTPS Only** - Target URLs should use HTTPS for secure delivery
3. **IP Whitelisting** - Merchants should whitelist PSP server IPs
4. **Signature Verification** - Consider adding HMAC signature in headers for verification
5. **Rate Limiting** - Implement rate limiting on merchant endpoints

### Best Practices

1. **Idempotency** - Webhook handler should be idempotent (handle duplicate deliveries)
2. **Response Time** - Respond within 5 seconds to avoid RabbitMQ timeout
3. **Error Handling** - Return appropriate HTTP status codes for retries
4. **Logging** - Log all incoming webhooks for audit trail
5. **Testing** - Use webhook testing tools to verify delivery

### Troubleshooting

**Webhook not received:**
- Verify `app_id` matches `service_name` (case-insensitive)
- Check `is_active = 1` in merchant_webhooks table
- Verify RabbitMQ is running and configured correctly
- Check application logs for webhook publishing errors

**Webhook in DLQ:**
- Check merchant endpoint is accessible
- Verify API key authentication
- Ensure target URL accepts POST requests
- Review HTTP status codes returned by merchant

**Delayed delivery:**
- Check RabbitMQ consumer health
- Monitor queue depth
- Review consumer processing time
- Check for network issues

