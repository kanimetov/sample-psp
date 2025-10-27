# DTO Mapping - Complete Endpoint and DTO Reference

## Overview

This document describes all PSP system endpoints with detailed mapping of DTOs used for request and response.

## DTO Package Structure

```
kg.demirbank.psp.dto/
├── common/
│   ├── KeyValueDto.java       - Helper DTO for additional fields
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

## DTO Organization

DTOs are organized by communication direction to clearly indicate data flow:

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

---

## A. Outgoing Requests to Operator (PSP → Operator)

**Service:** `OperatorClient` (via WebClient)  
**Base URL:** `/psp/api/v1/payment/qr/{version}/tx`

### 1. Check QR Details

Verification of QR code validity and obtaining beneficiary information.

```
POST /psp/api/v1/payment/qr/{version}/tx/check
```

**Headers:**
- `H-PSP-TOKEN: string` (required) - PSP token for authentication
- `H-PSP-ID: string` (required) - PSP identifier
- `H-SIGNING-VERSION: "2"` (required) - Signature version (v2 only)
- `H-HASH: string` (required) - JWS signature of request body

**Path Parameters:**
- `version: string` - QR protocol version (usually "1")

**Request Body:** `OutgoingCheckRequestDto`
```json
{
  "qrType": "staticQr",
  "merchantProvider": "DEMO_MERCHANT",
  "merchantCode": 5411,
  "currencyCode": "417",
  "customerType": "1",
  "amount": 100000,
  "qrLinkHash": "AB12"
}
```

**Response 200:** `OutgoingCheckResponseDto`
```json
{
  "beneficiaryName": "c***e A***o",
  "transactionType": null
}
```

---

### 2. Create Transaction

Creating a new transaction in the operator system.

```
POST /psp/api/v1/payment/qr/{version}/tx/create
```

**Headers:** (same as in check)

**Request Body:** `OutgoingCreateRequestDto`
```json
{
  "transactionId": "tx-12345678",
  "qrType": "staticQr",
  "merchantProvider": "DEMO_MERCHANT",
  "merchantCode": 5411,
  "currencyCode": "417",
  "customerType": "1",
  "pspTransactionId": "PSP-TX-123456",
  "receiptId": "RCP-001",
  "amount": 100000,
  "qrLinkHash": "AB12",
  "transactionType": "C2B"
}
```

**Response 200:** `OutgoingTransactionResponseDto`
```json
{
  "transactionId": "fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7",
  "status": null,
  "transactionType": "C2B",
  "amount": 100000,
  "beneficiaryName": "Sample Beneficiary",
  "customerType": 1,
  "receiptId": "RCP-001",
  "createdDate": "2022-11-01T12:00:00Z",
  "executedDate": ""
}
```

---

### 3. Execute Transaction

Request to execute a created transaction.

```
POST /psp/api/v1/payment/qr/{version}/tx/execute/{transactionId}
```

**Headers:** (same as in check)

**Path Parameters:**
- `transactionId: string` (UUID) - Transaction ID from operator

**Request Body:** Empty

**Response 200:** `OutgoingTransactionResponseDto`
```json
{
  "transactionId": "fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7",
  "status": null,
  "transactionType": null,
  "amount": 40000,
  "beneficiaryName": "c***e A***o",
  "customerType": "1",
  "receiptId": "7218199",
  "createdDate": "2022-11-01T12:00:00Z",
  "executedDate": "2022-11-01T12:02:00Z"
}
```

---

---

## B. Incoming Requests from Operator (Operator → PSP)

**Controller:** `IncomingController` (beneficiary endpoints)  
**Base URL:** `/in/qr/{version}/tx`

When PSP acts as payment recipient (beneficiary), the operator calls these endpoints.

### 1. Check (incoming)

```
POST /in/qr/{version}/tx/check
```

**Headers:**
- `H-HASH: string` (required) - JWS v2 signature of request body

**Request Body:** `OutgoingCheckRequestDto` (same as outgoing)

**Response 200:** `OutgoingCheckResponseDto`

---

### 2. Create (incoming)

```
POST /in/qr/{version}/tx/create
```

**Headers:**
- `H-HASH: string` (required)

**Request Body:** `OutgoingCreateRequestDto`

**Response 200:** `OutgoingTransactionResponseDto`

---

### 3. Execute (incoming)

```
POST /in/qr/{version}/tx/execute/{transactionId}
```

**Headers:**
- `H-HASH: string` (required)

**Request Body:** Empty

**Response 200:** `OutgoingTransactionResponseDto`

---

### 4. Update (incoming from operator)

The operator always sends UPDATE with the final transaction status.

```
POST /in/qr/{version}/tx/update/{transactionId}
```

**Headers:**
- `H-HASH: string` (required)

**Request Body:** `UpdateDto`
```json
{
  "status": 50,
  "updateDate": "2022-11-01T12:02:00Z"
}
```

**Response 200:** ACK (empty body)

---

## C. Architecture Note

**PSP System Architecture:**

The PSP system has only **two communication directions**:

1. **Outgoing (PSP → Operator)** - `OperatorClient.java` interface
2. **Incoming (Operator → PSP)** - `IncomingController.java` implementation

**No External APIs:** PSP does not expose direct client-facing APIs. It acts as an intermediary between the Operator and bank systems. Bank clients interact with PSP through the bank's internal systems, which then communicate with PSP using the Operator protocol.

**Communication Flow:**
```
Bank Client → Bank System → PSP (IncomingController) ↔ Operator
Bank Client → Bank System → PSP (OperatorClient) → Operator
```

---

## Detailed DTO Description

### CheckRequestDto

Used for QR code verification before creating a transaction.

| Field | Type | Required | Validation | Description |
|------|-----|----------|------------|----------|
| qrType | String | Yes | `staticQr\|dynamicQr` | QR code type |
| merchantProvider | String | Yes | max 32 chars | Unique provider identifier |
| merchantId | String | No | max 32 chars | Merchant ID |
| serviceId | String | No | max 32 chars | Service code |
| serviceName | String | No | max 32 chars | Service name |
| beneficiaryAccountNumber | String | No | max 32 chars | Beneficiary account number |
| merchantCode | Integer | Yes | 0-9999 | Merchant MCC code |
| currencyCode | String | Yes | 3 digits | Currency (always "417" for KGS) |
| qrTransactionId | String | No | max 32 chars | Transaction ID from QR |
| qrComment | String | No | max 32 chars | Payment comment |
| customerType | String | Yes | `1\|2` | 1=Individual, 2=Corporate |
| amount | Long | Yes | max 13 digits, positive | Amount in tyiyn |
| qrLinkHash | String | Yes | 4 alphanumeric | QR link hash |
| extra | List<KeyValueDto> | No | max 5 items | Additional fields |

### CreateRequestDto

Extended version of CheckRequestDto with additional fields for transaction creation.

Additional fields:
- `transactionId: String` (required, max 32) - Transaction ID (operator's transaction ID)
- `pspTransactionId: String` (required, max 50) - Transaction ID in PSP system
- `receiptId: String` (required, max 20) - Receipt/invoice ID
- `transactionType: CustomerType` (required, enum) - Transaction type

### IncomingCheckResponseDto / OutgoingCheckResponseDto

| Field | Type | Description |
|------|-----|----------|
| beneficiaryName | String | Masked beneficiary name (e.g., "c***e A***o") |
| transactionType | CustomerType | Transaction type (enum, nullable) |

### IncomingTransactionResponseDto / OutgoingTransactionResponseDto

Response with transaction creation confirmation.

| Field | Type | Description |
|------|-----|----------|
| transactionId | String | Transaction UUID created by operator |
| status | Status | Transaction status (enum, nullable) |
| transactionType | CustomerType | Transaction type (enum) |
| amount | Long | Transaction amount in tyiyn |
| beneficiaryName | String | Beneficiary name (masked) |
| customerType | Integer | Customer type (1=Individual, 2=Corporate) |
| receiptId | String | Receipt ID |
| createdDate | String | Creation date (ISO8601) |
| executedDate | String | Execution date (ISO8601, may be empty) |

### IncomingTransactionResponseDto / OutgoingTransactionResponseDto

Universal DTO for transaction status (used in execute and get).

| Field | Type | Nullable | Used in | Description |
|------|-----|----------|----------------|----------|
| transactionId | String | No | execute, get | Transaction UUID |
| status | Status | Yes | execute, get | Transaction status (enum) |
| transactionType | CustomerType | Yes | execute, get | Transaction type (enum) |
| amount | Long | No | execute, get | Amount in tyiyn |
| beneficiaryName | String | Yes | execute | Beneficiary name (masked) |
| customerType | String | Yes | execute | Customer type (1 or 2) |
| receiptId | String | Yes | execute, get | Receipt ID |
| createdDate | String | No | execute, get | Creation date (ISO8601) |
| executedDate | String | Yes | execute, get | Execution date (ISO8601) |

### UpdateDto

Used for incoming updates from operator (Operator → PSP).

| Field | Type | Required | Description |
|------|-----|----------|----------|
| status | Status | Yes | New transaction status (enum) |
| updateDate | String | No | Update date ISO8601 (max 30) |

### KeyValueDto

Helper DTO for additional fields in `extra`.

| Field | Type | Required | Validation |
|------|-----|----------|------------|
| key | String | Yes | NotBlank, max 64 chars |
| value | String | Yes | NotBlank, max 256 chars |

---

## Transaction Status Codes (Status enum)

| Code | Name | Final | Description |
|-----|----------|-----------|----------|
| 10 | CREATED | No | Transaction created |
| 20 | IN_PROCESS | No | Transaction in process |
| 30 | ERROR | Yes | Transaction completed with error |
| 40 | CANCELED | Yes | Transaction canceled |
| 50 | SUCCESS | Yes | Transaction successfully completed |

---

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

---

## Idempotency

All state-changing operations (check, create, execute, update) must be idempotent:

- **check:** Idempotency by `(qrLinkHash, amount, customerType)`
- **create:** Idempotency by `pspTransactionId`
- **execute:** Idempotency by `transactionId`
- **update:** Idempotency by `(transactionId, status)`

Idempotency is implemented via Redis with TTL keys.

---

## Usage Examples

### Complete sender flow (PSP → Operator)

1. **Check QR** → get beneficiary information
2. **Create** → create transaction, get `transactionId`
3. **Execute** → execute transaction
4. If execute didn't return final status → **Get** or wait for **Update** from operator
5. If operator is unavailable → send **Update** later via RabbitMQ

### Complete beneficiary flow (Operator → PSP)

1. Operator calls **Check** → PSP verifies details
2. Operator calls **Create** → PSP creates transaction
3. Operator calls **Execute** → PSP executes
4. Operator sends **Update** with final status

---

## Database Mapping

DTO mapping to `qr_tx` table:

| DTO Field | `qr_tx` Table |
|----------|-----------------|
| transactionId (operator) | operator_tx_id |
| pspTransactionId | psp_transaction_id |
| status | status |
| amount | amount |
| commission | commission |
| transactionType | transaction_type |
| qrLinkHash | qr_link_hash |
| qrTransactionId | qr_tx_id |
| merchantProvider | merchant_provider |
| receiptId | request_hash (or separate field) |

---

## Security Notes

1. **JWS signature (H-HASH)**: All requests to/from operator are signed with RSA 2048 (H-SIGNING-VERSION=2)
2. **JWE encryption**: Request body can be encrypted with RSA-OAEP-256 + A256GCM
3. **mTLS**: Mutual TLS authentication with operator
4. **PII masking**: `beneficiaryName` is always masked in responses
5. **Validation**: Jakarta Bean Validation on all input DTOs

---

## See Also

- [API Endpoints Reference](endpoints-reference.md) - Complete API endpoints reference
- [Security](../security/crypto.md) - JWS/JWE specifications
- [Data Schema](../data/schema.md) - Database schema
- [PRD](../product/PRD.md) - Business requirements

