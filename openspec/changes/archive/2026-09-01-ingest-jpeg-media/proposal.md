## Why
环境分析和作品评分都依赖可靠、私密且可追溯的JPEG导入能力。
## What Changes
- 支持Android选择、上传、解码校验与进度展示。
- 解析方向和摄影EXIF，并从AI副本移除GPS。
## Capabilities
### New Capabilities
- `media/jpeg-ingestion`: JPEG接收、校验、EXIF与本地存储行为。
### Modified Capabilities
无。
## Impact
影响App `photo-import`、服务端 `media` 和上传OpenAPI。
## Dependencies
`bootstrap-workspace`、`add-password-authentication`。
## Non-goals
- 不支持HEIF、RAW或相机自动传图。
- 不实现作品评分。
