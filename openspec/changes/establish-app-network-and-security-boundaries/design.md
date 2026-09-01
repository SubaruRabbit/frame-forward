## Context

参见 APP-01、APP-02：基础设施逻辑分散在 Feature 页面中，Keychain 已可复用。

## Goals / Non-Goals

**Goals:** 统一认证、超时、取消、错误转换和环境注入，并由调用方拥有小型 Port。

**Non-Goals:** 不改变服务端响应语义或全局状态策略。

## Decisions

- `shared` 提供无业务语义的 Network、安全存储和日志基础能力；Feature 自有 API 适配留在 Feature infrastructure。
- Presentation 只依赖 Application/Port，不直接依赖 `fetch`、Keychain 或硬编码 URL。
- 错误映射覆盖网络、认证、授权、校验、超时、取消和未知错误。

## Risks / Trade-offs

- [迁移遗漏] → 通过静态检查和 Feature 测试禁止页面直接访问客户端。

## Migration Plan

先建立可测边界，再由后续双 Feature changes 逐步迁移调用点；每次可独立回退。
