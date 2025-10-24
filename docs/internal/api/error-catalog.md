## Error Catalog

The PSP service exceptions are organized into logical groups based on their functionality and HTTP status codes.

### Validation Exceptions (`kg.demirbank.psp.exception.validation`)

**400 Bad Request**
- `BadRequestException` — Invalid or malformed request
- `ValidationException` — DTO validation failures

**452-456 Custom Validation Errors**
- `RecipientDataIncorrectException` (452) — Recipient data is incorrect
- `IncorrectRequestDataException` (454) — Incorrect data in request
- `MinAmountNotValidException` (455) — Minimum amount validation failed
- `MaxAmountNotValidException` (456) — Maximum amount validation failed

### Business Logic Exceptions (`kg.demirbank.psp.exception.business`)

**404 Not Found**
- `ResourceNotFoundException` — Requested resource does not exist

**422 Unprocessable Entity**
- `UnprocessableEntityException` — Well-formed but invalid data

### Security Exceptions (`kg.demirbank.psp.exception.security`)

**403 Forbidden**
- `SignatureVerificationException` — Signature verification failed

**453 Access Denied**
- `AccessDeniedException` — Access to the system is denied

### Network/Infrastructure Exceptions (`kg.demirbank.psp.exception.network`)

**500 Internal Server Error**
- `SystemErrorException` — Internal system error

**502 Bad Gateway**
- `NetworkException` — Generic network error (SSL/Socket/Unknown host)

**503 Service Unavailable**
- `NetworkConnectionException` — Connection error (connection refused/reset/no route)

**504 Gateway Timeout**
- `NetworkTimeoutException` — Request timeout (connect/read/write/response)

**523-524 Custom Network Errors**
- `SupplierNotAvailableException` (523) — Supplier not available
- `ExternalServerNotAvailableException` (524) — External server not available

### Error Mapping Notes

- HTTP errors returned by the operator are mapped directly by status code (400/404/422/452–456/500/523/524)
- Transport errors (timeouts, connection breaks, SSL, DNS, etc.) are mapped to 504/503/502 respectively
- All exceptions extend the base `PspException` class which provides HTTP status and error code information
- Business exceptions (validation, business logic, security) typically don't log stack traces
- Network exceptions may include stack traces for debugging infrastructure issues


