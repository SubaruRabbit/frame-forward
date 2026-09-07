## Context

现有组件只渲染回调，未调用既有照片点评、拍摄会话和重拍对比 API。

## Goals / Non-Goals

**Goals:**

通过 Feature Port 和 UseCase 编排请求，页面渲染显式加载、成功、失败与重试状态。

**Non-Goals:**

不修改 OpenAPI、服务端业务规则或持久化模型。

## Decisions

- 每个 Feature 定义小型 Application Port；网络 DTO 映射留在 Infrastructure，避免 Presentation 依赖 HTTP。
- UseCase 负责请求、防重复触发和错误向 UI state 的转换；组件只转发事件并渲染 state。
- App composition root 注入网络 Port，保留现有组件输入输出作为过渡边界。

## Risks / Trade-offs

- [重复提交] → UseCase 在进行中拒绝新触发，并由测试验证。
- [旧评分丢失] → 重分析失败时保留已展示结果并提供重试。

## Migration Plan

先添加 UseCase/组件测试，再接入页面；失败时可回退到仅展示现有回调组件。
