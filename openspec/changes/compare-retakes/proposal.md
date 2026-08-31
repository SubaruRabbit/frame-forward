## Why
评分闭环需要说明重拍具体改善了什么，而不能只显示分数涨跌。
## What Changes
- 建立原作与重拍作品关系。
- 以已选拍摄方案创建受用户约束的最小拍摄任务，并将参评作品归入该任务。
- 对比构图、参数、已改善问题和下一步练习。
## Capabilities
### New Capabilities
- `shooting/retake-comparison`: 两次作品的结构化进步对比。
### Modified Capabilities
无。
## Impact
影响App `shooting-session`、服务端 `evaluation` 与对比OpenAPI；新增仅服务于重拍闭环的拍摄任务归属。
## Dependencies
`evaluate-photos`、`generate-shooting-plans`。
## Non-goals
- 不改写单张作品评分规则。
- 不生成成长排行榜。
- 不提供通用拍摄任务管理、多人协作或跨方案合并。
