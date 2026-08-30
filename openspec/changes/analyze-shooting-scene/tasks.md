## 1. Scene analysis service

- [ ] 1.1 `contracts`: define scene-analysis input and structured result schemas and verify required fields reject an image-less request.
- [ ] 1.2 `frame-forward-server/shooting`: persist scene requests with owned media and equipment snapshots and verify cross-user media is rejected.
- [ ] 1.3 `frame-forward-server/ai-workflow`: implement the `qwen3.8-max` scene Graph and verify a fixture produces schema-valid light, space and composition fields.
- [ ] 1.4 `frame-forward-server/shooting`: filter unsafe candidate positions and verify roadway and edge fixtures produce warnings without recommendations.

## 2. Scene UI

- [ ] 2.1 `frame-forward-app/features/scene-analysis`: implement the input form, task progress and result cards and verify the flow never renders raw model text.
