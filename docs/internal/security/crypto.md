## Cryptography

- Only H‑SIGNING‑VERSION=2 is supported.
- Signature (JWS): SHA256withRSA according to v2 rules (JSON/string canonicalization).
- Encryption (JWE): RSA‑OAEP‑256 + A256GCM for POST bodies.
- Keys: public/private keys are placed in server directory (outside repository); rights 600/700; rotation regulations.
- Operator JWKS are cached in Redis (jwks:operator:{kid}).


