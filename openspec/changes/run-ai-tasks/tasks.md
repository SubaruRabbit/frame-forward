## 1. Task contract and persistence

- [x] 1.1 `contracts`: define AI task creation result, status query and SSE events and verify all five states validate.
- [x] 1.2 `frame-forward-server/ai-workflow`: persist task state, ownership and version trace and verify legal and illegal state transitions.
- [x] 1.3 `frame-forward-server/ai-workflow`: enforce user-scoped idempotency keys and verify duplicate requests return one task ID.

## 2. Workflow runtime

- [x] 2.1 `frame-forward-server/ai-workflow`: register configured Qwen routes and verify the selected and recorded model IDs match configuration.
- [x] 2.2 `frame-forward-server/ai-workflow`: add output schema validation with two retries and verify persistent invalid output ends in FAILED without partial result.
- [x] 2.3 `frame-forward-server/ai-workflow`: publish SSE progress and recover interrupted RUNNING tasks on startup and verify both integration paths.
- [x] 2.4 `frame-forward-app/shared/services`: persist active task IDs and query after restart and verify a simulated restart restores result or failure state.
