## Why
文字方案需要可选的视觉参考，帮助用户理解目标构图、机位和氛围。
## What Changes
- 基于现场图和已选方案调用 `qwen-image-3.0-pro`。
- 保存参考图并展示真实性边界说明。
## Capabilities
### New Capabilities
- `generation/reference-image`: 拍摄参考图生成与失败降级。
### Modified Capabilities
无。
## Impact
影响App `reference-image`、服务端 `generation` 与生成OpenAPI。
## Dependencies
`generate-shooting-plans`、`run-ai-tasks`、`ingest-jpeg-media`。
## Non-goals
- 不把参考图作为真实结果或唯一评分标准。
- 不提供通用图片编辑器。
