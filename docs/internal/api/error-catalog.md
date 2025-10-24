## Error Catalog

- 400 Bad Request — validation/format
- 404 Not Found — resource does not exist
- 422 Unprocessable Entity — data is syntactically correct but invalid
- 502 Bad Gateway — general network error (SSL/Socket/Unknown host)
- 503 Service Unavailable — connection error (connection refused/reset/no route)
- 504 Gateway Timeout — request timeout (connect/read/write/response)
- 452 Recipient data incorrect — according to operator specification
- 453 Access denied — according to operator specification
- 454 Incorrect data — according to operator specification
- 455 Min amount not valid — according to operator specification
- 456 Max amount not valid — according to operator specification
- 500 System error — internal error
- 523 Supplier not available — supplier unavailability
- 524 External server not available — external unavailability

### Error Mapping Notes

- HTTP errors returned by the operator are mapped directly by status code (400/404/422/452–456/500/523/524).
- Transport errors (timeouts, connection breaks, SSL, DNS, etc.) are mapped to 504/503/502 respectively.


