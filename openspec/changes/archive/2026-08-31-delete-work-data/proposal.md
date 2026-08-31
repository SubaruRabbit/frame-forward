## Why
用户图片默认持续保存，因此作品级删除必须真实移除文件与关联分析，而不是只从列表隐藏。
## What Changes
- 删除单张作品的原图、派生图、EXIF和关联AI结果。
- 对失败清理保留可重试记录并返回真实状态。
## Capabilities
### New Capabilities
- `portfolio/work-deletion`: 单作品及其私有派生数据删除。
### Modified Capabilities
无。
## Impact
影响App `portfolio`、服务端 `portfolio`、`media` 和删除OpenAPI。
## Dependencies
`browse-portfolio`、`ingest-jpeg-media`、`evaluate-photos`。
## Non-goals
- 不注销账号或删除其他作品。
- 不删除共享器材目录和课程内容。
