## Why
用户需要把现场照片转换成可复用的光线、空间、构图机会与安全分析。
## What Changes
- 收集现场照片、题材、对象、风格和时间限制。
- 使用 `qwen3.8-max` 返回结构化场景分析。
## Capabilities
### New Capabilities
- `shooting/scene-analysis`: 现场环境分析输入与输出。
### Modified Capabilities
无。
## Impact
影响App `scene-analysis`、服务端 `shooting` 与场景OpenAPI。
## Dependencies
`ingest-jpeg-media`、`run-ai-tasks`、`manage-user-equipment`。
## Non-goals
- 不生成拍摄方案或参考图。
- 不进行相机实时取景。
