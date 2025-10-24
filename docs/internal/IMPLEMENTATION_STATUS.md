# PSP Service Implementation Status

## Overview

PSP Service is in MVP (Minimum Viable Product) stage with implemented incoming APIs for processing requests from the operator.

**Architecture:** PSP system has only two communication directions:
- **Incoming (Operator → PSP)** - `IncomingController.java` ✅ IMPLEMENTED
- **Outgoing (PSP → Operator)** - `OperatorClient.java` 🔄 PLANNED

## ✅ Implemented Components

### API Layer
- **IncomingController** - processing incoming requests from operator
  - `POST /in/qr/{version}/tx/check` - transaction verification
  - `POST /in/qr/{version}/tx/create` - transaction creation  
  - `POST /in/qr/{version}/tx/execute/{transactionId}` - transaction execution
  - `POST /in/qr/{version}/tx/update/{transactionId}` - status update

### Security
- **SignatureService** - JWS v2 signature verification
- **SignatureInterceptor** - signature interception and validation
- **KeyManagementService** - signature key management

### Business Logic
- **IncomingService** - main business logic for request processing
- **ValidationUtil** - DTO object validation
- **JsonUtil** - JSON processing

### Data Layer
- **Entity models**:
  - `TransactionEntity` - main transactions table
  - `CheckRequestEntity` - verification requests table
  - `ExtraDataEntity` - additional key-value information
- **Oracle schema** - complete database schema with indexes and constraints

### Error Handling
- **GlobalExceptionHandler** - centralized exception handling
- **Specialized exceptions** - for various error types
- **Standardized ErrorResponseDto** - uniform error responses

### Logging & Monitoring
- **LoggingUtil** - structured logging
- **LoggingFilter** - HTTP request logging filter
- **Detailed tracking** - operations, signatures, errors

## 🔄 Planned Components

### API Layer
- **External APIs** - facade for clients (`/api/qr/tx/*`)
- **OperatorClient** - client for outgoing requests to operator

### Infrastructure
- **Redis** - caching and idempotency
- **RabbitMQ** - asynchronous processing and DLQ
- **Outbox pattern** - reliable event delivery

### Advanced Features
- **Rate limiting** - request frequency limiting
- **Circuit breaker** - protection against cascading failures
- **Retry mechanisms** - retry attempts with exponential backoff

## 📊 Current Capabilities

### Supported Operations
1. **Check** - transaction feasibility verification
2. **Create** - new transaction creation
3. **Execute** - transaction execution
4. **Update** - transaction status update

### Transaction Types
- C2C, C2B, C2G, B2C, B2B, BANK_RESERVE, B2G

### Transaction Statuses
- CREATED (10), IN_PROCESS (20), ERROR (30), CANCELED (40), SUCCESS (50)

## 🚀 Next Steps

1. **External API implementation** - for clients
2. **Redis integration** - caching and idempotency
3. **RabbitMQ setup** - asynchronous processing
4. **OperatorClient** - outgoing requests to operator
5. **Testing** - unit, integration, contract tests
6. **Monitoring** - metrics, alerts, dashboards

## 📝 Notes

- All incoming requests require JWS v2 signature
- API versioning supported via path parameter
- Structured logging for all operations
- Centralized error handling
- Complete database schema with indexes and constraints
