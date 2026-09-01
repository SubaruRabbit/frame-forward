## 1. Plan generation service

- [x] 1.1 `contracts`: define complete shooting-plan fields and ranked plan response and verify two-plan and three-plan examples validate.
- [x] 1.2 `frame-forward-server/shooting`: persist plans against scene and equipment snapshots and verify plans cannot reference another user's scene.
- [x] 1.3 `frame-forward-server/ai-workflow`: implement the `qwen3.7-plus` plan Graph and verify it returns two or three ranked structured plans.
- [x] 1.4 `frame-forward-server/shooting`: validate equipment, exposure, accessory and safety rules and verify incompatible output is corrected or rejected.

## 2. Plan UI

- [x] 2.1 `frame-forward-app/features/shooting-plan`: show ranked plan cards with the recommended plan expanded and verify all required fields and starting-point labels render.
