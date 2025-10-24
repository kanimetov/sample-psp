# DTO Specification

> **Important:** For complete description of all endpoints and DTOs see [dto-mapping.md](dto-mapping.md)

All strings UTF‑8; numbers are integers unless specified otherwise. Amounts in tyiyn (minor units).

## DTO Package Structure

```java
kg.demirbank.psp.dto/
├── KeyValueDto.java       - Helper DTO for extra fields
├── CheckRequestDto.java   - Request for check operation
├── CreateRequestDto.java  - Request for create operation
├── CheckResponseDto.java  - Response for check operation
├── CreateResponseDto.java - Response for create operation
├── StatusDto.java         - Universal DTO for execute/get responses
└── UpdateDto.java         - Bidirectional DTO for update operations
```

## Common headers (outgoing to Operator)

- H-PSP-TOKEN: string (required)
- H-PSP-ID: string (required)
- H-SIGNING-VERSION: "2" (required)
- H-HASH: string (JWS v2 signature payload) (required)

## Brief DTO Description

### CheckRequestDto

**Used in:**
- `POST /ipc/operator/api/v1/payment/qr/{version}/tx/check` (outgoing)
- `POST /in/qr/{version}/tx/check` (incoming from operator)
- `POST /api/qr/tx/check` (external API for clients)

**Fields:**
- qrType: string, enum ["staticQr","dynamicQr"] (required, @Pattern)
- merchantProvider: string, max 32 (required, @NotBlank)
- merchantId: string, max 32 (optional, @Size)
- serviceId: string, max 32 (optional, @Size)
- serviceName: string, max 32 (optional, @Size)
- beneficiaryAccountNumber: string, max 32 (optional, @Size)
- merchantCode: Integer, 0-9999 (required, @NotNull @Min @Max)
- currencyCode: string, 3 digits, default "417" (required, @Pattern)
- qrTransactionId: string, max 32 (optional, @Size)
- qrComment: string, max 32 (optional, @Size)
- customerType: string, enum ["1","2"] (required, @Pattern) - 1=Individual, 2=Corporate
- amount: Long, max 13 digits, positive (required, @NotNull @Digits @Positive)
- qrLinkHash: string, 4 alphanumeric chars (required, @Pattern "^[A-Z0-9]{4}$")
- extra: List<KeyValueDto>, max 5 (optional, @Size @Valid)

### CreateRequestDto

**Used in:**
- `POST /ipc/operator/api/v1/payment/qr/{version}/tx/create` (outgoing)
- `POST /in/qr/{version}/tx/create` (incoming from operator)
- `POST /api/qr/tx/create` (external API)

**Fields:** All fields from CheckRequestDto plus:
- transactionId: string, max 32 (required, @NotNull @Size) - Transaction ID (operator's transaction ID)
- pspTransactionId: string, max 50 (required, @NotBlank @Size) - Transaction ID in PSP
- receiptId: string, max 20 (required, @NotBlank @Size) - Receipt ID
- transactionType: CustomerType enum (required, @NotNull) - Transaction type

### CheckResponseDto

**Returned from:**
- Check operations (all directions)

**Fields:**
- beneficiaryName: string (masked, e.g. "c***e A***o")
- transactionType: CustomerType - Transaction type (enum, nullable)

### CreateResponseDto

**Returned from:**
- Create operations (all directions)

**Fields:**
- transactionId: string (UUID) - Transaction ID from operator
- status: Status enum - Transaction status (nullable)
- transactionType: CustomerType - Transaction type (enum)
- amount: Long - Transaction amount in tyiyn
- beneficiaryName: string - Beneficiary name (masked)
- customerType: Integer - Customer type (1=Individual, 2=Corporate)
- receiptId: string - Receipt ID
- createdDate: string (ISO8601) - Creation date
- executedDate: string (ISO8601) - Execution date (default "")

### StatusDto

**Used in:**
- Execute responses (all directions)
- Get responses (all directions)

**Fields:**
- transactionId: string (UUID)
- status: Status - transaction status enum (nullable)
- transactionType: CustomerType - transaction type enum (nullable)
- amount: Long - amount in tyiyn
- beneficiaryName: string (nullable)
- customerType: string (nullable) - "1" or "2"
- receiptId: string (nullable)
- createdDate: string (ISO8601)
- executedDate: string (ISO8601, nullable)

### UpdateDto

**Used in:**
- `POST /in/qr/{version}/tx/update/{transactionId}` (incoming from operator)

**Fields:**
- status: Status enum (required, @NotNull) - New transaction status
- updateDate: string, max 30 (optional, @Size) - Update date

### KeyValueDto

**Helper type for extra fields**

**Fields:**
- key: string, max 64 (required, @NotBlank @Size)
- value: string, max 256 (required, @NotBlank @Size)

## Endpoint Mapping

### Incoming from Operator (Operator → PSP) - IMPLEMENTED

| Operation | Endpoint | Request DTO | Response DTO |
|----------|----------|-------------|--------------|
| Check | POST /in/qr/{v}/tx/check | CheckRequestDto | CheckResponseDto |
| Create | POST /in/qr/{v}/tx/create | CreateRequestDto | CreateResponseDto |
| Execute | POST /in/qr/{v}/tx/execute/{id} | (empty) | StatusDto |
| Update | POST /in/qr/{v}/tx/update/{id} | UpdateDto | ACK (200 OK) |

### Outgoing to Operator (PSP → Operator) - PLANNED

| Operation | Endpoint | Request DTO | Response DTO |
|----------|----------|-------------|--------------|
| Check | POST /psp/api/v1/payment/qr/{version}/tx/check | CheckRequestDto | CheckResponseDto |
| Create | POST /psp/api/v1/payment/qr/{version}/tx/create | CreateRequestDto | CreateResponseDto |
| Execute | POST /psp/api/v1/payment/qr/{version}/tx/execute/{id} | (empty) | StatusDto |

### Architecture Note

**PSP System has only two communication directions:**

1. **Incoming (Operator → PSP)** - `IncomingController.java`
2. **Outgoing (PSP → Operator)** - `OperatorClient.java`

**No External APIs:** PSP does not expose direct client-facing APIs. It acts as an intermediary between the Operator and bank systems.

## Idempotency

All state-changing operations must be idempotent:

- **check:** Idempotency by `(qrLinkHash, amount, customerType)`
- **create:** Idempotency by `pspTransactionId`
- **execute:** Idempotency by `transactionId`
- **update:** Idempotency by `(transactionId, status)`

Implemented via Redis with TTL keys.

## Status Codes (Status enum)

| Code | Name | Final | Description |
|-----|----------|-----------|----------|
| 10 | CREATED | No | Transaction created |
| 20 | IN_PROCESS | No | Transaction in process |
| 30 | ERROR | Yes | Transaction completed with error |
| 40 | CANCELED | Yes | Transaction canceled |
| 50 | SUCCESS | Yes | Transaction successfully completed |

## Transaction Types (CustomerType enum)

| Code | Name | Description |
|-----|----------|----------|
| 10 | C2C | Transfer via QR code/payment link |
| 20 | C2B | Purchase via QR code/payment link |
| 30 | C2G | Government payment (individual) via QR code/payment link |
| 40 | B2C | Money transfer/withdrawal/refund via QR code/payment link |
| 50 | B2B | Payment/transfer via QR code/payment link |
| 60 | BANK_RESERVE | Electronic message about bank reserve placement |
| 70 | B2G | Government payment (legal entity) via QR code/payment link |

## See Also

- **[dto-mapping.md](dto-mapping.md)** - Complete detailed description of all endpoints with JSON examples
- [API Endpoints Reference](endpoints-reference.md) - Complete API endpoints
- [../security/crypto.md](../security/crypto.md) - JWS/JWE specifications
- [../data/schema.md](../data/schema.md) - Database schema
