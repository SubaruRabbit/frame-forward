## Context
参见 `proposal.md`。业务features尚未实现，壳层只提供挂载点和一致状态。
## Goals / Non-Goals
**Goals:** 四栏导航、会话门禁、路由恢复和通用页面状态。
**Non-Goals:** 业务页面与完整品牌视觉。
## Decisions
- `frame-forward-app/app` 持有根导航和会话门禁；各feature仅导出路由入口。
- 使用持久化的顶层路由键，恢复前先验证会话与路由白名单。
- 通用状态容器放入 `shared/components`，feature传入文案和重试动作。
## Risks / Trade-offs
- [未来深链路改变导航] → 本change只稳定顶层路由，深链另行规划。
