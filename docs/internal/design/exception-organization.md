# Exception Organization

## Overview

The PSP service exceptions are organized into logical groups based on their functionality and HTTP status codes. This organization improves code maintainability, makes exception handling more predictable, and provides clear separation of concerns.

## Package Structure

```
kg.demirbank.psp.exception/
├── PspException.java (base class)
├── validation/
│   ├── ValidationException.java
│   ├── BadRequestException.java
│   ├── IncorrectRequestDataException.java
│   ├── MinAmountNotValidException.java
│   ├── MaxAmountNotValidException.java
│   └── RecipientDataIncorrectException.java
├── network/
│   ├── NetworkException.java
│   ├── NetworkConnectionException.java
│   ├── NetworkTimeoutException.java
│   ├── ExternalServerNotAvailableException.java
│   ├── SupplierNotAvailableException.java
│   └── SystemErrorException.java
├── security/
│   ├── AccessDeniedException.java
│   └── SignatureVerificationException.java
└── business/
    ├── ResourceNotFoundException.java
    └── UnprocessableEntityException.java
```

## Exception Groups

### 1. Validation Exceptions (`validation` package)

**Purpose:** Handle input validation errors and malformed requests.

**HTTP Status Range:** 400-456

**Exceptions:**
- `ValidationException` (400) - General DTO validation failures
- `BadRequestException` (400) - Invalid or malformed requests
- `IncorrectRequestDataException` (454) - Incorrect data in request
- `MinAmountNotValidException` (455) - Minimum amount validation failed
- `MaxAmountNotValidException` (456) - Maximum amount validation failed
- `RecipientDataIncorrectException` (452) - Recipient data is incorrect

**Usage:** Thrown during request validation, DTO processing, and business rule validation.

### 2. Network/Infrastructure Exceptions (`network` package)

**Purpose:** Handle network communication errors and system infrastructure issues.

**HTTP Status Range:** 500-524

**Exceptions:**
- `NetworkException` (502) - Generic network errors (SSL/Socket/Unknown host)
- `NetworkConnectionException` (503) - Connection failures (connection refused/reset/no route)
- `NetworkTimeoutException` (504) - Request timeouts (connect/read/write/response)
- `ExternalServerNotAvailableException` (524) - External server unavailable
- `SupplierNotAvailableException` (523) - Supplier unavailable
- `SystemErrorException` (500) - Internal system errors

**Usage:** Thrown during external service communication, network failures, and system errors.

### 3. Security Exceptions (`security` package)

**Purpose:** Handle security-related errors and access control issues.

**HTTP Status Range:** 403-453

**Exceptions:**
- `AccessDeniedException` (453) - Access to the system is denied
- `SignatureVerificationException` (403) - Signature verification failed

**Usage:** Thrown during authentication, authorization, and signature verification.

### 4. Business Logic Exceptions (`business` package)

**Purpose:** Handle business rule violations and domain-specific errors.

**HTTP Status Range:** 404-422

**Exceptions:**
- `ResourceNotFoundException` (404) - Requested resource does not exist
- `UnprocessableEntityException` (422) - Well-formed but invalid data

**Usage:** Thrown when business rules are violated or resources cannot be found.

## Base Exception Class

All exceptions extend `PspException` which provides:

```java
public class PspException extends RuntimeException {
    private final HttpStatus status;
    private final Integer code;
    
    public PspException(String message, HttpStatus status, Integer code);
    public PspException(String message, Throwable cause, HttpStatus status, Integer code);
}
```

**Benefits:**
- Consistent error structure across all exceptions
- HTTP status code mapping
- Error code for client applications
- Support for cause chaining

## Import Guidelines

### For Service Classes

Use specific imports for better clarity:

```java
import kg.demirbank.psp.exception.PspException;
import kg.demirbank.psp.exception.validation.BadRequestException;
import kg.demirbank.psp.exception.business.ResourceNotFoundException;
import kg.demirbank.psp.exception.network.SystemErrorException;
```

### For Client Classes

Use wildcard imports for comprehensive error handling:

```java
import kg.demirbank.psp.exception.validation.*;
import kg.demirbank.psp.exception.business.*;
import kg.demirbank.psp.exception.security.*;
import kg.demirbank.psp.exception.network.*;
```

## Error Handling Patterns

### 1. Service Layer Error Handling

```java
.onErrorMap(throwable -> {
    if (throwable instanceof PspException) {
        return throwable; // Preserve original PspException
    }
    log.error("Unexpected error: {}", throwable.getMessage(), throwable);
    return new SystemErrorException("Failed to process request", throwable);
});
```

### 2. Client Layer Error Mapping

```java
private Throwable mapOperatorError(Throwable error) {
    if (error instanceof WebClientResponseException) {
        WebClientResponseException ex = (WebClientResponseException) error;
        switch (ex.getStatusCode().value()) {
            case 400:
                return new BadRequestException("Invalid request");
            case 404:
                return new ResourceNotFoundException("Resource not found");
            // ... other mappings
        }
    }
    // Network error detection and mapping
    return new SystemErrorException("Unexpected error", error);
}
```

### 3. Logging Considerations

```java
public static boolean isBusinessException(Throwable throwable) {
    return throwable instanceof kg.demirbank.psp.exception.validation.ValidationException ||
           throwable instanceof kg.demirbank.psp.exception.business.ResourceNotFoundException ||
           // ... other business exceptions
           // Business exceptions typically don't log stack traces
}
```

## Benefits of This Organization

### 1. **Improved Maintainability**
- Clear separation of concerns
- Easy to locate and modify specific exception types
- Reduced coupling between different error handling concerns

### 2. **Better Code Organization**
- Logical grouping by functionality
- Consistent naming conventions
- Easier to understand exception hierarchy

### 3. **Enhanced Error Handling**
- Predictable exception types for different scenarios
- Consistent error response structure
- Better error categorization for monitoring and alerting

### 4. **Simplified Testing**
- Easier to mock specific exception types
- Clear test scenarios for different error categories
- Better test coverage of error handling paths

### 5. **Improved Documentation**
- Self-documenting package structure
- Clear error catalog with package references
- Better API documentation for error responses

## Migration Notes

When adding new exceptions:

1. **Choose the appropriate package** based on the exception's purpose
2. **Extend PspException** with appropriate HTTP status and error code
3. **Update import statements** in files that use the exception
4. **Update documentation** including error catalog and this guide
5. **Add tests** for the new exception type

## Related Documentation

- [Error Catalog](../api/error-catalog.md) - Complete list of all exceptions with HTTP status codes
- [Network Exception Handling](../reliability/network-exceptions.md) - Detailed network error handling
- [Structured Logging](../runtime/structured-logging.md) - Logging patterns for different exception types
