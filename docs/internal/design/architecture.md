## Architecture

### Current Implementation (MVP)

- **PSP Service** (Spring Boot, stateless, 2 instances)
  - **IncomingController** - Beneficiary facade (/in/qr/{version}/tx/*)
  - **MerchantController** - Merchant facade (/out/qr/{version}/*)
  - **SignatureService** - JWS v2 signature verification
  - **IncomingService** - Business logic for processing incoming requests
  - **MerchantService** - Business logic for merchant operations
  - **clients/** - External service clients
    - **BankClient** - Internal bank operations
    - **OperatorClient** - External operator interaction
    - **QrDecoderClient** - QR code parsing and validation
  - **Oracle** - Main storage (operations, extra_data)
  - **Redis** - Caching and idempotency (planned)
  - **RabbitMQ** - Asynchronous processing (planned)

### Implemented Components

- ✅ **Incoming APIs** - processing requests from operator
- ✅ **Merchant APIs** - processing requests from merchants
- ✅ **Client Services** - organized external service clients
- ✅ **Signature verification** - JWS v2 with detailed logging
- ✅ **DTO validation** - comprehensive validation of incoming data
- ✅ **Structured logging** - detailed operation tracking
- ✅ **Error handling** - centralized GlobalExceptionHandler
- ✅ **Database** - complete schema with indexes and constraints

### Planned Components

- 🔄 **External APIs** - facade for clients (/api/qr/tx/*)
- 🔄 **Redis** - caching and idempotency
- 🔄 **RabbitMQ** - asynchronous processing and DLQ
- 🔄 **Outbox pattern** - reliable event delivery

### Hot Path (Current)

**Incoming requests**: `/in/qr/{version}/tx/*` → signature verification → DTO validation → business logic → response

**Merchant requests**: `/out/qr/{version}/*` → DTO validation → QR decoding → service routing → response

**Planned**: API → validation → idempotency → JWS/JWE → operator → mapping → response

### Fallback Strategies

- UPDATE via MQ when final status is missing
- GET status on timeout
- Retry mechanisms with exponential backoff


