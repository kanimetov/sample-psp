## Архитектура

- PSP Service (Spring Boot, stateless, 6–10 инстансов)
  - Sender фасад (внешние API)
  - Beneficiary фасад (/in/...)
  - OperatorClient (H‑SIGNING‑VERSION=2, JWS/JWE, mTLS)
  - Redis (идемпотентность, rate‑limit, кэш)
  - RabbitMQ (исходящие UPDATE, DLQ)
  - Oracle (транзакции, аудит, outbox)

Горячий путь: API → валидация → идемпотентность → JWS/JWE → оператор → маппинг → ответ.

Fallback: UPDATE через MQ при отсутствии финала; GET статус при timeout.


