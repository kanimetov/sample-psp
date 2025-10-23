# DTO спецификация

> **Важно:** Полное описание всех эндпоинтов и DTOs см. в [dto-mapping.md](dto-mapping.md)

Все строки UTF‑8; числа — целые, если не оговорено. Суммы в тыйын (minor units).

## Структура пакета DTO

```java
kg.demirbank.psp.dto/
├── KeyValueDto.java       - Вспомогательный DTO для extra полей
├── CheckRequestDto.java   - Request для check операции
├── CreateRequestDto.java  - Request для create операции
├── CheckResponseDto.java  - Response для check операции
├── CreateResponseDto.java - Response для create операции
├── StatusDto.java         - Универсальный DTO для execute/get responses
└── UpdateDto.java         - Bidirectional DTO для update операций
```

## Common headers (исходящие к Оператору)

- H-PSP-TOKEN: string (required)
- H-PSP-ID: string (required)
- H-SIGNING-VERSION: "2" (required)
- H-HASH: string (JWS v2 подпись payload) (required)

## Краткое описание DTOs

### CheckRequestDto

**Используется в:**
- `POST /ipc/operator/api/v1/payment/qr/{version}/tx/check` (исходящий)
- `POST /in/qr/{version}/tx/check` (входящий от оператора)
- `POST /api/qr/tx/check` (внешний API для клиентов)

**Поля:**
- qrType: string, enum ["staticQr","dynamicQr"] (required)
- merchantProvider: string, max 32 (required)
- merchantId: string, max 32
- serviceId: string, max 32
- serviceName: string, max 32
- beneficiaryAccountNumber: string, max 32
- merchantCode: integer, 0-9999 (required)
- currencyCode: string, 3 digits, default "417" (required)
- qrTransactionId: string, max 32
- qrComment: string, max 32
- customerType: string, enum ["1","2"] (required) - 1=Individual, 2=Corporate
- amount: integer, max 13 digits, positive (required)
- qrLinkHash: string, 4 alphanumeric chars (required)
- extra: Array<KeyValueDto>, max 5

### CreateRequestDto

**Используется в:**
- `POST /ipc/operator/api/v1/payment/qr/{version}/tx/create` (исходящий)
- `POST /in/qr/{version}/tx/create` (входящий от оператора)
- `POST /api/qr/tx/create` (внешний API)

**Поля:** Все поля из CheckRequestDto плюс:
- transactionId: string, max 32 (required) - ID транзакции (operator's transaction ID)
- pspTransactionId: string, max 50 (required) - ID транзакции в PSP
- receiptId: string, max 20 (required) - ID чека
- transactionType: CustomerType enum (required) - Тип транзакции

### CheckResponseDto

**Возвращается из:**
- Check операций (все направления)

**Поля:**
- beneficiaryName: string (masked, например "c***e A***o")
- transactionType: CustomerType - Тип транзакции (enum, nullable)

### CreateResponse

**Возвращается из:**
- Create операций (все направления)

**Поля:**
- transactionId: string (UUID) - ID транзакции от оператора
- status: integer - Статус транзакции
- transactionType: CustomerType - Тип транзакции (enum)
- amount: integer - Сумма транзакции
- beneficiaryName: string - Имя получателя (masked)
- customerType: integer - Тип клиента (1=Individual, 2=Corporate)
- receiptId: string - ID чека
- createdDate: string (ISO8601) - Дата создания
- executedDate: string (ISO8601) - Дата выполнения (может быть пустой)

### StatusDto

**Используется в:**
- Execute responses (все направления)
- Get responses (все направления)

**Поля:**
- transactionId: string (UUID)
- status: Status - enum статус транзакции (nullable)
- transactionType: CustomerType - enum тип транзакции (nullable)
- amount: Long - сумма в тыйынах
- beneficiaryName: string (nullable)
- customerType: string (nullable)
- receiptId: string (nullable)
- createdDate: string (ISO8601)
- executedDate: string (ISO8601, nullable)

### UpdateDto

**Используется в:**
- `POST /in/qr/{version}/tx/update/{transactionId}` (входящий от оператора)

**Поля:**
- status: integer (required) - Новый статус транзакции
- updateDate: string, max 30 - Дата обновления

### KeyValueDto

**Вспомогательный тип для extra полей**

**Поля:**
- key: string, max 64 (required)
- value: string, max 256 (required)

## Маппинг эндпоинтов

### Исходящие к Оператору (PSP → Operator)

| Операция | Endpoint | Request DTO | Response DTO |
|----------|----------|-------------|--------------|
| Check | POST /ipc/operator/.../tx/check | CheckRequestDto | CheckResponseDto |
| Create | POST /ipc/operator/.../tx/create | CreateRequestDto | CreateResponseDto |
| Execute | POST /ipc/operator/.../tx/execute/{id} | (empty) | StatusDto |

### Входящие от Оператора (Operator → PSP)

| Операция | Endpoint | Request DTO | Response DTO |
|----------|----------|-------------|--------------|
| Check | POST /in/qr/{v}/tx/check | CheckRequestDto | CheckResponseDto |
| Create | POST /in/qr/{v}/tx/create | CreateRequestDto | CreateResponseDto |
| Execute | POST /in/qr/{v}/tx/execute/{id} | (empty) | StatusDto |
| Update | POST /in/qr/{v}/tx/update/{id} | UpdateDto | ACK |

### Внешние (Client → PSP)

| Операция | Endpoint | Request DTO | Response DTO |
|----------|----------|-------------|--------------|
| Check | POST /api/qr/tx/check | CheckRequestDto | CheckResponseDto |
| Create | POST /api/qr/tx/create | CreateRequestDto | CreateResponseDto |
| Execute | POST /api/qr/tx/execute/{id} | (empty) | StatusDto |
| Get | GET /api/qr/tx/{id} | (none) | StatusDto |

## Идемпотентность

Все операции, изменяющие состояние, должны быть идемпотентными:

- **check:** Идемпотентность по `(qrLinkHash, amount, customerType)`
- **create:** Идемпотентность по `pspTransactionId`
- **execute:** Идемпотентность по `transactionId`
- **update:** Идемпотентность по `(transactionId, status)`

Реализуется через Redis с TTL ключами.

## Коды статусов (Status enum)

| Код | Название | Финальный | Описание |
|-----|----------|-----------|----------|
| 10 | CREATED | Нет | Транзакция создана |
| 20 | IN_PROCESS | Нет | Транзакция в процессе |
| 30 | ERROR | Да | Транзакция завершена с ошибкой |
| 40 | CANCELED | Да | Транзакция отменена |
| 50 | SUCCESS | Да | Транзакция успешно завершена |

## Типы транзакций (CustomerType enum)

| Код | Название | Описание |
|-----|----------|----------|
| 10 | C2C | Перевод по QR-коду/платежной ссылке |
| 20 | C2B | Покупка по QR-коду/платежной ссылке |
| 30 | C2G | Государственный платеж (физ. лицо) по QR-коду/платежной ссылке |
| 40 | B2C | Денежный перевод/вывод/возврат по QR-коду/платежной ссылке |
| 50 | B2B | Платеж/перевод по QR-коду/платежной ссылке |
| 60 | BANK_RESERVE | Электронное сообщение о постановке резерва на банк |
| 70 | B2G | Государственный платеж (юр. лицо) по QR-коду/платежной ссылке |

## См. также

- **[dto-mapping.md](dto-mapping.md)** - Полное детальное описание всех эндпоинтов с примерами JSON
- [contracts.md](contracts.md) - API контракты
- [../security/crypto.md](../security/crypto.md) - JWS/JWE спецификации
- [../data/schema.md](../data/schema.md) - Схема БД
