## 1. Work deletion

- [ ] 1.1 `contracts`: define owned-work deletion status and error responses and verify OpenAPI validation passes.
- [ ] 1.2 `frame-forward-server/portfolio`: persist an idempotent deletion job and deny product reads once deletion starts; verify repeated requests return the same job.
- [ ] 1.3 `frame-forward-server/evaluation`: expose work-result cleanup and verify only results bound to the target work are removed.
- [ ] 1.4 `frame-forward-server/media`: expose idempotent media cleanup and verify original plus derivative fixtures are deleted while unrelated media remains.
- [ ] 1.5 `frame-forward-app/features/portfolio`: add delete confirmation and job status feedback and verify cleanup failure is not shown as success.
