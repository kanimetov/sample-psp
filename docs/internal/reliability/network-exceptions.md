# Network Exception Handling

## Overview

This document describes the network exception handling implementation in the PSP Service. The system now properly handles various network-related errors including timeouts, connection failures, and SSL/TLS issues.

## Network Exception Types

### 1. NetworkTimeoutException (504 Gateway Timeout)

Thrown when a request to the operator service times out.

**Triggers:**
- Connection timeout exceeded
- Read timeout exceeded
- Write timeout exceeded
- Response timeout exceeded

**HTTP Status:** 504 Gateway Timeout

**Client Response:**
```json
{
  "code": 504,
  "message": "Request timeout",
  "details": "The request to the external service timed out. Please try again later.",
  "timestamp": "2025-10-23T10:30:00",
  "path": "/api/v1/..."
}
```

### 2. NetworkConnectionException (503 Service Unavailable)

Thrown when the service cannot establish a connection to the operator service.

**Triggers:**
- Connection refused
- Connection reset
- No route to host
- Port unreachable
- Broken pipe

**HTTP Status:** 503 Service Unavailable

**Client Response:**
```json
{
  "code": 503,
  "message": "Service temporarily unavailable",
  "details": "Unable to connect to the external service. Please try again later.",
  "timestamp": "2025-10-23T10:30:00",
  "path": "/api/v1/..."
}
```

### 3. NetworkException (502 Bad Gateway)

Generic network exception for other network-related errors.

**Triggers:**
- SSL/TLS handshake failures
- Certificate validation errors
- Unknown host exceptions
- Socket exceptions
- General I/O errors

**HTTP Status:** 502 Bad Gateway

**Client Response:**
```json
{
  "code": 502,
  "message": "Network error",
  "details": "A network error occurred while communicating with external service.",
  "timestamp": "2025-10-23T10:30:00",
  "path": "/api/v1/..."
}
```

## Timeout Configuration

Timeouts are configured in `application.yml`:

```yaml
operator:
  timeout:
    connection: 5000      # Connection timeout in milliseconds (5 seconds)
    read: 30000          # Read timeout in milliseconds (30 seconds)
    write: 30000         # Write timeout in milliseconds (30 seconds)
    response: 60000      # Response timeout in milliseconds (60 seconds)
```

### Timeout Types Explained

1. **Connection Timeout (5s):** Maximum time to establish a TCP connection
2. **Read Timeout (30s):** Maximum time waiting for data after connection is established
3. **Write Timeout (30s):** Maximum time to send data after connection is established
4. **Response Timeout (60s):** Maximum time for the entire request-response cycle

### Recommended Timeout Values

| Environment | Connection | Read  | Write | Response |
|-------------|-----------|-------|-------|----------|
| Development | 5000ms    | 30000ms | 30000ms | 60000ms  |
| Testing     | 5000ms    | 30000ms | 30000ms | 60000ms  |
| Production  | 3000ms    | 20000ms | 20000ms | 45000ms  |

## Implementation Details

### WebClient Configuration

The `RestConfig` class configures the Netty HTTP client with timeout settings:

```java
HttpClient httpClient = HttpClient.create()
    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
    .responseTimeout(Duration.ofMillis(responseTimeout))
    .doOnConnected(conn -> conn
        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
        .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
    );
```

### Error Detection

The `OperatorServiceImpl` class includes helper methods to detect various types of network errors:

- `isTimeoutException()`: Detects timeout-related errors
- `isConnectionException()`: Detects connection failures
- `isSslException()`: Detects SSL/TLS errors
- `isNetworkException()`: Detects general network errors

These methods traverse the exception chain to identify network issues at any level.

### Error Mapping

The `mapOperatorError()` method in `OperatorServiceImpl` maps low-level network exceptions to appropriate high-level exceptions:

```
TimeoutException → NetworkTimeoutException (504)
ConnectException → NetworkConnectionException (503)
SSLException → NetworkException (502)
IOException → NetworkException (502)
```

## Retry Strategy

For network exceptions, clients should implement retry logic with exponential backoff:

1. **Retryable Errors:**
   - 502 Bad Gateway
   - 503 Service Unavailable
   - 504 Gateway Timeout

2. **Non-Retryable Errors:**
   - 400 Bad Request
   - 404 Not Found
   - 422 Unprocessable Entity
   - 452-456 Business validation errors

3. **Recommended Retry Configuration:**
   - Initial delay: 1 second
   - Maximum retries: 3
   - Backoff multiplier: 2x
   - Maximum delay: 10 seconds

## Monitoring and Alerting

### Metrics to Monitor

1. **Network Timeout Rate:** Percentage of requests timing out
2. **Connection Error Rate:** Percentage of connection failures
3. **Average Response Time:** Track trends
4. **Error Distribution:** Track which errors occur most frequently

### Alert Thresholds

- **Warning:** Network error rate > 5% over 5 minutes
- **Critical:** Network error rate > 15% over 5 minutes
- **Critical:** Average timeout rate > 10% over 5 minutes

## Troubleshooting

### High Timeout Rate

**Possible Causes:**
- Operator service is slow
- Network latency is high
- Timeout values are too aggressive

**Actions:**
1. Check operator service performance
2. Review network latency metrics
3. Consider increasing timeout values
4. Implement request queuing or circuit breaker

### High Connection Error Rate

**Possible Causes:**
- Operator service is down
- Network connectivity issues
- Firewall blocking connections
- DNS resolution failures

**Actions:**
1. Verify operator service availability
2. Check network connectivity
3. Review firewall rules
4. Check DNS configuration

### SSL/TLS Errors

**Possible Causes:**
- Certificate expired
- Certificate chain incomplete
- Certificate hostname mismatch
- TLS version incompatibility

**Actions:**
1. Verify certificate validity
2. Check certificate chain
3. Verify hostname matches certificate
4. Review TLS configuration

## Testing

### Unit Tests

Test network exception handling with mocked WebClient responses:

```java
@Test
void shouldHandleTimeout() {
    // Mock timeout exception
    when(webClient.post().uri(anyString()))
        .thenThrow(new ReadTimeoutException());
    
    // Verify NetworkTimeoutException is thrown
    StepVerifier.create(operatorService.check(request))
        .expectError(NetworkTimeoutException.class)
        .verify();
}
```

### Integration Tests

Use WireMock to simulate network failures:

```java
@Test
void shouldHandleConnectionRefused() {
    // Configure WireMock to refuse connections
    wireMockServer.stop();
    
    // Verify NetworkConnectionException is thrown
    StepVerifier.create(operatorService.check(request))
        .expectError(NetworkConnectionException.class)
        .verify();
}
```

## Best Practices

1. **Set Appropriate Timeouts:** Balance between user experience and system stability
2. **Log Network Errors:** Include full exception stack traces for debugging
3. **Monitor Metrics:** Track network error rates and response times
4. **Implement Circuit Breakers:** Prevent cascading failures
5. **Use Retry Logic:** Implement intelligent retry strategies for transient failures
6. **Test Network Failures:** Regularly test timeout and connection failure scenarios
7. **Document Changes:** Keep this document updated with timeout configuration changes

## Related Documentation

- [Resilience Patterns](./resilience.md)
- [Observability](../runtime/observability.md)
- [Error Catalog](../api/error-catalog.md)

