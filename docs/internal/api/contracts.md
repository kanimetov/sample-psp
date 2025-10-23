## API контракты (сводка)

### Внешние (PSP → клиенты)
- POST /api/qr/tx/check
- POST /api/qr/tx/create
- POST /api/qr/tx/execute/{transactionId}
- GET  /api/qr/tx/{transactionId}

### Исходящие (PSP → Operator) - Mock для тестирования
- POST /ipc/operator/api/v1/payment/qr/{version}/tx/check
- POST /ipc/operator/api/v1/payment/qr/{version}/tx/create
- POST /ipc/operator/api/v1/payment/qr/{version}/tx/execute/{transactionId}

### Входящие (Operator → PSP, Beneficiary)
- POST /in/qr/{version}/tx/check
- POST /in/qr/{version}/tx/create
- POST /in/qr/{version}/tx/execute/{transactionId}
- POST /in/qr/{version}/tx/update/{transactionId}

Заголовки безопасности: H‑PSP‑TOKEN, H‑PSP‑ID, H‑HASH (JWS v2), H‑SIGNING‑VERSION=2. POST‑тела — JWE при необходимости.


