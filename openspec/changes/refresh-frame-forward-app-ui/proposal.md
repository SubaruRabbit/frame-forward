## Why

当前客户端的四个一级入口和首屏功能虽可用，但视觉层级、底部导航与状态反馈尚未体现 Canva「FrameForward App UI」的摄影教练界面。现在统一应用壳与界面令牌，使核心拍摄闭环在 Android 手机上的识别与操作更清晰。

## What Changes

- 重构已登录应用壳、首页及四项底部导航的视觉层级和安全区布局。
- 建立可复用的界面令牌与基础展示组件，统一卡片、操作按钮、状态反馈和可访问性样式。
- 保持既有登录、路由、AI 工作流、接口和业务规则不变。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `app-shell`: 将已登录应用壳的视觉结构、导航反馈和无障碍交互更新为 Canva 设计所表达的移动端体验。

## Dependencies

- Canva 设计「FrameForward App UI」（`DAHU3718L34`）
- 已批准 PRD v1.0.0 与现有 `app-shell`、各功能模块公开接口

## Non-goals

- 不调整服务端、OpenAPI、鉴权、AI 业务规则或新增第三方 UI 依赖。

## Impact

- 影响 `frame-forward-app` 的主题令牌、应用壳与现有页面展示组件；不改变 API、持久化数据或服务端系统。
