## API контракты (сводка)

### Входящие (Operator → PSP, Beneficiary) - РЕАЛИЗОВАНО
- POST /in/qr/{version}/tx/check
- POST /in/qr/{version}/tx/create
- POST /in/qr/{version}/tx/execute/{transactionId}
- POST /in/qr/{version}/tx/update/{transactionId}

### Внешние (клиенты → PSP) - ПЛАНИРУЕТСЯ
- POST /api/qr/tx/check
- POST /api/qr/tx/makePayment
- GET  /api/qr/tx/{transactionId}

### Исходящие (PSP → Operator) - ПЛАНИРУЕТСЯ
- POST /psp/api/v1/payment/qr/{version}/tx/check
- POST /psp/api/v1/payment/qr/{version}/tx/create  
- POST /psp/api/v1/payment/qr/{version}/tx/execute/{transactionId}
- POST /psp/api/v1/payment/qr/{version}/tx/update/{transactionId}

### Заголовки безопасности
- H‑HASH (JWS v2 подпись payload) - обязательный для всех входящих запросов
- H‑PSP‑TOKEN, H‑PSP‑ID, H‑SIGNING‑VERSION=2 - для исходящих запросов к оператору
- POST‑тела — JWE при необходимости


