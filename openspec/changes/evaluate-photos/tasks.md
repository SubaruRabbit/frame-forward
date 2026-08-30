## 1. Evaluation service

- [ ] 1.1 `contracts`: define evaluation request, ordered result sections and EXIF-limit indicators and verify representative responses validate.
- [ ] 1.2 `frame-forward-server/evaluation`: persist scoring rule versions, weighted dimensions and result sections and verify totals remain within 1–100.
- [ ] 1.3 `frame-forward-server/ai-workflow`: implement the `qwen3.8-max` evaluation Graph and verify visual-only and EXIF-aware fixtures both produce valid output.
- [ ] 1.4 `frame-forward-server/evaluation`: reuse `(user, content hash, rule version)` results and verify duplicates avoid a second AI task.
- [ ] 1.5 `frame-forward-server/evaluation`: label uncertain technical causes and verify ambiguous blur fixtures never return an absolute cause.

## 2. Review UI

- [ ] 2.1 `frame-forward-app/features/photo-review`: render the PRD-defined result order and reanalysis action and verify missing EXIF shows a limitation without hiding visual feedback.
