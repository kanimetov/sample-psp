# API Endpoints Reference

> **Single Source of Truth** for all PSP API endpoints. All other documents should reference this file.

## Overview

This document provides the definitive list of all API endpoints in the PSP system. The PSP system has **three communication directions**:

1. **Incoming (Operator → PSP)** - handled by `IncomingController.java`
2. **Outgoing (PSP → Operator)** - handled by `OperatorClient.java`
3. **Merchant (Merchant → PSP)** - handled by `MerchantController.java`

**Note:** PSP acts as an intermediary between Operators, Merchants, and bank systems.

## Endpoint Categories

### 1. Incoming (Operator → PSP)

**Implementation:** `IncomingController.java`  
**Base URL:** `/in/qr/{version}/tx`

| Operation | Method | Endpoint | Description |
|-----------|--------|----------|-------------|
| Check | POST | `/in/qr/{version}/tx/check` | QR code verification |
| Create | POST | `/in/qr/{version}/tx/create` | Transaction creation |
| Execute | POST | `/in/qr/{version}/tx/execute/{transactionId}` | Transaction execution |
| Update | POST | `/in/qr/{version}/tx/update/{transactionId}` | Status update |

**Security:** All endpoints require `H-HASH` (JWS v2 signature)

---

### 2. Merchant (Merchant → PSP)

**Implementation:** `MerchantController.java`  
**Base URL:** `/merchant/qr/{version}`

| Operation | Method | Endpoint | Description |
|-----------|--------|----------|-------------|
| Check | POST | `/merchant/qr/{version}/check` | QR code verification for merchants |
| Make Payment | POST | `/merchant/qr/{version}/makePayment` | Payment processing for merchants |

**Security:** No signature verification required (public merchant API)

---

### 3. Outgoing (PSP → Operator)

**Implementation:** `OperatorClient.java`  
**Base URL:** `/psp/api/v1/payment/qr/{version}/tx`

| Operation | Method | Endpoint | Description |
|-----------|--------|----------|-------------|
| Check | POST | `/psp/api/v1/payment/qr/{version}/tx/check` | QR code verification |
| Create | POST | `/psp/api/v1/payment/qr/{version}/tx/create` | Transaction creation |
| Execute | POST | `/psp/api/v1/payment/qr/{version}/tx/execute/{transactionId}` | Transaction execution |
| Get | GET | `/psp/api/v1/payment/qr/{version}/tx/get/{transactionId}` | Status retrieval |
| Update | POST | `/psp/api/v1/payment/qr/{version}/tx/update/{transactionId}` | Status update |

**Security:** All endpoints require:
- `H-PSP-TOKEN` (PSP authentication token)
- `H-PSP-ID` (PSP identifier)
- `H-SIGNING-VERSION: "2"` (signature version)
- `H-HASH` (JWS v2 signature)

---

## Path Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `version` | String | Yes | QR protocol version | `"1"` |
| `transactionId` | String (UUID) | Yes | Transaction identifier | `"fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7"` |

## Versioning Policy

- **Incoming/Outgoing APIs:** Version parameter required in path (`{version}`)
- **External APIs:** No version parameter (stable contract)
- **Breaking changes:** New version path/parameters
- **Minor changes:** Backward compatible

## Security Headers

### For Incoming APIs (Operator → PSP)
```
H-HASH: <JWS v2 signature of request body>
```

### For Outgoing APIs (PSP → Operator)
```
H-PSP-TOKEN: <PSP authentication token>
H-PSP-ID: <PSP identifier>
H-SIGNING-VERSION: "2"
H-HASH: <JWS v2 signature of request body>
```

### For Merchant APIs (Merchant → PSP)
```
Content-Type: application/json
Accept: application/json
```

## Request/Response DTOs

### Incoming APIs (Operator → PSP)
| Endpoint | Request DTO | Response DTO |
|----------|-------------|--------------|
| Check | `CheckRequestDto` | `CheckResponseDto` |
| Create | `CreateRequestDto` | `CreateResponseDto` |
| Execute | `(empty)` | `StatusDto` |
| Get | `(none)` | `StatusDto` |
| Update | `UpdateDto` | `ACK (200 OK)` |

### Merchant APIs (Merchant → PSP)
| Endpoint | Request DTO | Response DTO |
|----------|-------------|--------------|
| Check | `ClientCheckRequestDto` | `ClientCheckResponseDto` |
| Make Payment | `ClientMakePaymentRequestDto` | `ClientMakePaymentResponseDto` |


## Related Documentation

- [DTO Mapping](dto-mapping.md) - Detailed DTO specifications
- [Security](../security/crypto.md) - JWS/JWE specifications
- [Error Catalog](error-catalog.md) - Error codes and handling

---

**Last Updated:** 2024-01-XX  
**Version:** 1.0  
**Maintainer:** PSP Team
