## Flows

### Beneficiary Flow (Incoming)

**Incoming requests from operator:**
1. **Check**: `POST /in/qr/{version}/tx/check` → signature verification → validation → business logic → `IncomingCheckResponseDto`
2. **Create**: `POST /in/qr/{version}/tx/create` → signature verification → validation → transaction creation → `IncomingTransactionResponseDto`
3. **Execute**: `POST /in/qr/{version}/tx/execute/{id}` → signature verification → transaction execution → `IncomingTransactionResponseDto`
4. **Update**: `POST /in/qr/{version}/tx/update/{id}` → signature verification → status update → ACK (200 OK)

### Sender Flow (Outgoing)
Scan QR → POST check → POST makePayment → (inbound UPDATE) → done; otherwise GET status.

### Beneficiary Flow (Extension)
Inbound POST check/create/execute → processing → outgoing POST update when final status is missing.

### Exception Handling

**Implementation:**
1. **Signature verification** → `SignatureVerificationException` on error
2. **DTO validation** → `ValidationException` on incorrect data
3. **Business logic** → specific exceptions (`BadRequestException`, `ResourceNotFoundException`, etc.)
4. **All exceptions** → `GlobalExceptionHandler` → standardized `ErrorResponseDto`

**Network error handling:**
1. HTTP errors (400-524) → mapping by status code → corresponding PspException
2. Transport errors → type detection → NetworkTimeoutException (504) / NetworkConnectionException (503) / NetworkException (502)
3. Client receives HTTP status + JSON with code/message/details/timestamp/path


