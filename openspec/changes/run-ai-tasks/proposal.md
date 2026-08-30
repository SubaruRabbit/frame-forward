## Why
多个AI能力需要共享稳定的异步状态、重试、模型路由和恢复机制。
## What Changes
- 建立确定性AI任务状态机和节点记录。
- 提供Qwen路由、结构校验、重试、SSE进度与任务查询。
## Capabilities
### New Capabilities
- `ai/task-runtime`: AI工作流任务运行与可追溯行为。
### Modified Capabilities
无。
## Impact
影响App共享任务服务、服务端 `ai-workflow`、`usage` 和任务OpenAPI。
## Dependencies
`bootstrap-workspace`、`add-password-authentication`。
## Non-goals
- 不实现任何具体摄影AI工作流。
- 不设计生产队列或部署。
