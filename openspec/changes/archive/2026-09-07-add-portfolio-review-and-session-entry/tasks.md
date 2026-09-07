## 1. Portfolio workflow selection

- [x] 1.1 In `frame-forward-app/features/portfolio`, extend the owned work-detail UI model and selection state for media, plan, session, and distinct same-session completed evaluation pairs; verify focused unit tests cover valid selections and every unavailable reason.
- [x] 1.2 In `frame-forward-app/features/portfolio`, add accessible detail actions that emit only validated reanalysis, session-creation, and comparison intents; verify component tests assert the emitted minimal identifiers and that unavailable actions cannot submit.

## 2. Public flow handoff

- [x] 2.1 In `frame-forward-app` public feature contracts, consume the inputs made available by `connect-photo-review-and-shooting-session-flows` and define the Portfolio-to-App navigation intent boundary; verify TypeScript compilation prevents importing target-flow internals from Portfolio.
- [x] 2.2 In `frame-forward-app/app`, compose the Portfolio intents with the photo-review and shooting-session public flows, preserving target-flow loading, failure, retry, and success ownership; verify integration tests exercise each handoff with its expected identifiers.

## 3. Quality verification

- [x] 3.1 In `frame-forward-app`, run `npm run quality` after the dependent flow change is applied and verify format checking, zero-warning linting, type checking, and all Jest suites pass.
