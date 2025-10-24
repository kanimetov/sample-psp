# PSP Service Architecture

## Merchant Provider Routing

The PSP service now supports routing transactions to different providers based on the `merchantProvider` configuration. The routing logic is integrated directly into the `ClientService`.

### Architecture Overview

```
ClientController
    ↓
ClientServiceImpl (with routing logic)
    ↓
┌─────────────────┬─────────────────┐
│   BankService   │ OperatorService │
│                 │                 │
│  BankClientImpl │OperatorClientImpl│
└─────────────────┴─────────────────┘
```

### Routing Logic

The `ClientServiceImpl` determines which service to use based on the `merchantProvider` from the QR data:

- **merchantProvider = "demirbank"** → Uses `BankService` (internal bank operations)
- **merchantProvider = any other value** → Uses `OperatorService` (external operator operations)

### Services Structure

1. **ClientService** - Main service with routing logic based on merchant provider
2. **BankService** - Handles full client operations using bank client
3. **OperatorService** - Handles full client operations using operator client

### Configuration

The routing is controlled by the `merchant.provider` configuration in `application.yml`:

```yaml
merchant:
  provider: "demirbank"  # Provider identifier for direction determination
  # Supported providers:
  # - "demirbank" - Uses internal bank client for transactions
  # - Any other value - Uses external operator client for transactions
```

### Implementation Details

- **ClientService** decodes QR data and routes to appropriate service
- **BankService** implements full client operations using `BankClient` and `OperationRepository`
- **OperatorService** implements full client operations using `OperatorClient` and `OperationRepository`
- Each service handles its own operation entity creation, session lookup, and database management
- Services internally determine if they should handle the operation based on merchant provider
- Unified response format across all services

### Flow Details

#### QR Check Flow:
1. `ClientService` receives QR check request
2. Decodes QR to get `merchantProvider`
3. Routes to appropriate service based on `merchantProvider`
4. Service creates operation entity with generated `paymentSessionId`
5. Returns response with `paymentSessionId` for linking to payment

#### Payment Flow:
1. `ClientService` receives payment request with required `paymentSessionId`
2. Tries `BankService` first, which:
   - Looks up operation by `paymentSessionId` in `OperationRepository`
   - Checks if `merchantProvider` is "demirbank"
   - If yes, processes payment; if no, returns error
3. If `BankService` fails, tries `OperatorService`, which:
   - Looks up operation by `paymentSessionId` in `OperationRepository`
   - Checks if `merchantProvider` is NOT "demirbank"
   - If yes, processes payment; if no, returns error

### Payment Session ID

The system now uses a dedicated `paymentSessionId` for linking check and payment operations:

- **Generated during CHECK**: Each check operation generates a unique `paymentSessionId`
- **Required for PAYMENT**: The `paymentSessionId` is now required in payment requests
- **Stored in Database**: The `paymentSessionId` is stored in the `operations` table
- **Used for Lookup**: Services use `paymentSessionId` to find the original check operation
- **Unique Constraint**: Ensures each payment session is unique across the system

### Benefits

- **Unified Interface**: Same endpoints for all providers
- **Repository Encapsulation**: Each service manages its own database operations
- **Self-Determining Services**: Services decide if they should handle the operation
- **Payment Session Tracking**: Dedicated session ID for linking check and payment operations
- **Flexibility**: Easy to switch between bank and operator processing
- **Scalability**: Can add more providers in the future
- **Maintainability**: Clear separation of concerns with routing in ClientService
- **Configuration-driven**: No code changes needed to switch providers
