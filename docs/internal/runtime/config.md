## Конфигурации

- Пути к ключам подписи/шифрования на файловой системе (H‑SIGNING‑VERSION=2)
- Параметры Redis, RabbitMQ, Oracle
- Таймауты/ретраи/лимиты

### Operator (application.yml)

```yaml
operator:
  base-url: http://localhost:8080
  version: v1
  signing-version: "2"
  psp:
    token: <psp-token>
    id: <psp-id>
  timeout:
    connection: 5000      # ms
    read: 30000           # ms
    write: 30000          # ms
    response: 60000       # ms
```

Эти значения прокидываются в `RestConfig` и используются для настройки `HttpClient` (connect/response timeout, `ReadTimeoutHandler`, `WriteTimeoutHandler`).


