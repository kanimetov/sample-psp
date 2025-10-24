## Architecture

### Current Implementation (MVP)

- **PSP Service** (Spring Boot, stateless, 6–10 instances)
  - **IncomingController** - Beneficiary facade (/in/qr/{version}/tx/*)
  - **SignatureService** - JWS v2 signature verification
  - **IncomingService** - Business logic for processing incoming requests
  - **OperatorClient** - Client for operator interaction (planned)
  - **Oracle** - Main storage (transactions, check_requests, extra_data)
  - **Redis** - Caching and idempotency (planned)
  - **RabbitMQ** - Asynchronous processing (planned)

### Implemented Components

- ✅ **Incoming APIs** - processing requests from operator
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

**Planned**: API → validation → idempotency → JWS/JWE → operator → mapping → response

### Fallback Strategies

- UPDATE via MQ when final status is missing
- GET status on timeout
- Retry mechanisms with exponential backoff


