# Oracle Database Setup для PSP Service

## Требования

- Oracle Database 12c или выше
- JDK 21
- Spring Boot 3.3.4

## Настройка Oracle Database

### 1. Создание пользователя базы данных

```sql
-- Подключитесь как SYSDBA
sqlplus sys/password@localhost:1521/XE as sysdba

-- Создайте пользователя
CREATE USER psp_user IDENTIFIED BY psp_password;
GRANT CONNECT, RESOURCE TO psp_user;
GRANT CREATE SESSION TO psp_user;
GRANT CREATE TABLE TO psp_user;
GRANT CREATE SEQUENCE TO psp_user;
GRANT CREATE INDEX TO psp_user;
```

### 2. Создание последовательностей

```sql
-- Подключитесь как psp_user
sqlplus psp_user/psp_password@localhost:1521/XE

-- Выполните скрипт создания последовательностей
@src/main/resources/db/oracle-sequences.sql
```

### 3. Создание таблиц

```sql
-- Выполните скрипт создания таблиц
@src/main/resources/db/oracle-schema.sql
```

### 4. Настройка приложения

Обновите `application.yml` с вашими параметрами подключения:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@your-host:1521:your-sid
    username: psp_user
    password: psp_password
```

## Особенности Oracle

### Типы данных
- `NUMBER(19)` для Long полей
- `VARCHAR2` для строковых полей
- `TIMESTAMP` для временных меток
- `NUMBER(1)` для boolean полей (0/1)

### Последовательности
- Используются Oracle последовательности вместо IDENTITY
- `allocationSize = 1` для оптимальной производительности
- Кэширование последовательностей для лучшей производительности

### Индексы
- Созданы индексы для часто используемых полей
- Внешние ключи для связей между таблицами
- Уникальные индексы для бизнес-ключей:
  - `transactions.transaction_id` - уникальный ID транзакции
  - `transactions.psp_transaction_id` - уникальный PSP ID транзакции
  - `transactions.receipt_id` - уникальный номер чека
  - `transactions.qr_transaction_id` - уникальный ID транзакции в QR системе
- Обычные индексы для производительности:
  - `check_requests.qr_transaction_id` - индекс для поиска по QR транзакции

## Проверка установки

```bash
# Сборка проекта
./gradlew clean build

# Запуск приложения
./gradlew bootRun
```

## Мониторинг

Для мониторинга производительности Oracle:

```sql
-- Проверка использования последовательностей
SELECT sequence_name, last_number FROM user_sequences;

-- Проверка размера таблиц
SELECT table_name, num_rows FROM user_tables;

-- Проверка индексов
SELECT index_name, table_name, status FROM user_indexes;
```
