# Миграция системы логирования v2.0

## Обзор изменений

Система логирования PSP сервиса была значительно упрощена для повышения производительности, читаемости и соответствия архитектуре безопасности.

## Основные изменения

### 1. Удаление полей безопасности
- **Удалены:** `ipAddress`, `userAgent` из `OperationEntity` и схемы БД
- **Причина:** Доступ к системе ограничен доверенными источниками
- **Миграция:** Создан скрипт `V001__remove_ip_address_user_agent.sql`

### 2. Семантические улучшения
- **Заменено:** `merchantProvider` → `transferDirection`
- **Причина:** Более точное отражение направления транзакции
- **Значения:** `IN` (входящие), `OUT` (исходящие), `OWN` (внутренние)

### 3. Упрощение LoggingUtil
- **Было:** 358 строк, 20+ методов, 20+ MDC полей
- **Стало:** 181 строка, 5 методов, 9 MDC полей
- **Сокращение:** 49% кода

### 4. Упрощение типов операций
- `CHECK_TRANSACTION` → `CHECK`
- `CREATE_TRANSACTION` → `CREATE`
- `EXECUTE_TRANSACTION` → `EXECUTE`
- `UPDATE_TRANSACTION` → `UPDATE`

## Изменения в коде

### LoggingUtil (до/после)

#### До (сложная версия):
```java
// 20+ MDC констант
public static final String MERCHANT_PROVIDER = "merchantProvider";
public static final String IP_ADDRESS = "ipAddress";
public static final String USER_AGENT = "userAgent";
// ... еще 17+ констант

// 20+ методов логирования
public static void logOperationStart(String operationType, Map<String, Object> properties)
public static void logBusinessValidation(String validationType, boolean isValid, String details, Map<String, Object> properties)
public static void logSignatureVerification(boolean isVerified, String details, Map<String, Object> properties)
// ... еще 17+ методов
```

#### После (упрощенная версия):
```java
// 9 основных MDC констант
public static final String CORRELATION_ID = "correlationId";
public static final String PSP_TRANSACTION_ID = "pspTransactionId";
public static final String TRANSACTION_ID = "transactionId";
public static final String RECEIPT_ID = "receiptId";
public static final String OPERATION_TYPE = "operationType";
public static final String TRANSFER_DIRECTION = "transferDirection";
public static final String MERCHANT_CODE = "merchantCode";
public static final String AMOUNT = "amount";
public static final String STATUS = "status";
public static final String ERROR_CODE = "errorCode";

// 5 основных методов
public static String generateAndSetCorrelationId()
public static void setTransactionContext(...)
public static void logOperationStart(...)
public static void logOperationSuccess(...)
public static void logError(...)
```

### IncomingController (до/после)

#### До (сложная версия):
```java
// 301 строка
Map<String, Object> properties = new HashMap<>();
properties.put("operationType", "CHECK_TRANSACTION");
properties.put("apiVersion", version);
LoggingUtil.logOperationStart("CHECK_TRANSACTION", properties);
LoggingUtil.logSignatureVerification(false, verificationResult.getErrorMessage(), properties);
// ... много сложного логирования
```

#### После (упрощенная версия):
```java
// 166 строк
LoggingUtil.generateAndSetCorrelationId();
// Простая обработка запроса без сложного логирования
// Детальное логирование в IncomingServiceImpl
```

## Миграция базы данных

### Удаление колонок
```sql
-- V001__remove_ip_address_user_agent.sql
ALTER TABLE operations DROP COLUMN ip_address;
ALTER TABLE operations DROP COLUMN user_agent;
```

### Обновление схемы
```sql
-- oracle-schema.sql (обновлен)
-- Удалены строки:
-- ip_address VARCHAR2(45),
-- user_agent VARCHAR2(500),
```

## Примеры логов (до/после)

### До (сложные логи):
```json
{
  "event": "operation_start",
  "operationType": "CHECK_TRANSACTION",
  "timestamp": "2024-01-15T10:30:00",
  "correlationId": "uuid-123",
  "merchantProvider": "provider1",
  "merchantCode": 1234,
  "amount": 50000,
  "ipAddress": "192.168.1.1",
  "userAgent": "Mozilla/5.0...",
  "apiVersion": "v1",
  "uri": "/in/qr/v1/tx/check",
  "signatureVerified": true,
  "requestHash": "abc123..."
}
```

### После (упрощенные логи):
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

## Преимущества миграции

### Производительность
- **49% меньше кода** в LoggingUtil
- **Меньше операций с MDC** (9 вместо 20+ полей)
- **Упрощенные методы** без сложных properties maps
- **Быстрее компиляция** и выполнение

### Безопасность
- **Соответствие архитектуре** с доверенными источниками
- **Нет избыточных данных** о клиентах
- **Фокус на бизнес-логике** вместо технических деталях

### Читаемость
- **Проще понимать** структуру логов
- **Меньше полей** для анализа
- **Семантически точные** названия полей
- **Единообразное** использование терминологии

### Поддержка
- **Легче отлаживать** проблемы
- **Проще анализировать** логи
- **Меньше ложных срабатываний** в мониторинге
- **Фокус на важных данных** для анализа ошибок

## Обратная совместимость

### Что изменилось
- **Типы операций:** `CHECK_TRANSACTION` → `CHECK`
- **Поля логов:** `merchantProvider` → `transferDirection`
- **Удалены поля:** `ipAddress`, `userAgent`

### Что осталось
- **Структура JSON** логов
- **Уровни логирования** (INFO, WARN, ERROR)
- **Correlation ID** для трассировки
- **Основные идентификаторы** транзакций

## Рекомендации по миграции

1. **Обновите мониторинг** - измените фильтры с `CHECK_TRANSACTION` на `CHECK`
2. **Обновите алерты** - используйте `transferDirection` вместо `merchantProvider`
3. **Обновите дашборды** - адаптируйте под новую структуру логов
4. **Протестируйте** - убедитесь, что все системы мониторинга работают
5. **Документируйте** - обновите внутреннюю документацию

## Заключение

Миграция на упрощенную систему логирования v2.0 значительно улучшает производительность, безопасность и читаемость системы, сохраняя при этом все необходимые данные для эффективного анализа ошибок и мониторинга.
