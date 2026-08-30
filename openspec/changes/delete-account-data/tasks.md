## 1. Account deletion orchestration

- [ ] 1.1 `contracts`: define password-verified account deletion and job status responses and verify there is no recovery operation.
- [ ] 1.2 `frame-forward-server/auth`: create the deletion job only after current-password verification and verify a wrong password changes no data.
- [ ] 1.3 `frame-forward-server/auth`: revoke all sessions when deletion starts and verify the account cannot authenticate during cleanup.
- [ ] 1.4 `frame-forward-server`: add an architecture test requiring every user-owned table to declare cascade or cleanup behavior and verify all modules pass.
- [ ] 1.5 `frame-forward-server/media`: enumerate and idempotently delete all media owned by the account and verify shared files remain untouched.
- [ ] 1.6 `frame-forward-server/auth`: complete account removal only after database and file cleanup succeed and verify a failed step remains retryable without reporting completion.

## 2. Account UI

- [ ] 2.1 `frame-forward-app/features/settings`: add password re-entry, second confirmation and final sign-out and verify canceling either confirmation preserves the account.
