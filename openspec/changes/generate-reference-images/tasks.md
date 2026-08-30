## 1. Reference generation

- [ ] 1.1 `contracts`: define reference-image task input and result metadata and verify foreign scene or plan IDs map to an ownership error.
- [ ] 1.2 `frame-forward-server/generation`: build a controlled prompt from the selected plan and verify unit fixtures preserve structure, light and viewpoint fields.
- [ ] 1.3 `frame-forward-server/ai-workflow`: register the `qwen-image-3.0-pro` workflow and verify a successful output becomes an owned media resource linked to the plan.
- [ ] 1.4 `frame-forward-server/generation`: isolate generation failure from plan state and verify a failed image task leaves the plan readable and retryable.
- [ ] 1.5 `frame-forward-app/features/reference-image`: render generation progress, result and three disclosure points and verify failed generation returns to the text plan.
