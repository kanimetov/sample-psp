# PSP Service

Payment Service Provider для QR-платежей по протоколу DKIBQR.

## Быстрый старт

```bash
cd psp-service
./gradlew bootRun
```

Сервис запустится на `http://localhost:8080`

## Текущий статус

**MVP реализован** - входящие API для обработки запросов от оператора с полной верификацией подписей JWS v2.

### ✅ Реализовано
- **API Layer**: IncomingController с полной обработкой запросов от оператора
- **Security**: JWS v2 верификация подписей, управление ключами
- **Business Logic**: IncomingService с валидацией и обработкой транзакций
- **Data Layer**: Entity модели и Oracle схема
- **Error Handling**: Централизованная обработка исключений
- **Logging**: Структурированное логирование всех операций

### 🔄 В разработке
- Внешние API для клиентов
- Интеграция с Redis и RabbitMQ
- OperatorClient для исходящих запросов

## API Endpoints

### Входящие от Оператора (активные)
- `POST /in/qr/{version}/tx/check` - Проверка транзакции
- `POST /in/qr/{version}/tx/create` - Создание транзакции  
- `POST /in/qr/{version}/tx/execute/{transactionId}` - Выполнение транзакции
- `POST /in/qr/{version}/tx/update/{transactionId}` - Обновление статуса

### Внешние API (планируются)
- `POST /api/qr/tx/check` - Проверка QR-кода
- `POST /api/qr/tx/create` - Создание транзакции
- `POST /api/qr/tx/execute/{transactionId}` - Выполнение транзакции
- `GET /api/qr/tx/{transactionId}` - Получение статуса

## Технологии

- **Java 21** + **Spring Boot 3.4.1**
- **Gradle** + **Lombok**
- **Jakarta Bean Validation** + **Jackson**
- **Oracle DB** + **Redis** + **RabbitMQ** (планируется)

## Документация

📚 **Полная документация**: [`../docs/internal/`](../docs/internal/)

### Основные разделы:
- **[IMPLEMENTATION_STATUS.md](../docs/internal/IMPLEMENTATION_STATUS.md)** - Текущий статус реализации
- **[api/](../docs/internal/api/)** - API контракты и DTOs
- **[product/PRD.md](../docs/internal/product/PRD.md)** - Продуктовые требования
- **[design/](../docs/internal/design/)** - Архитектура и потоки
- **[security/](../docs/internal/security/)** - Криптография и безопасность
- **[data/](../docs/internal/data/)** - Схема БД и миграции

### Настройка:
- **[SIGNATURE_SETUP.md](SIGNATURE_SETUP.md)** - Настройка RSA подписей
- **[ORACLE_SETUP.md](ORACLE_SETUP.md)** - Настройка Oracle DB

## Пример запроса

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

