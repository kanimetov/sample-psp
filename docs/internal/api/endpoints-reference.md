# API Endpoints Reference

> **Single Source of Truth** for all PSP API endpoints. All other documents should reference this file.

## Overview

This document provides the definitive list of all API endpoints in the PSP system. The PSP system has only **two communication directions**:

1. **Incoming (Operator → PSP)** - handled by `IncomingController.java`
2. **Outgoing (PSP → Operator)** - handled by `OperatorClient.java`

**Note:** PSP does not expose direct client-facing APIs. It acts as an intermediary between the Operator and bank systems.

## Endpoint Categories

### 1. Incoming (Operator → PSP) - ✅ IMPLEMENTED

**Implementation:** `IncomingController.java`  
**Base URL:** `/in/qr/{version}/tx`

| Operation | Method | Endpoint | Status | Description |
|-----------|--------|----------|--------|-------------|
| Check | POST | `/in/qr/{version}/tx/check` | ✅ | QR code verification |
| Create | POST | `/in/qr/{version}/tx/create` | ✅ | Transaction creation |
| Execute | POST | `/in/qr/{version}/tx/execute/{transactionId}` | ✅ | Transaction execution |
| Update | POST | `/in/qr/{version}/tx/update/{transactionId}` | ✅ | Status update |

**Security:** All endpoints require `H-HASH` (JWS v2 signature)

---

### 2. Outgoing (PSP → Operator) - 🔄 PLANNED

**Implementation:** `OperatorClient.java`  
**Base URL:** `/psp/api/v1/payment/qr/{version}/tx`

| Operation | Method | Endpoint | Status | Description |
|-----------|--------|----------|--------|-------------|
| Check | POST | `/psp/api/v1/payment/qr/{version}/tx/check` | 🔄 | QR code verification |
| Create | POST | `/psp/api/v1/payment/qr/{version}/tx/create` | 🔄 | Transaction creation |
| Execute | POST | `/psp/api/v1/payment/qr/{version}/tx/execute/{transactionId}` | 🔄 | Transaction execution |
| Get | GET | `/psp/api/v1/payment/qr/{version}/tx/get/{transactionId}` | 🔄 | Status retrieval |
| Update | POST | `/psp/api/v1/payment/qr/{version}/tx/update/{transactionId}` | 🔄 | Status update |

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

### For External APIs (Client → PSP)
```
Content-Type: application/json
Accept: application/json
```

## Request/Response DTOs

| Endpoint | Request DTO | Response DTO |
|----------|-------------|--------------|
| Check | `CheckRequestDto` | `CheckResponseDto` |
| Create | `CreateRequestDto` | `CreateResponseDto` |
| Execute | `(empty)` | `StatusDto` |
| Get | `(none)` | `StatusDto` |
| Update | `UpdateDto` | `ACK (200 OK)` |

## Implementation Status

- ✅ **IMPLEMENTED:** Incoming APIs (Operator → PSP) - `IncomingController.java`
- 🔄 **PLANNED:** Outgoing APIs (PSP → Operator) - `OperatorClient.java`
- ❌ **NOT APPLICABLE:** External APIs (Client → PSP) - PSP acts as intermediary only

## Related Documentation

- [DTO Mapping](dto-mapping.md) - Detailed DTO specifications
- [Security](../security/crypto.md) - JWS/JWE specifications
- [Error Catalog](error-catalog.md) - Error codes and handling

---

**Last Updated:** 2024-01-XX  
**Version:** 1.0  
**Maintainer:** PSP Team
