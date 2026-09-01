## Why
用户需要与器材和实拍任务结合的摄影课程，首版又不建设课程后台。
## What Changes
- 预生成并缓存P0基础、微单、器材和机型课程。
- 记录内容版本、资料来源、练习进度与AI作业点评。
## Capabilities
### New Capabilities
- `learning/ai-courses`: AI课程交付、版本和学习进度。
### Modified Capabilities
无。
## Impact
影响App `learning`、服务端 `course` 与课程OpenAPI。
## Dependencies
`run-ai-tasks`、`add-equipment-catalog`、`add-password-authentication`。
## Non-goals
- 不建设课程管理后台或视频课程。
- 不实现相机直连功能。
