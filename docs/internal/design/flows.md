## Потоки

### Sender
Scan QR → POST check → POST create → POST execute → (inbound UPDATE) → done; иначе GET статус.

### Beneficiary
Inbound POST check/create/execute → обработка → исходящий POST update при отсутствии финала.

### Network Exception Handling
При вызове оператора через OperatorService:
1. HTTP ошибки (400-524) → маппинг по статус-коду → соответствующие PspException
2. Транспортные ошибки → детекция типа → NetworkTimeoutException (504) / NetworkConnectionException (503) / NetworkException (502)
3. Все исключения → GlobalExceptionHandler → стандартизированный ErrorResponseDto
4. Клиент получает HTTP статус + JSON с code/message/details/timestamp/path


