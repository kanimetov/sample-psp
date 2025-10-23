## Каталог ошибок

- 400 Bad Request — валидация/формат
- 404 Not Found — ресурс не существует
- 422 Unprocessable Entity — данные корректны синтаксически, но невалидны
- 502 Bad Gateway — общая сетевая ошибка (SSL/Socket/Unknown host)
- 503 Service Unavailable — ошибка подключения (connection refused/reset/no route)
- 504 Gateway Timeout — таймаут запроса (connect/read/write/response)
- 452 Recipient data incorrect — по спецификации оператора
- 453 Access denied — по спецификации оператора
- 454 Incorrect data — по спецификации оператора
- 455 Min amount not valid — по спецификации оператора
- 456 Max amount not valid — по спецификации оператора
- 500 System error — внутренняя ошибка
- 523 Supplier not available — недоступность поставщика
- 524 External server not available — внешняя недоступность

### Примечание по маппингу ошибок

- HTTP‑ошибки, возвращённые оператором, маппятся напрямую по коду статуса (400/404/422/452–456/500/523/524).
- Транспортные ошибки (таймауты, разрывы соединения, SSL, DNS и т. п.) маппятся в 504/503/502 соответственно.


