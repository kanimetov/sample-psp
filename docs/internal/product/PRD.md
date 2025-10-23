## PRD: PSP (Sender/Beneficiary) по DKIBQR

### 1. Контекст и цели
- Реализовать PSP, поддерживающий исходящие переводы (Sender) и приём платежей (Beneficiary) по DKIBQR.
- Нагрузочные цели: 5k TPS пик, p95 ≤ 150 мс, доступность 99.95%.
- Инфраструктура: ВМ; Oracle DB; Redis (обязателен); RabbitMQ; H‑SIGNING‑VERSION=2 (только), JWS/JWE (RSA‑2048), mTLS.

### 2. Область (Scope)
- Включено:
  - Sender API: tx/check, tx/create, tx/execute/{transactionId}, tx/get/{transactionId} (fallback без version).
  - Beneficiary API (/in/...): приём check/create/execute/get и обязательный приём UPDATE.
  - UPDATE:
    - Входящий UPDATE от Оператора — всегда принимаем.
    - Исходящий UPDATE к Оператору — отправляем, если execute не вернул финал.
    - GET статус — только при timeout.
- Исключено: биллинг/межбанковские расчёты, фронтенд.

### 3. Акторы
- Клиентские каналы банка (мобильный/интернет‑банк) — Sender.
- Interaction Operator — контрагент.
- Операторы поддержки (мониторинг).

### 4. Процессы (высокоуровнево)
- Sender: Scan QR → Check → Create → Execute → (получаем UPDATE или GET при timeout).
- Beneficiary: Приём Check/Create/Execute → Исполнение → (если нет финала) исходящий UPDATE к Оператору.

### 5. Эндпоинты
- Внешние (PSP → клиенты)
  - POST /api/qr/tx/check
  - POST /api/qr/tx/create
  - POST /api/qr/tx/execute/{transactionId}
  - GET  /api/qr/tx/{transactionId}
- Исходящие (PSP → Operator)
  - POST /operator-api/api/v1/payment/qr/{version}/tx/check
  - POST /operator-api/api/v1/payment/qr/{version}/tx/create
  - POST /operator-api/api/v1/payment/qr/{version}/tx/execute/{transactionId}
  - GET  /operator-api/api/v1/payment/qr/{version}/tx/get/{transactionId}
  - POST /operator-api/api/v1/payment/qr/{version}/tx/update/{transactionId}
- Входящие (Operator → PSP, Beneficiary)
  - POST /in/qr/{version}/tx/check
  - POST /in/qr/{version}/tx/create
  - POST /in/qr/{version}/tx/execute/{transactionId}
  - GET  /in/qr/{version}/tx/get/{transactionId}
  - POST /qr/{version}/tx/update/{transactionId}

Примечания по заголовкам/безопасности:
- Поддерживается только H‑SIGNING‑VERSION=2. H‑HASH — JWS подпись по правилам v2.
- POST‑тела: JWE (RSA‑OAEP‑256/A256GCM) при необходимости протоколом.
- GET: как правило без JWE; подпись по v2.

### 6. Контракты данных (кратко)
- tx/check (request): qrType, merchantProvider, merchantCode, currencyCode=417, qrTransactionId, amount (tyiyns), qrLinkHash (4), customerType (1|2), extra[≤3].
- tx/check (response): beneficiaryName, transactionType, extra[].
- tx/create/execute: использовать pspTransactionId, receiptId и поля по спецификации.
- get/{transactionId} (response): { transactionId, status, transactionType, amount, commission, senderTransactionId, senderReceiptId, createdDate, executedDate }.
- update (payload): { transactionId, status, amount, commission?, pspTransactionId?, receiptId?, createdDate?, executedDate? } — финальный состав уточняется у Оператора.

### 7. Нефункциональные требования (NFR)
- 5k TPS суммарно; горизонтальное масштабирование (6–10 инстансов).
- p95 ≤ 150 мс на горячем пути.
- 99.95% доступность; устойчивость к сбоям оператора (timeouts/retry/circuit breaker/bulkhead).

### 8. Redis (обязателен)
- Идемпотентность: SET NX + TTL
  - idem:check:{pspId}:{merchantProvider}:{qrTxId}:{amount} TTL 120s
  - idem:create|execute|update:{pspTransactionId|transactionId}:{status} TTL 24h
- Rate‑limit: rl:{pspId}:{minute}, rl:tx:{transactionId}
- Кэш статуса: status:{transactionId} TTL 60s
- Кэш ключей: jwks:operator:{kid} TTL 1h; token:psp — по политике
- Блокировки: lock:update:{transactionId} TTL 30s

### 9. RabbitMQ
- Exchange: qr.tx.update; Queues: qr.tx.update.dispatch, qr.tx.update.dlq
- Ретраи: экспоненциальный backoff (15s→60s→5m→15m→1h), лимит попыток, DLQ
- Outbox: фиксация события в БД и публикация в MQ (exactly‑once на уровне домена)

### 10. Данные (Oracle)
- qr_tx (pk, psp_transaction_id unique, operator_tx_id idx, status, суммы, атрибуты QR, created_at partition)
- qr_tx_audit (append‑only, REQ/RESP, stage)
- integrations_keys (JWKS)
- outbox_events

### 11. Безопасность
- mTLS к оператору; pinning публичного ключа.
- JWS/JWE (RSA‑2048) v2; приватные/публичные ключи загружаются в директорию сервера (вне репозитория), права доступа 600/700; документировать путь/ротацию.
- Логи с маскированием PII; строгая канонизация JSON при подписи v2.

### 12. Наблюдаемость
- OpenTelemetry (traces/metrics/logs). Метрики: TPS, p95/p99, error rate, CB state, retry count, Redis/DB latency.
- Логи JSON с корреляцией traceId/pspTransactionId. Алерты по DLQ/таймаутам/доступности.

### 13. Операции и деплой
- ВМ + NGINX/HAProxy; blue/green/canary; health/readiness.
- Бэкапы: БД и ключи; DR: RPO ≤ 5 мин, RTO ≤ 30 мин.
- Управление секретами/ключами: файлы на сервере + SOP ротации.

### 14. Критерии приёмки
- Корректность check/create/execute/update/get; маппинг кодов ошибок.
- Идемпотентность и отсутствие дублей; устойчивые ретраи UPDATE.
- Нагрузочные цели достигнуты; JWS/JWE v2 проверены.

### 15. Риски/вопросы
- Возможные изменения DKIBQR; ограничения оператора (SLA/rate‑limit).
- Уточнить финальный состав полей в UPDATE payload.


