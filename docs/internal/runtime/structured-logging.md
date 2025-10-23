# Структурированное логирование в PSP сервисе

## Обзор

PSP сервис использует структурированное логирование для обеспечения эффективного анализа логов, мониторинга и отладки. Все логи содержат ключевые свойства, которые позволяют легко фильтровать, агрегировать и анализировать данные.

## Ключевые свойства для анализа логов

### Основные идентификаторы
- `correlationId` - Уникальный идентификатор для трассировки запроса через все компоненты
- `transactionId` - Идентификатор транзакции PSP
- `pspTransactionId` - Идентификатор транзакции от PSP провайдера
- `receiptId` - Идентификатор чека

### Контекст операции
- `operationType` - Тип операции (CHECK_TRANSACTION, CREATE_TRANSACTION, EXECUTE_TRANSACTION, UPDATE_TRANSACTION)
- `status` - Статус операции
- `apiVersion` - Версия API
- `uri` - URI запроса

### Данные транзакции
- `merchantProvider` - Провайдер мерчанта
- `merchantCode` - Код мерчанта
- `qrType` - Тип QR кода (staticQr, dynamicQr)
- `amount` - Сумма транзакции
- `currencyCode` - Код валюты
- `customerType` - Тип клиента (1, 2)

### Производительность
- `responseTimeMs` - Время ответа в миллисекундах
- `timestamp` - Временная метка события

### Безопасность
- `signatureVerified` - Результат проверки подписи
- `requestHash` - Хеш запроса
- `ipAddress` - IP адрес клиента
- `userAgent` - User Agent клиента

### Ошибки
- `errorCode` - Код ошибки
- `errorMessage` - Сообщение об ошибке

## Типы событий

### 1. operation_start
Логируется в начале каждой операции.

```json
{
  "event": "operation_start",
  "operationType": "CHECK_TRANSACTION",
  "timestamp": "2024-01-15T10:30:00",
  "correlationId": "uuid-123",
  "merchantProvider": "provider1",
  "merchantCode": 1234,
  "amount": 50000
}
```

### 2. operation_success
Логируется при успешном завершении операции.

```json
{
  "event": "operation_success",
  "operationType": "CHECK_TRANSACTION",
  "timestamp": "2024-01-15T10:30:01",
  "correlationId": "uuid-123",
  "responseTimeMs": 150,
  "beneficiaryName": "c***e A***o",
  "transactionType": "C2C"
}
```

### 3. business_error
Логируется при известных бизнес-ошибках (без stack trace).

```json
{
  "event": "business_error",
  "operationType": "CHECK_TRANSACTION",
  "timestamp": "2024-01-15T10:30:01",
  "correlationId": "uuid-123",
  "errorCode": "MIN_AMOUNT_INVALID",
  "errorMessage": "Minimum amount is 100",
  "amount": 50
}
```

### 4. system_error
Логируется при системных ошибках (с stack trace).

```json
{
  "event": "system_error",
  "operationType": "CHECK_TRANSACTION",
  "timestamp": "2024-01-15T10:30:01",
  "correlationId": "uuid-123",
  "errorCode": "NETWORK_ERROR",
  "errorMessage": "Connection timeout",
  "exception": "NetworkTimeoutException"
}
```

### 5. business_validation
Логируется при бизнес-валидации.

```json
{
  "event": "business_validation",
  "validationType": "AMOUNT_VALIDATION",
  "isValid": false,
  "details": "Amount below minimum: 50",
  "timestamp": "2024-01-15T10:30:00",
  "correlationId": "uuid-123"
}
```

### 6. signature_verification
Логируется при проверке подписи.

```json
{
  "event": "signature_verification",
  "isVerified": true,
  "details": "Signature verified successfully",
  "timestamp": "2024-01-15T10:30:00",
  "correlationId": "uuid-123"
}
```

### 7. performance_metrics
Логируется для метрик производительности.

```json
{
  "event": "performance_metrics",
  "operationType": "CHECK_TRANSACTION",
  "responseTimeMs": 150,
  "timestamp": "2024-01-15T10:30:01",
  "correlationId": "uuid-123"
}
```

### 8. audit_trail
Логируется для аудита изменений.

```json
{
  "event": "audit_trail",
  "action": "UPDATE_TRANSACTION",
  "entityType": "TRANSACTION",
  "entityId": "tx-123",
  "timestamp": "2024-01-15T10:30:01",
  "correlationId": "uuid-123"
}
```

## Типы ошибок и логирование

### Бизнес-ошибки (без stack trace)
Логируются как WARN без stack trace, так как являются частью нормального flow:

- `MinAmountNotValidException` - неверная минимальная сумма
- `MaxAmountNotValidException` - неверная максимальная сумма  
- `IncorrectRequestDataException` - некорректные данные запроса
- `BadRequestException` - неверный запрос
- `ValidationException` - ошибка валидации
- `AccessDeniedException` - доступ запрещен
- `ResourceNotFoundException` - ресурс не найден
- `UnprocessableEntityException` - необрабатываемая сущность
- `RecipientDataIncorrectException` - некорректные данные получателя
- `SignatureVerificationException` - ошибка проверки подписи

### Системные ошибки (с stack trace)
Логируются как ERROR с stack trace, так как требуют внимания разработчиков:

- `NetworkException` - сетевые ошибки
- `NetworkConnectionException` - ошибки соединения
- `NetworkTimeoutException` - таймауты сети
- `ExternalServerNotAvailableException` - внешний сервер недоступен
- `SupplierNotAvailableException` - поставщик недоступен
- `SystemErrorException` - системные ошибки
- `RuntimeException` - неожиданные runtime ошибки
- `Exception` - все остальные исключения

## Использование в коде

### LoggingUtil
Основной класс для структурированного логирования:

```java
// Установка контекста транзакции
LoggingUtil.setTransactionContext(transactionId, pspTransactionId, receiptId, 
    merchantProvider, merchantCode, qrType);

// Установка контекста операции
LoggingUtil.setOperationContext(operationType, status, amount, 
    currencyCode, customerType, apiVersion);

// Логирование начала операции
LoggingUtil.logOperationStart("CHECK_TRANSACTION", properties);

// Логирование успешного завершения
LoggingUtil.logOperationSuccess("CHECK_TRANSACTION", properties);

// Логирование ошибки (автоматически определяет тип и уровень)
LoggingUtil.logError("CHECK_TRANSACTION", errorCode, errorMessage, throwable, properties);
```

### LoggingFilter
Автоматически добавляет correlation ID и логирует HTTP запросы:

```java
@Component
@Order(1)
public class LoggingFilter extends OncePerRequestFilter {
    // Автоматически генерирует correlation ID
    // Логирует начало и конец HTTP запросов
    // Измеряет время ответа
}
```

## Анализ логов

### Поиск по correlation ID
```bash
# Найти все логи для конкретного запроса
grep "correlationId.*uuid-123" application.log
```

### Анализ производительности
```bash
# Найти медленные операции (>1000ms)
grep "responseTimeMs.*[1-9][0-9][0-9][0-9]" application.log
```

### Анализ ошибок
```bash
# Найти все ошибки валидации
grep "errorCode.*VALIDATION" application.log

# Найти ошибки конкретного мерчанта
grep "merchantCode.*1234.*errorCode" application.log
```

### Анализ по типу операции
```bash
# Найти все операции создания транзакций
grep "operationType.*CREATE_TRANSACTION" application.log
```

### Анализ по статусу
```bash
# Найти все успешные операции
grep "event.*operation_success" application.log
```

### Анализ по типам ошибок
```bash
# Найти только бизнес-ошибки (без stack trace)
grep "event.*business_error" application.log

# Найти системные ошибки (с stack trace)
grep "event.*system_error" application.log

# Найти ошибки валидации
grep "errorCode.*VALIDATION" application.log
```

## Мониторинг и алерты

### Ключевые метрики для мониторинга:
1. **Время ответа** - среднее время ответа по типам операций
2. **Частота ошибок** - процент ошибок по типам операций
3. **Объем транзакций** - количество транзакций по мерчантам
4. **Ошибки валидации** - частота ошибок валидации

### Примеры алертов:
- Время ответа > 5 секунд
- Частота системных ошибок > 1% (требуют внимания)
- Частота бизнес-ошибок > 10% (могут указывать на проблемы в клиентском коде)
- Ошибки подписи > 1%
- Ошибки валидации суммы > 10%

## Конфигурация логирования

### application.yml
```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{correlationId}] %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{correlationId}] %logger{36} - %msg%n"
  level:
    kg.demirbank.psp: INFO
    kg.demirbank.psp.util.LoggingUtil: DEBUG
```

### MDC (Mapped Diagnostic Context)
Все ключевые свойства автоматически добавляются в MDC для включения в каждый лог:

```java
// Автоматически добавляется в каждый лог
MDC.put("correlationId", correlationId);
MDC.put("transactionId", transactionId);
MDC.put("merchantProvider", merchantProvider);
// ... другие свойства
```

## Рекомендации

1. **Всегда используйте LoggingUtil** для структурированного логирования
2. **Не логируйте чувствительные данные** (пароли, токены, полные номера карт)
3. **Используйте соответствующие уровни логирования** (DEBUG, INFO, WARN, ERROR)
4. **Включайте correlation ID** во все логи для трассировки
5. **Логируйте ключевые бизнес-события** для аудита
6. **Мониторьте производительность** через метрики времени ответа
7. **Различайте бизнес и системные ошибки** - бизнес-ошибки не должны засорять логи stack trace
8. **Используйте LoggingUtil.logError()** для автоматического определения типа ошибки
9. **Настройте алерты** на системные ошибки, но не на бизнес-ошибки
10. **Анализируйте частоту бизнес-ошибок** для выявления проблем в клиентском коде
