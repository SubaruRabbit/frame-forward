## Why
用户需要结合画面、EXIF和拍摄意图获得稳定、可执行的作品反馈。
## What Changes
- 输出1～100总分、分项、优缺点、参数诊断和重拍步骤。
- 支持无EXIF降级与相同内容评分复用。
## Capabilities
### New Capabilities
- `evaluation/photo-evaluation`: 照片评分、诊断与建议行为。
### Modified Capabilities
无。
## Impact
影响App `photo-review`、服务端 `evaluation` 与评分OpenAPI。
## Dependencies
`ingest-jpeg-media`、`run-ai-tasks`、`manage-user-equipment`。
## Non-goals
- 不做RAW显影或修图。
- 不比较重拍作品。
