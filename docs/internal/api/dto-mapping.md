# DTO Mapping - Полный справочник эндпоинтов и DTOs

## Обзор

Данный документ описывает все эндпоинты PSP системы с детальным маппингом используемых DTOs для request и response.

## Структура пакета DTO

```
kg.demirbank.psp.dto/
├── KeyValueDto.java       - Вспомогательный DTO для дополнительных полей
├── CheckRequestDto.java   - Request для check операции
├── CreateRequestDto.java  - Request для create операции
├── CheckResponseDto.java  - Response для check операции
├── CreateResponseDto.java - Response для create операции
├── StatusDto.java         - Универсальный DTO для execute/get responses
└── UpdateDto.java         - Bidirectional DTO для update операций
```

---

## A. Исходящие запросы к Оператору (PSP → Operator)

**Controller:** `OutgoingController` (mock для тестирования интеграции)  
**Base URL:** `/ipc/operator/api/v1/payment/qr/{version}/tx`

### 1. Check QR реквизитов

Проверка валидности QR-кода и получение информации о бенефициаре.

```
POST /ipc/operator/api/v1/payment/qr/{version}/tx/check
```

**Headers:**
- `H-PSP-TOKEN: string` (required) - Токен PSP для аутентификации
- `H-PSP-ID: string` (required) - Идентификатор PSP
- `H-SIGNING-VERSION: "2"` (required) - Версия подписи (только v2)
- `H-HASH: string` (required) - JWS подпись тела запроса

**Path Parameters:**
- `version: string` - Версия QR протокола (обычно "1")

**Request Body:** `CheckRequestDto`
```json
{
  "qrType": "staticQr",
  "merchantProvider": "DEMO_MERCHANT",
  "merchantCode": 5411,
  "currencyCode": "417",
  "customerType": "1",
  "amount": 100000,
  "qrLinkHash": "AB12"
}
```

**Response 200:** `CheckResponseDto`
```json
{
  "beneficiaryName": "c***e A***o",
  "transactionType": null
}
```

---

### 2. Create транзакции

Создание новой транзакции в системе оператора.

```
POST /ipc/operator/api/v1/payment/qr/{version}/tx/create
```

**Headers:** (те же, что и в check)

**Request Body:** `CreateRequestDto`
```json
{
  "transactionId": "tx-12345678",
  "qrType": "staticQr",
  "merchantProvider": "DEMO_MERCHANT",
  "merchantCode": 5411,
  "currencyCode": "417",
  "customerType": "1",
  "pspTransactionId": "PSP-TX-123456",
  "receiptId": "RCP-001",
  "amount": 100000,
  "qrLinkHash": "AB12",
  "transactionType": "C2B"
}
```

**Response 200:** `CreateResponseDto`
```json
{
  "transactionId": "fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7",
  "status": null,
  "transactionType": "C2B",
  "amount": 100000,
  "beneficiaryName": "Sample Beneficiary",
  "customerType": 1,
  "receiptId": "RCP-001",
  "createdDate": "2022-11-01T12:00:00Z",
  "executedDate": ""
}
```

---

### 3. Execute транзакции

Запрос на выполнение созданной транзакции.

```
POST /ipc/operator/api/v1/payment/qr/{version}/tx/execute/{transactionId}
```

**Headers:** (те же, что и в check)

**Path Parameters:**
- `transactionId: string` (UUID) - ID транзакции от оператора

**Request Body:** Empty

**Response 200:** `StatusDto`
```json
{
  "transactionId": "fbded76a-9fc6-42d8-b0a0-e7e7110e0cc7",
  "status": null,
  "transactionType": null,
  "amount": 40000,
  "beneficiaryName": "c***e A***o",
  "customerType": "1",
  "receiptId": "7218199",
  "createdDate": "2022-11-01T12:00:00Z",
  "executedDate": "2022-11-01T12:02:00Z"
}
```

---

---

## B. Входящие запросы от Оператора (Operator → PSP)

**Controller:** `IncomingController` (beneficiary endpoints)  
**Base URL:** `/in/qr/{version}/tx`

Когда PSP выступает в роли получателя платежа (beneficiary), оператор вызывает эти эндпоинты.

### 1. Check (входящий)

```
POST /in/qr/{version}/tx/check
```

**Headers:**
- `H-HASH: string` (required) - JWS v2 подпись тела запроса

**Request Body:** `CheckRequestDto` (аналогично исходящему)

**Response 200:** `CheckResponseDto`

---

### 2. Create (входящий)

```
POST /in/qr/{version}/tx/create
```

**Headers:**
- `H-HASH: string` (required)

**Request Body:** `CreateRequestDto`

**Response 200:** `CreateResponseDto`

---

### 3. Execute (входящий)

```
POST /in/qr/{version}/tx/execute/{transactionId}
```

**Headers:**
- `H-HASH: string` (required)

**Request Body:** Empty

**Response 200:** `StatusDto`

---

### 4. Update (входящий от оператора)

Оператор всегда отправляет UPDATE с финальным статусом транзакции.

```
POST /in/qr/{version}/tx/update/{transactionId}
```

**Headers:**
- `H-HASH: string` (required)

**Request Body:** `UpdateDto`
```json
{
  "status": 50,
  "updateDate": "2022-11-01T12:02:00Z"
}
```

**Response 200:** ACK (empty body)

---

## C. Внешние API для клиентов (Client → PSP)

**Controller:** `IncomingController` (external methods)  
**Base URL:** `/api/qr/tx`

Публичные эндпоинты для клиентских приложений (без crypto headers).

### 1. Check QR

```
POST /api/qr/tx/check
```

**Headers:** None (стандартные HTTP headers)

**Request Body:** `CheckRequestDto`

**Response 200:** `CheckResponseDto`

---

### 2. Create транзакция

```
POST /api/qr/tx/create
```

**Request Body:** `CreateRequestDto`

**Response 200:** `CreateResponseDto`

---

### 3. Execute транзакция

```
POST /api/qr/tx/execute/{transactionId}
```

**Request Body:** Empty

**Response 200:** `StatusDto`

---

### 4. Get статус

```
GET /api/qr/tx/{transactionId}
```

**Request Body:** None

**Response 200:** `StatusDto`

---

## Детальное описание DTOs

### CheckRequestDto

Используется для проверки QR-кода перед созданием транзакции.

| Поле | Тип | Required | Validation | Описание |
|------|-----|----------|------------|----------|
| qrType | String | Yes | `staticQr\|dynamicQr` | Тип QR-кода |
| merchantProvider | String | Yes | max 32 chars | Уникальный идентификатор провайдера |
| merchantId | String | No | max 32 chars | ID мерчанта |
| serviceId | String | No | max 32 chars | Код услуги |
| serviceName | String | No | max 32 chars | Название услуги |
| beneficiaryAccountNumber | String | No | max 32 chars | Лицевой счёт получателя |
| merchantCode | Integer | Yes | 0-9999 | MCC код мерчанта |
| currencyCode | String | Yes | 3 digits | Валюта (всегда "417" для KGS) |
| qrTransactionId | String | No | max 32 chars | ID транзакции из QR |
| qrComment | String | No | max 32 chars | Комментарий для платежа |
| customerType | String | Yes | `1\|2` | 1=Individual, 2=Corporate |
| amount | Long | Yes | max 13 digits, positive | Сумма в тыйынах |
| qrLinkHash | String | Yes | 4 alphanumeric | Хеш QR-ссылки |
| extra | List<KeyValueDto> | No | max 5 items | Дополнительные поля |

### CreateRequestDto

Расширенная версия CheckRequestDto с дополнительными полями для создания транзакции.

Дополнительные поля:
- `transactionId: String` (required, max 32) - ID транзакции (operator's transaction ID)
- `pspTransactionId: String` (required, max 50) - ID транзакции в системе PSP
- `receiptId: String` (required, max 20) - ID чека/квитанции
- `transactionType: CustomerType` (required, enum) - Тип транзакции

### CheckResponseDto

| Поле | Тип | Описание |
|------|-----|----------|
| beneficiaryName | String | Замаскированное имя получателя (например, "c***e A***o") |
| transactionType | CustomerType | Тип транзакции (enum, nullable) |

### CreateResponseDto

Ответ с подтверждением создания транзакции.

| Поле | Тип | Описание |
|------|-----|----------|
| transactionId | String | UUID транзакции, созданный оператором |
| status | Status | Статус транзакции (enum, nullable) |
| transactionType | CustomerType | Тип транзакции (enum) |
| amount | Long | Сумма транзакции в тыйынах |
| beneficiaryName | String | Имя получателя (masked) |
| customerType | Integer | Тип клиента (1=Individual, 2=Corporate) |
| receiptId | String | ID чека |
| createdDate | String | Дата создания (ISO8601) |
| executedDate | String | Дата выполнения (ISO8601, может быть пустой) |

### StatusDto

Универсальный DTO для статуса транзакции (используется в execute и get).

| Поле | Тип | Nullable | Используется в | Описание |
|------|-----|----------|----------------|----------|
| transactionId | String | No | execute, get | UUID транзакции |
| status | Status | Yes | execute, get | Статус транзакции (enum) |
| transactionType | CustomerType | Yes | execute, get | Тип транзакции (enum) |
| amount | Long | No | execute, get | Сумма в тыйынах |
| beneficiaryName | String | Yes | execute | Имя получателя (masked) |
| customerType | String | Yes | execute | Тип клиента (1 или 2) |
| receiptId | String | Yes | execute, get | ID чека |
| createdDate | String | No | execute, get | Дата создания (ISO8601) |
| executedDate | String | Yes | execute, get | Дата выполнения (ISO8601) |

### UpdateDto

Используется для входящих обновлений от оператора (Operator → PSP).

| Поле | Тип | Required | Описание |
|------|-----|----------|----------|
| status | Status | Yes | Новый статус транзакции (enum) |
| updateDate | String | No | Дата обновления ISO8601 (max 30) |

### KeyValueDto

Вспомогательный DTO для дополнительных полей в `extra`.

| Поле | Тип | Required | Validation |
|------|-----|----------|------------|
| key | String | Yes | NotBlank, max 64 chars |
| value | String | Yes | NotBlank, max 256 chars |

---

## Коды статусов транзакций (Status enum)

| Код | Название | Финальный | Описание |
|-----|----------|-----------|----------|
| 10 | CREATED | Нет | Транзакция создана |
| 20 | IN_PROCESS | Нет | Транзакция в процессе |
| 30 | ERROR | Да | Транзакция завершена с ошибкой |
| 40 | CANCELED | Да | Транзакция отменена |
| 50 | SUCCESS | Да | Транзакция успешно завершена |

---

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

---

## Идемпотентность

Все операции, изменяющие состояние (check, create, execute, update), должны быть идемпотентными:

- **check:** Идемпотентность по `(qrLinkHash, amount, customerType)`
- **create:** Идемпотентность по `pspTransactionId`
- **execute:** Идемпотентность по `transactionId`
- **update:** Идемпотентность по `(transactionId, status)`

Идемпотентность реализуется через Redis с TTL ключами.

---

## Примеры использования

### Полный флоу sender (PSP → Operator)

1. **Check QR** → получить информацию о получателе
2. **Create** → создать транзакцию, получить `transactionId`
3. **Execute** → выполнить транзакцию
4. Если execute не вернул финальный статус → **Get** или ждать **Update** от оператора
5. Если оператор недоступен → отправить **Update** позже через RabbitMQ

### Полный флоу beneficiary (Operator → PSP)

1. Оператор вызывает **Check** → PSP проверяет реквизиты
2. Оператор вызывает **Create** → PSP создаёт транзакцию
3. Оператор вызывает **Execute** → PSP выполняет
4. Оператор отправляет **Update** с финальным статусом

---

## Связь с БД

Маппинг DTOs на таблицу `qr_tx`:

| DTO поле | Таблица `qr_tx` |
|----------|-----------------|
| transactionId (operator) | operator_tx_id |
| pspTransactionId | psp_transaction_id |
| status | status |
| amount | amount |
| commission | commission |
| transactionType | transaction_type |
| qrLinkHash | qr_link_hash |
| qrTransactionId | qr_tx_id |
| merchantProvider | merchant_provider |
| receiptId | request_hash (или отдельное поле) |

---

## Заметки по безопасности

1. **JWS подпись (H-HASH)**: Все запросы к/от оператора подписаны RSA 2048 (H-SIGNING-VERSION=2)
2. **JWE шифрование**: Тело запроса может быть зашифровано RSA-OAEP-256 + A256GCM
3. **mTLS**: Взаимная TLS аутентификация с оператором
4. **Маскировка PII**: `beneficiaryName` всегда маскируется в ответах
5. **Валидация**: Jakarta Bean Validation на всех входных DTOs

---

## См. также

- [API Contracts](contracts.md) - Детальное описание REST контрактов
- [Security](../security/crypto.md) - JWS/JWE спецификации
- [Data Schema](../data/schema.md) - Схема БД
- [PRD](../product/PRD.md) - Бизнес требования

