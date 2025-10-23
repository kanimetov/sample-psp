# RSA Signature Verification Setup

This document explains how to set up RSA signature verification for the PSP service.

## Overview

The PSP service now supports RSA SHA-256 signature verification for both incoming and outgoing requests:

- **Incoming requests** (`/in/qr/**`): Verified using operator's public key
- **Outgoing requests** (to operator): Signed using PSP's private key

## Configuration

### 1. Update application.yml

Set the paths to your PEM key files:

```yaml
security:
  signature:
    operator-public-key-path: /path/to/operator-public-key.pem
    psp-private-key-path: /path/to/psp-private-key.pem
    enabled: true
```

### 2. Generate RSA Key Pair

If you need to generate test keys:

```bash
# Generate private key
openssl genrsa -out psp-private-key.pem 2048

# Generate public key from private key
openssl rsa -in psp-private-key.pem -pubout -out psp-public-key.pem

# Generate operator's public key (for testing)
openssl genrsa -out operator-private-key.pem 2048
openssl rsa -in operator-private-key.pem -pubout -out operator-public-key.pem
```

### 3. Key File Format

Keys must be in PEM format:

**Private Key:**
```
-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC...
-----END PRIVATE KEY-----
```

**Public Key:**
```
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...
-----END PUBLIC KEY-----
```

## How It Works

### Incoming Request Verification

1. All requests to `/in/qr/**` are intercepted by `SignatureVerificationFilter`
2. The `H-HASH` header is extracted - **MANDATORY**
3. For POST requests: raw request body is read and hashed with SHA-256
4. For GET/DELETE requests: URI is hashed with SHA-256
5. The signature is verified using the operator's public key
6. **Any missing or invalid signature results in 403 Forbidden response**

### Outgoing Request Signing

1. All outgoing requests to the operator are intercepted by `SignatureInterceptor`
2. **Current implementation**: All requests (POST/GET/DELETE) are signed using URI
3. **Note**: Full body signing for POST requests requires more complex WebClient implementation
4. The URI is hashed with SHA-256 and signed using PSP's private key
5. The signature is added to the `H-HASH` header
6. **Signature generation failure results in request rejection**

### Signature Algorithm

- **Incoming requests**: SHA-256 of raw body bytes (POST) or URI (GET/DELETE)
- **Outgoing requests**: SHA-256 of URI (all request types)
- **Signing**: RSA with SHA-256 (SHA256withRSA)
- **Encoding**: Base64

## Testing

### Test with curl

```bash
# Test incoming POST request (you'll need a valid signature)
curl -X POST http://localhost:8080/in/qr/v1/tx/check \
  -H "Content-Type: application/json" \
  -H "H-HASH: your-signature-here" \
  -d '{"qrType":"staticQr","merchantProvider":"test",...}'

# Test incoming GET request (you'll need a valid signature for URI)
curl -X GET "http://localhost:8080/in/qr/v1/tx/status/123" \
  -H "H-HASH: your-uri-signature-here"
```

### Disable Signature Verification

To disable signature verification for testing:

```yaml
security:
  signature:
    enabled: false
```

## Security Notes

1. **Key Storage**: Store keys outside the repository with proper file permissions (600)
2. **Key Rotation**: Implement regular key rotation procedures
3. **Monitoring**: Monitor signature verification failures
4. **Logging**: Sensitive data is not logged in signature verification

## Troubleshooting

### Common Issues

1. **Key Loading Errors**: Check file paths and permissions
2. **Signature Verification Failures**: Ensure keys match between PSP and operator
3. **URI Signing**: GET/DELETE requests use URI for signature instead of body
4. **Cache Issues**: Keys are cached for 24 hours by default
5. **Missing Signatures**: All requests MUST include valid H-HASH header
6. **Invalid Data**: Requests without body or URI are rejected

### Logs to Check

- Key loading: `Loading operator public key from: ...`
- Signature verification: `Signature verification successful/failed`
- Cache hits: Spring cache logs
