# PSP Service

Payment Service Provider для QR-платежей по протоколу DKIBQR.

## Быстрый старт

```bash
cd psp-service
./gradlew bootRun
```

Сервис запустится на `http://localhost:8080`

## Текущий статус

**MVP реализован** - полная архитектура PSP сервиса с поддержкой QR-платежей и входящих транзакций.

### ✅ Реализовано
- **API Layer**: 
  - IncomingController для входящих запросов от оператора
  - MerchantController для внешних QR-платежей
- **Service Layer**: Полная архитектура сервисов
  - MerchantService - маршрутизация QR-платежей
  - BankService - обработка банковских транзакций
  - OperatorService - обработка операторских транзакций
  - IncomingService - фасад для входящих операций
- **Client Layer**: Внешние клиенты
  - BankClient - интеграция с банковскими системами
  - OperatorClient - интеграция с операторскими API
  - QrDecoderClient - декодирование QR-кодов
- **Security**: JWS v2 верификация подписей, управление ключами
- **Data Layer**: Entity модели и Oracle схема
- **Error Handling**: Централизованная обработка исключений
- **Logging**: Структурированное логирование всех операций

### 🔄 В разработке
- Интеграция с Redis для кэширования
- Интеграция с RabbitMQ для асинхронной обработки
- Расширение OperatorService для входящих транзакций

## API Endpoints

### Входящие от Оператора (активные)
- `POST /in/qr/{version}/tx/check` - Проверка транзакции
- `POST /in/qr/{version}/tx/create` - Создание транзакции  
- `POST /in/qr/{version}/tx/execute/{transactionId}` - Выполнение транзакции
- `POST /in/qr/{version}/tx/update/{transactionId}` - Обновление статуса

### Внешние API для QR-платежей (активные)
- `POST /out/qr/{version}/check` - Проверка QR-кода
- `POST /out/qr/{version}/make-payment` - Создание платежа

### Архитектура маршрутизации
- **BankService**: Обрабатывает транзакции для `merchantProvider = "demirbank"`
- **OperatorService**: Обрабатывает транзакции для всех других провайдеров
- **IncomingService**: Всегда делегирует операции в BankService

## Технологии

- **Java 21** + **Spring Boot 3.4.1**
- **Gradle** + **Lombok**
- **Jakarta Bean Validation** + **Jackson**
- **Oracle DB** + **Redis** (настроено) + **RabbitMQ** (планируется)
- **Reactive Programming** (WebFlux + Reactor)
- **JWS v2** для верификации подписей

## Документация

📚 **Полная документация**: [`../docs/internal/`](../docs/internal/)

### Основные разделы:
- **[design/service-architecture.md](../docs/internal/design/service-architecture.md)** - Архитектура сервисов
- **[api/](../docs/internal/api/)** - API контракты и DTOs
- **[product/PRD.md](../docs/internal/product/PRD.md)** - Продуктовые требования
- **[design/](../docs/internal/design/)** - Архитектура и потоки
- **[security/](../docs/internal/security/)** - Криптография и безопасность
- **[data/](../docs/internal/data/)** - Схема БД и миграции

### Настройка:
- **[SIGNATURE_SETUP.md](SIGNATURE_SETUP.md)** - Настройка RSA подписей
- **[ORACLE_SETUP.md](ORACLE_SETUP.md)** - Настройка Oracle DB

## Примеры запросов

### QR-платеж (внешний API)
```bash
# Проверка QR-кода
curl -X POST http://localhost:8080/out/qr/v1/check \
  -H "Content-Type: application/json" \
  -d '{
    "qrUri": "https://example.com/qr?data=encoded_qr_data"
  }'

# Создание платежа
curl -X POST http://localhost:8080/out/qr/v1/make-payment \
  -H "Content-Type: application/json" \
  -d '{
    "paymentSessionId": "session-uuid",
    "amount": 100000
  }'
```

### Входящая транзакция (от оператора)
```bash
# Check транзакции (требует JWS v2 подпись)
curl -X POST http://localhost:8080/in/qr/v1/tx/check \
  -H "Content-Type: application/json" \
  -H "H-HASH: <jws-signature>" \
  -d '{
    "qrType": "staticQr",
    "merchantProvider": "DEMO",
    "merchantCode": 5411,
    "currencyCode": "417",
    "customerType": "1",
    "amount": 100000,
    "qrLinkHash": "AB12"
  }'
```

## Разработка

### Требования
- Java 21+
- Gradle 8.x (wrapper включен)

### Сборка
```bash
./gradlew build
```

### Конфигурация
Настройки в `src/main/resources/application.yml`:
- Server port
- Database connections  
- Redis configuration
- RabbitMQ queues
- Logging levels

### Логирование
Структурированное логирование настроено в `logback-spring.xml`:

**Директория логов**: `logs/`
- `psp-service.log` - основные логи приложения
- `psp-service-error.log` - только ошибки (ERROR level)
- `psp-service-audit.log` - аудит операций (90 дней хранения)
- `psp-service-performance.log` - метрики производительности (7 дней хранения)

**Ротация логов**:
- Максимальный размер файла: 100MB
- История: 30 дней для основных логов
- Общий размер: до 1GB

**Уровни логирования**:
- `kg.demirbank.psp`: DEBUG (dev), INFO (prod)
- `org.hibernate.SQL`: DEBUG (dev), WARN (prod)
- `org.springframework`: INFO (dev), WARN (prod)

---

**Internal project - Demirbank**

