## Потоки

### Текущая реализация (Beneficiary)

**Входящие запросы от оператора:**
1. **Check**: `POST /in/qr/{version}/tx/check` → верификация подписи → валидация → бизнес-логика → `CheckResponseDto`
2. **Create**: `POST /in/qr/{version}/tx/create` → верификация подписи → валидация → создание транзакции → `CreateResponseDto`
3. **Execute**: `POST /in/qr/{version}/tx/execute/{id}` → верификация подписи → выполнение транзакции → `StatusDto`
4. **Update**: `POST /in/qr/{version}/tx/update/{id}` → верификация подписи → обновление статуса → ACK (200 OK)

### Планируемые потоки

### Sender (планируется)
Scan QR → POST check → POST create → POST execute → (inbound UPDATE) → done; иначе GET статус.

### Beneficiary (расширение)
Inbound POST check/create/execute → обработка → исходящий POST update при отсутствии финала.

### Обработка исключений

**Текущая реализация:**
1. **Верификация подписи** → `SignatureVerificationException` при ошибке
2. **Валидация DTO** → `ValidationException` при некорректных данных
3. **Бизнес-логика** → специфичные исключения (`BadRequestException`, `ResourceNotFoundException`, etc.)
4. **Все исключения** → `GlobalExceptionHandler` → стандартизированный `ErrorResponseDto`

**Планируемая обработка сетевых ошибок:**
1. HTTP ошибки (400-524) → маппинг по статус-коду → соответствующие PspException
2. Транспортные ошибки → детекция типа → NetworkTimeoutException (504) / NetworkConnectionException (503) / NetworkException (502)
3. Клиент получает HTTP статус + JSON с code/message/details/timestamp/path


