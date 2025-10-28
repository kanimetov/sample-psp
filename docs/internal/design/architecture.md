## Architecture

### Implementation

- **PSP Service** (Spring Boot, stateless, 2 instances)
  - **IncomingController** - Beneficiary facade (/in/qr/{version}/tx/*)
  - **MerchantController** - Merchant facade (/merchant/qr/{version}/*)
    - Handles QR decoding once per request
    - Routes to appropriate service based on merchant provider
  - **SignatureService** - JWS v2 signature verification
  - **IncomingService** - Business logic for processing incoming requests
  - **MerchantService** - Business logic coordinator for merchant operations
  - **clients/** - External service clients
    - **BankClient** - Internal bank operations
    - **OperatorClient** - External operator interaction
    - **QrDecoderClient** - QR code parsing and validation
  - **Oracle** - Main storage (operations, extra_data)
  - **Redis** - Caching and idempotency
  - **RabbitMQ** - Asynchronous processing

### Components

- **Incoming APIs** - processing requests from operator
- **Merchant APIs** - processing requests from merchants
- **Client Services** - organized external service clients
- **Signature verification** - JWS v2 with detailed logging
- **DTO validation** - comprehensive validation of incoming data
- **Structured logging** - detailed operation tracking
- **Error handling** - centralized GlobalExceptionHandler
- **Database** - complete schema with indexes and constraints

### Hot Path

**Incoming requests**: `/in/qr/{version}/tx/*` → signature verification → DTO validation → business logic → response

**Merchant requests**: `/merchant/qr/{version}/*` → DTO validation → QR decoding → MerchantService → BankService/OperatorService → response

**Transaction flow**: API → validation → idempotency → JWS/JWE → operator → mapping → response

### Fallback Strategies

- UPDATE via MQ when final status is missing
- GET status on timeout
- Retry mechanisms with exponential backoff


