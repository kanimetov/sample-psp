# DTO Specification

> **Important:** For complete description of all endpoints and DTOs see [dto-mapping.md](dto-mapping.md)

All strings UTF‑8; numbers are integers unless specified otherwise. Amounts in tyiyn (minor units).

## DTO Organization

DTOs are organized by communication direction to clearly indicate data flow and eliminate duplication:

### Response DTOs:
- **IncomingTransactionResponseDto**: Used for incoming create, execute, and status operations
- **OutgoingTransactionResponseDto**: Used for outgoing create, execute, and status operations  
- **IncomingCheckResponseDto**: Used for incoming check operations
- **OutgoingCheckResponseDto**: Used for outgoing check operations

### Request DTOs:
- **IncomingCheckRequestDto**: Used for incoming check operations
- **IncomingCreateRequestDto**: Used for incoming create operations
- **OutgoingCheckRequestDto**: Used for outgoing check operations
- **OutgoingCreateRequestDto**: Used for outgoing create operations

## DTO Package Structure

```java
kg.demirbank.psp.dto/
├── common/
│   ├── KeyValueDto.java       - Helper DTO for extra fields
│   └── UpdateDto.java         - Bidirectional DTO for update operations
├── incoming/
│   ├── request/
│   │   ├── IncomingCheckRequestDto.java   - Request for check operation
│   │   └── IncomingCreateRequestDto.java  - Request for create operation
│   └── response/
│       ├── IncomingCheckResponseDto.java      - Response for check operation
│       └── IncomingTransactionResponseDto.java - Response for create/execute/status operations
└── outgoing/
    ├── request/
    │   ├── OutgoingCheckRequestDto.java   - Request for check operation
    │   └── OutgoingCreateRequestDto.java  - Request for create operation
    └── response/
        ├── OutgoingCheckResponseDto.java      - Response for check operation
        └── OutgoingTransactionResponseDto.java - Response for create/execute/status operations
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

### IncomingCheckResponseDto

**Returned from:**
- Incoming check operations (Operator → PSP)

**Fields:**
- beneficiaryName: string (masked, e.g. "c***e A***o")
- transactionType: CustomerType - Transaction type (enum, nullable)

### OutgoingCheckResponseDto

**Returned from:**
- Outgoing check operations (PSP → Operator)

**Fields:**
- beneficiaryName: string (masked, e.g. "c***e A***o")
- transactionType: CustomerType - Transaction type (enum, nullable)

### IncomingTransactionResponseDto

**Used in:**
- Incoming create/execute/status responses (Operator → PSP)

**Fields:**
- transactionId: string (UUID) - Transaction ID from operator
- status: Status enum - Transaction status (nullable)
- amount: Long - Transaction amount in tyiyn
- beneficiaryName: string - Beneficiary name (masked)
- customerType: string - Customer type ("1"=Individual, "2"=Corporate)
- receiptId: string - Receipt ID
- createdDate: string (ISO8601) - Creation date
- executedDate: string (ISO8601) - Execution date (empty if not executed)

### OutgoingTransactionResponseDto

**Used in:**
- Outgoing create/execute/status responses (PSP → Operator)

**Fields:**
- transactionId: string (UUID) - Transaction ID from operator
- status: Status enum - Transaction status (nullable)
- transactionType: CustomerType - Transaction type (enum)
- amount: Long - Transaction amount in tyiyn
- commission: Long - Transaction commission in tyiyn
- senderTransactionId: string - Payment ID in sender's system
- senderReceiptId: string - Sender's receipt number
- senderBic: string - Sender's BIC
- beneficiaryBic: string - Beneficiary's BIC
- createdDate: string (ISO8601) - Creation date
- executedDate: string (ISO8601) - Execution date (empty if not executed)
- extra: List<KeyValueDto> - Additional fields (max 3 key-value pairs)

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

### Incoming from Operator (Operator → PSP)

| Operation | Endpoint | Request DTO | Response DTO |
|----------|----------|-------------|--------------|
| Check | POST /in/qr/{v}/tx/check | IncomingCheckRequestDto | IncomingCheckResponseDto |
| Create | POST /in/qr/{v}/tx/create | IncomingCreateRequestDto | IncomingTransactionResponseDto |
| Execute | POST /in/qr/{v}/tx/execute/{id} | (empty) | IncomingTransactionResponseDto |
| Update | POST /in/qr/{v}/tx/update/{id} | UpdateDto | ACK (200 OK) |

### Outgoing to Operator (PSP → Operator)

| Operation | Endpoint | Request DTO | Response DTO |
|----------|----------|-------------|--------------|
| Check | POST /psp/api/v1/payment/qr/{version}/tx/check | OutgoingCheckRequestDto | OutgoingCheckResponseDto |
| Create | POST /psp/api/v1/payment/qr/{version}/tx/create | OutgoingCreateRequestDto | OutgoingTransactionResponseDto |
| Execute | POST /psp/api/v1/payment/qr/{version}/tx/execute/{id} | (empty) | OutgoingTransactionResponseDto |

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

## Transaction Types (TransactionType enum)

| Code | Name | Description |
|-----|----------|----------|
| 10 | C2C | Transfer via QR code/payment link |
| 20 | C2B | Purchase via QR code/payment link |
| 30 | C2G | Government payment (individual) via QR code/payment link |
| 40 | B2C | Money transfer/withdrawal/refund via QR code/payment link |
| 50 | B2B | Payment/transfer via QR code/payment link |
| 60 | BANK_RESERVE | Electronic message about bank reserve placement |
| 70 | B2G | Government payment (legal entity) via QR code/payment link |

## Transfer Directions

| Code | Name | Description |
|-----|----------|----------|
| IN | INCOMING | Incoming transfer to PSP (Operator → PSP) |
| OUT | OUTGOING | Outgoing transfer from PSP (PSP → Operator) |
| OWN | INTERNAL | Internal transfer within PSP system |

## Customer Types

| Code | Name | Description |
|-----|----------|----------|
| 1 | INDIVIDUAL | Individual customer |
| 2 | CORPORATE | Corporate customer |

## See Also

- **[dto-mapping.md](dto-mapping.md)** - Complete detailed description of all endpoints with JSON examples
- [API Endpoints Reference](endpoints-reference.md) - Complete API endpoints
- [../security/crypto.md](../security/crypto.md) - JWS/JWE specifications
- [../data/schema.md](../data/schema.md) - Database schema
