## Context

APP-01、APP-02 要求设备和网络经 Port 隔离。
## Goals / Non-Goals
**Goals:** 页面只渲染认证与上传状态。
**Non-Goals:** 不修改会话或媒体契约。
## Decisions

- UseCase 管理凭据、选择器和上传副作用。
## Risks / Trade-offs

- [上传恢复回归] → 覆盖取消、失败、重试和进度。
## Migration Plan

分别迁移并回归。
