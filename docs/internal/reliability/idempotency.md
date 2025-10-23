## Идемпотентность

- Redis SET NX + TTL ключи:
  - idem:check:{pspId}:{merchantProvider}:{qrTxId}:{amount}
  - idem:create|execute|update:{pspTransactionId|transactionId}:{status}
- Дубли UPDATE: не изменять терминальный статус; отвечать 200.


