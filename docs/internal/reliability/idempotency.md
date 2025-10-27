# Idempotency Implementation Guide

> **📋 Reference:** For complete Redis key patterns and TTL values, see [Redis Schema Reference](../data/redis-schema.md)

## Overview

Idempotency is a critical requirement for the PSP system to ensure that duplicate operations don't cause unintended side effects. The system implements idempotency using Redis as a distributed cache with specific key patterns and TTL strategies.

## Implementation

Idempotency is implemented using Redis with specific key patterns and TTL strategies to prevent duplicate operations across distributed systems.

## Idempotency Strategy

### Key Principles

1. **Operation-Level Idempotency:** Each operation type has its own idempotency key pattern
2. **Time-Based Expiration:** Different TTL values based on operation criticality
3. **Graceful Degradation:** System continues to work if Redis is unavailable (with logging)
4. **Atomic Operations:** Use Redis `SET NX EX` for atomic check-and-set operations

### Redis Key Patterns

| Operation | Key Pattern | TTL | Description |
|-----------|-------------|-----|-------------|
| **Check** | `idem:check:{merchantProvider}:{qrTransactionId}:{amount}` | 120s | QR verification idempotency |
| **Create** | `idem:create:{pspTransactionId}` | 24h | Transaction creation idempotency |
| **Execute** | `idem:execute:{transactionId}` | 24h | Transaction execution idempotency |
| **Update** | `idem:update:{transactionId}:{status}` | 24h | Status update idempotency |

### Key Components

**Example Keys:**
```
idem:check:DEMO_MERCHANT:QR123:100000
idem:create:PSP-TX-123456
idem:execute:fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7
idem:update:fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7:50
```

## Implementation Details

### 1. Redis Operations

**Atomic Check-and-Set Pattern:**
```redis
# Check if operation already processed
SET idem:check:{key} "1" NX EX 120

# Response:
# - "OK" if key was set (first time)
# - null if key already exists (duplicate)
```

**Implementation Logic:**
```java
public boolean isOperationIdempotent(String key, int ttlSeconds) {
    String result = redisTemplate.opsForValue()
        .setIfAbsent(key, "1", Duration.ofSeconds(ttlSeconds));
    return result == null; // null means key already exists (duplicate)
}
```

### 2. Operation-Specific Implementation

#### Check Operation
```java
public Mono<CheckResponseDto> checkTransaction(CheckRequestDto request) {
    String idempotencyKey = String.format("idem:check:%s:%s:%d",
        request.getMerchantProvider(), 
        request.getQrTransactionId(),
        request.getAmount()
    );
    
    return idempotencyService.checkAndSet(idempotencyKey, 120)
        .flatMap(isDuplicate -> {
            if (isDuplicate) {
                // Return cached response or 200 OK
                return getCachedCheckResponse(idempotencyKey);
            }
            // Process new request
            return processCheckRequest(request, idempotencyKey);
        });
}
```

#### Create Operation
```java
public Mono<CreateResponseDto> createTransaction(CreateRequestDto request) {
    String idempotencyKey = String.format("idem:create:%s", 
        request.getPspTransactionId());
    
    return idempotencyService.checkAndSet(idempotencyKey, 86400)
        .flatMap(isDuplicate -> {
            if (isDuplicate) {
                // Return cached response
                return getCachedCreateResponse(idempotencyKey);
            }
            // Process new request
            return processCreateRequest(request, idempotencyKey);
        });
}
```

#### Execute Operation
```java
public Mono<StatusDto> executeTransaction(String transactionId) {
    String idempotencyKey = String.format("idem:execute:%s", transactionId);
    
    return idempotencyService.checkAndSet(idempotencyKey, 86400)
        .flatMap(isDuplicate -> {
            if (isDuplicate) {
                // Return cached status
                return getCachedStatus(transactionId);
            }
            // Process execution
            return processExecuteRequest(transactionId, idempotencyKey);
        });
}
```

#### Update Operation
```java
public Mono<Void> updateTransaction(String transactionId, UpdateDto updateRequest) {
    String idempotencyKey = String.format("idem:update:%s:%d", 
        transactionId, updateRequest.getStatus().getCode());
    
    return idempotencyService.checkAndSet(idempotencyKey, 86400)
        .flatMap(isDuplicate -> {
            if (isDuplicate) {
                // Return 200 OK without processing
                return Mono.empty();
            }
            // Process update
            return processUpdateRequest(transactionId, updateRequest, idempotencyKey);
        });
}
```

### 3. Idempotency Service Implementation

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    /**
     * Check if operation is idempotent and set key if not exists
     * @param key Idempotency key
     * @param ttlSeconds TTL in seconds
     * @return true if operation is duplicate, false if first time
     */
    public Mono<Boolean> checkAndSet(String key, int ttlSeconds) {
        return Mono.fromCallable(() -> {
            try {
                Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(key, "1", Duration.ofSeconds(ttlSeconds));
                
                boolean isDuplicate = result == null;
                log.debug("Idempotency check for key {}: duplicate={}", key, isDuplicate);
                return isDuplicate;
                
            } catch (Exception e) {
                log.warn("Redis unavailable for idempotency check, allowing operation: {}", e.getMessage());
                return false; // Allow operation if Redis is down
            }
        });
    }
    
    /**
     * Cache operation result for future duplicate requests
     */
    public Mono<Void> cacheResult(String key, Object result, int ttlSeconds) {
        return Mono.fromRunnable(() -> {
            try {
                String jsonResult = JsonUtil.toJson(result);
                redisTemplate.opsForValue()
                    .set(key + ":result", jsonResult, Duration.ofSeconds(ttlSeconds));
            } catch (Exception e) {
                log.warn("Failed to cache result for key {}: {}", key, e.getMessage());
            }
        });
    }
    
    /**
     * Get cached result for duplicate requests
     */
    public <T> Mono<T> getCachedResult(String key, Class<T> resultType) {
        return Mono.fromCallable(() -> {
            try {
                String cachedJson = redisTemplate.opsForValue().get(key + ":result");
                if (cachedJson != null) {
                    return JsonUtil.fromJson(cachedJson, resultType);
                }
                return null;
            } catch (Exception e) {
                log.warn("Failed to get cached result for key {}: {}", key, e.getMessage());
                return null;
            }
        });
    }
}
```

## TTL Strategy

### TTL Values by Operation

| Operation | TTL | Rationale |
|-----------|-----|-----------|
| **Check** | 120s | Short-lived, quick operations, frequent duplicates |
| **Create** | 24h | Long-lived, prevent duplicate transaction creation |
| **Execute** | 24h | Long-lived, prevent duplicate execution |
| **Update** | 24h | Long-lived, prevent duplicate status updates |

### TTL Management

**Automatic Expiration:**
- All keys have explicit TTL using `EX` parameter
- No manual cleanup required
- Memory usage bounded by TTL values

**TTL Monitoring:**
```redis
# Check remaining TTL
TTL idem:create:PSP-TX-123456

# Check if key exists
EXISTS idem:create:PSP-TX-123456
```

## Error Handling

### Redis Unavailability

**Graceful Degradation Strategy:**
1. **Log Warning:** Record Redis unavailability
2. **Continue Processing:** Allow operations without idempotency
3. **Accept Risk:** Accept potential duplicates during Redis downtime
4. **Monitor:** Track duplicate operations in logs

**Implementation:**
```java
public Mono<Boolean> checkAndSet(String key, int ttlSeconds) {
    return Mono.fromCallable(() -> {
        try {
            // Redis operation
            return performRedisCheck(key, ttlSeconds);
        } catch (Exception e) {
            log.warn("Redis unavailable for idempotency check, allowing operation: {}", e.getMessage());
            return false; // Allow operation
        }
    });
}
```

### Common Redis Errors

| Error | Cause | Resolution |
|-------|-------|------------|
| `OOM` | Memory limit exceeded | Scale up Redis or optimize keys |
| `NOAUTH` | Authentication failed | Check Redis credentials |
| `CONNECTION_REFUSED` | Redis unavailable | Check Redis service |
| `TIMEOUT` | Operation timeout | Check network/load |

## Integration Plan

### 1. Add Redis Dependency

**build.gradle:**
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.boot:spring-boot-starter-cache'
    // ... existing dependencies
}
```

### 2. Redis Configuration

**application.yml:**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

### 3. Redis Configuration Class

```java
@Configuration
@EnableCaching
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
```

### 4. Service Integration

**Update IncomingServiceImpl:**
```java
@Service
@RequiredArgsConstructor
public class IncomingServiceImpl implements IncomingService {
    
    private final IdempotencyService idempotencyService;
    
    @Override
    public Mono<CheckResponseDto> checkTransaction(CheckRequestDto request) {
        // Add idempotency check before processing
        return idempotencyService.checkAndSet(/* ... */)
            .flatMap(isDuplicate -> {
                if (isDuplicate) {
                    return getCachedResponse(/* ... */);
                }
                return processCheckRequest(request);
            });
    }
    
    // ... similar updates for other methods
}
```

## Monitoring and Observability

### Key Metrics

| Metric | Description | Alert Threshold |
|--------|-------------|-----------------|
| Redis Hit Rate | Percentage of idempotency hits | < 95% |
| Redis Latency | P95 latency for Redis operations | > 2ms |
| Duplicate Operations | Count of duplicate operations | > 1% of total |
| Redis Memory Usage | Memory consumption | > 80% |

### Logging

**Structured Logging:**
```java
log.info("Idempotency check: key={}, duplicate={}, ttl={}", 
    key, isDuplicate, ttlSeconds);
```

**Audit Trail:**
```java
LoggingUtil.logAuditTrail("IDEMPOTENCY_CHECK", "REDIS", key, properties);
```

## Testing Strategy

### Unit Tests

```java
@Test
void testIdempotencyCheck() {
    // First call should return false (not duplicate)
    StepVerifier.create(idempotencyService.checkAndSet("test-key", 60))
        .expectNext(false)
        .verifyComplete();
    
    // Second call should return true (duplicate)
    StepVerifier.create(idempotencyService.checkAndSet("test-key", 60))
        .expectNext(true)
        .verifyComplete();
}
```

### Integration Tests

```java
@Test
void testCheckOperationIdempotency() {
    CheckRequestDto request = createTestRequest();
    
    // First call
    CheckResponseDto response1 = webTestClient.post()
        .uri("/in/qr/v1/tx/check")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(CheckResponseDto.class)
        .returnResult()
        .getResponseBody();
    
    // Duplicate call should return same response
    CheckResponseDto response2 = webTestClient.post()
        .uri("/in/qr/v1/tx/check")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(CheckResponseDto.class)
        .returnResult()
        .getResponseBody();
    
    assertThat(response1).isEqualTo(response2);
}
```

## Idempotency Key Selection Rationale

### Check Operation Key Components

**Selected Fields:** `merchantProvider`, `qrTransactionId`, `amount`

**Rationale:**
- **`merchantProvider`**: Identifies the merchant system making the request
- **`qrTransactionId`**: Unique identifier for the QR transaction (optional but important for uniqueness)
- **`amount`**: Transaction amount - critical for preventing duplicate checks with different amounts

**Why these fields:**
1. **Uniqueness**: Combination ensures unique identification of check requests
2. **Business Logic**: All fields affect the check result (beneficiary name, transaction type)
3. **Security**: Prevents amount manipulation in duplicate requests
4. **Simplicity**: Minimal set of fields needed for proper idempotency

**Alternative Considerations:**
- **`qrLinkHash`**: Could be used but is more for QR validation than idempotency
- **`merchantCode`**: Could be included but `merchantProvider` is more specific
- **`currencyCode`**: Usually constant (417 for KGS), not needed for uniqueness
- **`customerType`**: Removed as it's not provided in the request

### Create Operation Key Components

**Selected Field:** `pspTransactionId`

**Rationale:**
- **Unique Identifier**: `pspTransactionId` is the primary identifier for PSP transactions
- **Business Rule**: Each PSP transaction should be created only once
- **Long TTL**: 24 hours to prevent accidental duplicate creation

### Execute Operation Key Components

**Selected Field:** `transactionId`

**Rationale:**
- **Unique Identifier**: `transactionId` is the operator's transaction identifier
- **Business Rule**: Each transaction should be executed only once
- **Long TTL**: 24 hours to prevent duplicate execution

### Update Operation Key Components

**Selected Fields:** `transactionId`, `status`

**Rationale:**
- **`transactionId`**: Identifies the transaction to update
- **`status`**: Specific status update - allows same transaction to be updated to different statuses
- **Business Rule**: Same status update should be idempotent, different status updates should be allowed

## Related Documentation

- [Redis Schema Reference](../data/redis-schema.md) - Complete Redis key patterns and TTL values
- [Architecture Decisions](../design/decisions/ADR-0002-redis-mandatory.md) - Redis mandatory decision
- [Configuration Reference](../runtime/configuration-reference.md) - Redis configuration
- [Observability](../runtime/observability.md) - Monitoring and logging


