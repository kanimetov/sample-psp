# PSP Service

Payment Service Provider for QR payments using DKIBQR protocol.

## Quick Start

```bash
cd psp-service
./gradlew bootRun
```

Service will start on `http://localhost:8080`

## Current Status

**MVP implemented** - complete PSP service architecture with QR payment and incoming transaction support.

### ✅ Implemented
- **API Layer**: 
  - IncomingController for incoming requests from operators
  - MerchantController for external QR payments
- **Service Layer**: Complete service architecture
  - MerchantService - QR payment routing
  - BankService - bank transaction processing
  - OperatorService - operator transaction processing
  - IncomingService - facade for incoming operations
- **Client Layer**: External clients
  - BankClient - integration with banking systems
  - OperatorClient - integration with operator APIs
  - QrDecoderClient - QR code decoding
- **Security**: JWS v2 signature verification, key management
- **Data Layer**: Entity models and Oracle schema
- **Error Handling**: Centralized exception handling
- **Logging**: Structured logging for all operations

### 🔄 In Development
- Redis integration for caching
- RabbitMQ integration for asynchronous processing
- OperatorService extension for incoming transactions

## API Endpoints

### Incoming from Operator (active)
- `POST /in/qr/{version}/tx/check` - Transaction check
- `POST /in/qr/{version}/tx/create` - Transaction creation  
- `POST /in/qr/{version}/tx/execute/{transactionId}` - Transaction execution
- `POST /in/qr/{version}/tx/update/{transactionId}` - Status update

### External APIs for QR Payments (active)
- `POST /out/qr/{version}/check` - QR code check
- `POST /out/qr/{version}/make-payment` - Payment creation

### Routing Architecture
- **BankService**: Processes transactions for `merchantProvider = "demirbank"`
- **OperatorService**: Processes transactions for all other providers
- **IncomingService**: Always delegates operations to BankService

## Technologies

- **Java 21** + **Spring Boot 3.4.1**
- **Gradle** + **Lombok**
- **Jakarta Bean Validation** + **Jackson**
- **Oracle DB** + **Redis** (configured) + **RabbitMQ** (planned)
- **Reactive Programming** (WebFlux + Reactor)
- **JWS v2** for signature verification

## Documentation

📚 **Complete documentation**: [`../docs/internal/`](../docs/internal/)

### Main sections:
- **[design/service-architecture.md](../docs/internal/design/service-architecture.md)** - Service architecture
- **[api/](../docs/internal/api/)** - API contracts and DTOs
- **[product/PRD.md](../docs/internal/product/PRD.md)** - Product requirements
- **[design/](../docs/internal/design/)** - Architecture and flows
- **[security/](../docs/internal/security/)** - Cryptography and security
- **[data/](../docs/internal/data/)** - Database schema and migrations

### Setup:
- **[SIGNATURE_SETUP.md](SIGNATURE_SETUP.md)** - RSA signature setup
- **[ORACLE_SETUP.md](ORACLE_SETUP.md)** - Oracle DB setup

## Request Examples

### QR Payment (external API)
```bash
# QR code check
curl -X POST http://localhost:8080/out/qr/v1/check \
  -H "Content-Type: application/json" \
  -d '{
    "qrUri": "https://example.com/qr?data=encoded_qr_data"
  }'

# Payment creation
curl -X POST http://localhost:8080/out/qr/v1/make-payment \
  -H "Content-Type: application/json" \
  -d '{
    "paymentSessionId": "session-uuid",
    "amount": 100000
  }'
```

### Incoming Transaction (from operator)
```bash
# Transaction check (requires JWS v2 signature)
curl -X POST http://localhost:8080/in/qr/v1/tx/check \
  -H "Content-Type: application/json" \
  -H "H-HASH: <jws-signature>" \
  -d '{
    "qrType": "staticQr",
    "merchantProvider": "DEMO",
    "merchantCode": 5411,
    "currencyCode": "417",
    "customerType": "1",
    "amount": 100000,
    "qrLinkHash": "AB12"
  }'
```

## Development

### Requirements
- Java 21+
- Gradle 8.x (wrapper included)

### Build
```bash
./gradlew build
```

### Configuration
Settings in `src/main/resources/application.yml`:
- Server port
- Database connections  
- Redis configuration
- RabbitMQ queues
- Logging levels

### Logging
Structured logging configured in `logback-spring.xml`:

**Log directory**: `logs/`
- `psp-service.log` - main application logs
- `psp-service-error.log` - errors only (ERROR level)
- `psp-service-audit.log` - operation audit (90 days retention)
- `psp-service-performance.log` - performance metrics (7 days retention)

**Log rotation**:
- Maximum file size: 100MB
- History: 30 days for main logs
- Total size: up to 1GB

**Logging levels**:
- `kg.demirbank.psp`: DEBUG (dev), INFO (prod)
- `org.hibernate.SQL`: DEBUG (dev), WARN (prod)
- `org.springframework`: INFO (dev), WARN (prod)

---

**Internal project - Demirbank**

