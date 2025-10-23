# PSP Service

Payment Service Provider для QR-платежей по протоколу DKIBQR.

## Быстрый старт

### Запуск приложения

```bash
cd psp-service
./gradlew bootRun
```

Сервис запустится на `http://localhost:8080`

### Сборка

```bash
./gradlew build
```

## Структура проекта

```
psp-service/
├── src/main/java/kg/demirbank/psp/
│   ├── PspServiceApplication.java   - Main класс
│   ├── api/                         - REST контроллеры
│   │   ├── IncomingController.java      - Входящие от оператора + внешние API
│   │   ├── OutgoingController.java      - Исходящие к оператору (mock)
│   └── dto/                         - Data Transfer Objects
│       ├── CheckRequest.java
│       ├── CheckResponse.java
│       ├── CreateRequest.java
│       ├── CreateResponse.java
│       ├── StatusDto.java
│       ├── UpdateDto.java
│       └── KeyValue.java
└── src/main/resources/
    └── application.yml              - Конфигурация Spring Boot
```

## API Endpoints

### Внешние API для клиентов

- `POST /api/qr/tx/check` - Проверка QR-кода
- `POST /api/qr/tx/create` - Создание транзакции
- `POST /api/qr/tx/execute/{transactionId}` - Выполнение транзакции
- `GET /api/qr/tx/{transactionId}` - Получение статуса

### Входящие от Оператора (Beneficiary side)

- `POST /in/qr/{version}/tx/check` - Check от оператора
- `POST /in/qr/{version}/tx/create` - Create от оператора
- `POST /in/qr/{version}/tx/execute/{transactionId}` - Execute от оператора
- `POST /qr/{version}/tx/update/{transactionId}` - UPDATE от оператора

### Исходящие к Оператору (Sender side, mock)

- `POST /ipc/operator/api/v1/payment/qr/{version}/tx/check` - Check к оператору
- `POST /ipc/operator/api/v1/payment/qr/{version}/tx/create` - Create к оператору
- `POST /ipc/operator/api/v1/payment/qr/{version}/tx/execute/{transactionId}` - Execute к оператору
- `GET /ipc/operator/api/v1/payment/qr/{version}/tx/get/{transactionId}` - Get статуса
- `POST /ipc/operator/api/v1/payment/qr/{version}/tx/update/{transactionId}` - UPDATE к оператору

## DTOs

Все DTOs находятся в пакете `kg.demirbank.psp.dto`:

| DTO | Назначение |
|-----|------------|
| `CheckRequest` | Request для check операции |
| `CheckResponse` | Response для check (beneficiaryName, transactionType) |
| `CreateRequest` | Request для create операции |
| `CreateResponse` | Response для create (transactionId) |
| `StatusDto` | Универсальный статус для execute/get responses |
| `UpdateDto` | Bidirectional DTO для update операций |
| `KeyValue` | Вспомогательный для extra полей |

## Текущее состояние

✅ **Реализовано:**
- Базовая структура Spring Boot приложения
- Все контроллеры с mock responses
- Полный набор DTOs с валидацией
- Валидация H-SIGNING-VERSION=2 (mock)

🚧 **TODO:**
- Интеграция с Oracle DB
- Интеграция с Redis (идемпотентность, кэширование)
- Интеграция с RabbitMQ (асинхронные ретраи)
- JWS/JWE криптография (RSA 2048)
- mTLS к оператору
- Service layer с бизнес-логикой
- Observability (metrics, traces, logs)
- Unit и integration тесты

## Документация

Полная документация находится в папке `internal/`:

- **[internal/api/dto-mapping.md](../internal/api/dto-mapping.md)** - Детальное описание всех эндпоинтов и DTOs
- **[internal/api/dto.md](../internal/api/dto.md)** - Краткая спецификация DTOs
- **[internal/product/PRD.md](../internal/product/PRD.md)** - Product Requirements Document
- **[internal/data/ddl.sql](../internal/data/ddl.sql)** - Oracle DB schema
- **[internal/security/crypto.md](../internal/security/crypto.md)** - JWS/JWE спецификации

## Технологии

- Java 21
- Spring Boot 3.4.1
- Gradle
- Lombok
- Jakarta Bean Validation
- Jackson (JSON)

## Требования

- Java 21+
- Gradle 8.x (wrapper included)

## Пример запроса

```bash
# Check QR
curl -X POST http://localhost:8080/api/qr/tx/check \
  -H "Content-Type: application/json" \
  -d '{
    "qrType": "staticQr",
    "merchantProvider": "DEMO",
    "merchantCode": 5411,
    "currencyCode": "417",
    "customerType": "1",
    "amount": 100000,
    "qrLinkHash": "AB12"
  }'

# Response:
# {
#   "beneficiaryName": "c***e A***o",
#   "transactionType": "10"
# }
```

## Разработка

### Добавление новых зависимостей

Редактируйте `build.gradle`:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    // ...
}
```

### Конфигурация

Редактируйте `src/main/resources/application.yml` для настройки:
- Server port
- Database connections
- Redis configuration
- RabbitMQ queues
- Logging levels

## Лицензия

Internal project - Demirbank

