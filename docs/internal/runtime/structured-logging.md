# Структурированное логирование в PSP сервисе

## Обзор

PSP сервис использует упрощенное структурированное логирование, сфокусированное на данных, необходимых для эффективного анализа ошибок и мониторинга. Система логирования была значительно упрощена для повышения производительности и читаемости.

## Ключевые свойства для анализа логов

### Основные идентификаторы
- `correlationId` - Уникальный идентификатор для трассировки запроса через все компоненты
- `pspTransactionId` - Основной идентификатор PSP транзакции
- `transactionId` - Идентификатор транзакции оператора
- `receiptId` - Идентификатор чека

### Контекст операции
- `operationType` - Тип операции (CHECK, CREATE, EXECUTE, UPDATE)
- `status` - Статус операции
- `transferDirection` - Направление транзакции (IN, OUT, OWN)

### Данные транзакции
- `merchantCode` - Код мерчанта
- `amount` - Сумма транзакции

### Ошибки
- `errorCode` - Код ошибки
- `errorMessage` - Сообщение об ошибке

### Временные метки
- `timestamp` - Временная метка события

## Типы событий

### 1. operation_start
Логируется в начале каждой операции.

```json
{
  "event": "operation_start",
  "operationType": "CHECK",
  "timestamp": "2024-01-15T10:30:00",
  "pspTransactionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "transferDirection": "IN",
  "merchantCode": 1234
}
```

### 2. operation_success
Логируется при успешном завершении операции.

```json
{
  "event": "operation_success",
  "operationType": "CHECK",
  "timestamp": "2024-01-15T10:30:01",
  "pspTransactionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "transactionId": "OP-2024-001",
  "status": "SUCCESS"
}
```

### 3. operation_error (business_error)
Логируется при известных бизнес-ошибках (без stack trace).

```json
{
  "event": "operation_error",
  "operationType": "CHECK",
  "timestamp": "2024-01-15T10:30:01",
  "pspTransactionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "errorCode": "MIN_AMOUNT_INVALID",
  "errorMessage": "Minimum amount is 100"
}
```

### 4. operation_error (system_error)
Логируется при системных ошибках (с stack trace).

```json
{
  "event": "operation_error",
  "operationType": "CHECK",
  "timestamp": "2024-01-15T10:30:01",
  "pspTransactionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "errorCode": "NETWORK_ERROR",
  "errorMessage": "Connection timeout"
}
```

### 5. audit_trail
Логируется для аудита изменений.

```json
{
  "event": "audit_trail",
  "action": "UPDATE",
  "pspTransactionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "details": "Transaction updated",
  "timestamp": "2024-01-15T10:30:01"
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

### LoggingUtil (упрощенная версия)
Основной класс для структурированного логирования:

```java
// Генерация correlation ID
String correlationId = LoggingUtil.generateAndSetCorrelationId();

// Установка контекста транзакции
LoggingUtil.setTransactionContext(pspTransactionId, transactionId, receiptId, 
    operationType, transferDirection, merchantCode, amount, status);

// Логирование начала операции
LoggingUtil.logOperationStart("CHECK", pspTransactionId, transferDirection, merchantCode);

// Логирование успешного завершения
LoggingUtil.logOperationSuccess("CHECK", pspTransactionId, transactionId, status);

// Логирование ошибки (автоматически определяет тип и уровень)
LoggingUtil.logError("CHECK", pspTransactionId, errorCode, errorMessage, throwable);

// Аудит
LoggingUtil.logAuditTrail("UPDATE", pspTransactionId, "Transaction updated");

// Очистка контекста
LoggingUtil.clearContext();
```

### LoggingFilter
Автоматически добавляет correlation ID и логирует HTTP запросы:

```java
@Component
@Order(1)
public class LoggingFilter implements WebFilter {
    // Автоматически генерирует correlation ID
    // Логирует начало и конец HTTP запросов
    // Измеряет время ответа
    // Очищает MDC контекст
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
grep "operationType.*CREATE" application.log
```

### Анализ по статусу
```bash
# Найти все успешные операции
grep "event.*operation_success" application.log
```

### Анализ по типам ошибок
```bash
# Найти все ошибки операций
grep "event.*operation_error" application.log

# Найти ошибки валидации
grep "errorCode.*VALIDATION" application.log

# Найти ошибки по направлению транзакции
grep "transferDirection.*IN.*errorCode" application.log
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
Ключевые свойства автоматически добавляются в MDC для включения в каждый лог:

```java
// Автоматически добавляется в каждый лог
MDC.put("correlationId", correlationId);
MDC.put("pspTransactionId", pspTransactionId);
MDC.put("transactionId", transactionId);
MDC.put("receiptId", receiptId);
MDC.put("operationType", operationType);
MDC.put("transferDirection", transferDirection);
MDC.put("merchantCode", merchantCode);
MDC.put("amount", amount);
MDC.put("status", status);
MDC.put("errorCode", errorCode);
```

## Изменения в системе логирования

### Упрощения (v2.0)
1. **Удалены поля безопасности** - `ipAddress` и `userAgent` (доступ ограничен доверенными источниками)
2. **Заменен `merchantProvider` на `transferDirection`** - более семантически точное поле
3. **Упрощены типы операций** - `CHECK_TRANSACTION` → `CHECK`, `CREATE_TRANSACTION` → `CREATE`
4. **Сокращено количество MDC полей** - с 20+ до 9 основных полей
5. **Упрощены методы логирования** - с 20+ до 5 основных методов
6. **Удалены сложные properties maps** - фокус на простых параметрах

### Преимущества упрощения
- **Производительность** - меньше операций с MDC и логированием
- **Читаемость** - проще понимать и использовать
- **Фокус** - только необходимые данные для анализа ошибок
- **Безопасность** - соответствие архитектуре с доверенными источниками

## Рекомендации

1. **Всегда используйте LoggingUtil** для структурированного логирования
2. **Не логируйте чувствительные данные** (пароли, токены, полные номера карт)
3. **Используйте соответствующие уровни логирования** (DEBUG, INFO, WARN, ERROR)
4. **Включайте correlation ID** во все логи для трассировки
5. **Логируйте ключевые бизнес-события** для аудита
6. **Различайте бизнес и системные ошибки** - бизнес-ошибки не должны засорять логи stack trace
7. **Используйте LoggingUtil.logError()** для автоматического определения типа ошибки
8. **Настройте алерты** на системные ошибки, но не на бизнес-ошибки
9. **Анализируйте частоту бизнес-ошибок** для выявления проблем в клиентском коде
10. **Используйте transferDirection** для анализа направления транзакций
