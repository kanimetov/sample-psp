## Key Management

- Storage: public/private keys are placed in server directory (outside VCS). Rights: 600/700. Path is set via config.
- Rotation: planned key replacement; binary compatibility during rotation period; expiration monitoring.
- Operator JWKS: periodic fetch and cache in Redis; pinning by KID.


