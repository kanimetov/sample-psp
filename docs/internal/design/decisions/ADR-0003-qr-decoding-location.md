# ADR-0003: QR Decoding Location

## Status
Accepted - 2024-10-28

## Context
The QR decoding functionality (`decodeQrUri`) was being called multiple times in the service layer:
- `MerchantServiceImpl.checkQrPayment()` decoded QR once
- `BankServiceImpl.checkQrPayment()` decoded QR again (duplicate)
- `OperatorServiceImpl.checkQrPayment()` decoded QR again (duplicate)

This created performance issues and unnecessary processing overhead since QR codes were being decoded multiple times for the same request.

## Decision
Move QR decoding to the `MerchantController` level (entry point) so it happens only once per request.

### Architecture Change
**Before:**
```
MerchantController → MerchantService → QrDecoderClient
                                  ↓
                        → BankService → QrDecoderClient (duplicate)
                        → OperatorService → QrDecoderClient (duplicate)
```

**After:**
```
MerchantController → QrDecoderClient (once)
                    ↓
              MerchantService (receives decoded ELQRData)
                    ↓
            → BankService (receives decoded ELQRData)
            → OperatorService (receives decoded ELQRData)
```

### Implementation Details
1. **MerchantController**: Now calls `qrDecoderClient.decodeQrUri()` and passes decoded `ELQRData` to services
2. **Service Interfaces**: Updated to accept `ELQRData` parameter instead of decoding themselves
3. **Service Implementations**: Receive pre-decoded data and process it
4. **Dependencies**: Removed `QrDecoderClient` from `BankServiceImpl` and `OperatorServiceImpl`

## Consequences

### Positive
- ✅ Eliminated duplicate QR decoding (performance improvement)
- ✅ Clearer separation of concerns (controller handles input parsing)
- ✅ Reduced processing overhead
- ✅ Simpler service implementations
- ✅ Better architectural alignment with layered architecture

### Trade-offs
- Service interfaces changed (method signatures updated)
- Services now depend on `ELQRData` type being passed from controller
- Less flexibility for services to decode QR independently

## Implementation
- Modified: `MerchantController.java`
- Modified: `MerchantService.java` (interface)
- Modified: `BankService.java` (interface)
- Modified: `OperatorService.java` (interface)
- Modified: `MerchantServiceImpl.java`
- Modified: `BankServiceImpl.java`
- Modified: `OperatorServiceImpl.java`

## Related ADRs
- None

## Notes
This decision aligns with the layered architecture principle where:
- **Controllers** handle input/output parsing and validation
- **Services** handle business logic and coordination
- **Clients** handle external service interactions

The QR decoding is clearly an input parsing activity, making it appropriate for the controller layer.
