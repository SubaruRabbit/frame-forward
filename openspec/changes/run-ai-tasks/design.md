## Context
具体AI能力共享任务生命周期，但本change不定义摄影提示词。
## Goals / Non-Goals
**Goals:** 可恢复任务、Qwen路由、结构校验、重试和追踪。
**Non-Goals:** 生产消息队列与具体工作流。
## Decisions
- `ai-workflow` 模块在PostgreSQL保存任务事实，以本地执行器运行Graph节点；Redis只加速进度和技术限流。
- 每个workflow注册输入/输出schema、模型路由和最多2次结构重试；Qwen模型ID只来自配置。
- `contracts` 提供创建结果中的task ID、SSE订阅和查询接口；App共享task service持久化未完成ID。
- 同一幂等键在用户与操作类型范围内唯一。
## Risks / Trade-offs
- [进程终止中断本地任务] → 启动时将可重试RUNNING任务恢复为QUEUED。
- [模型降级改变质量] → 只允许显式配置且已评测的Qwen备用项，并记录实际ID。
