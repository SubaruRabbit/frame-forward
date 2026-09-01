## 1. Contract and server

- [x] 1.1 `contracts`: define register, login, refresh, logout and change-password operations and verify OpenAPI validation passes.
- [x] 1.2 `frame-forward-server/auth`: add account persistence with normalized unique identifiers and verify duplicate username and email tests pass.
- [x] 1.3 `frame-forward-server/auth`: add password hashing and validation and verify valid and invalid password-policy tests pass.
- [x] 1.4 `frame-forward-server/auth`: implement renewable session issuance, rotation and revocation and verify logout and replay tests pass.
- [x] 1.5 `frame-forward-server/auth`: protect a test resource and verify missing, expired and valid session cases.

## 2. App authentication

- [x] 2.1 `frame-forward-app/features/auth`: implement registration, login and no-recovery warning screens and verify form-state tests pass.
- [x] 2.2 `frame-forward-app/shared/storage`: store and clear session credentials with Android secure storage and verify logout returns the App to login.
