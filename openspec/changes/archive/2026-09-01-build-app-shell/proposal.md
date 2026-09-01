## Why
Android端需要稳定的导航、会话门禁和通用页面状态，后续业务功能才能独立接入。
## What Changes
- 建立四栏导航、登录前后路由和全局错误边界。
- 提供加载、空态、失败及重试容器。
## Capabilities
### New Capabilities
- `app-shell`: Android App壳层与导航行为。
### Modified Capabilities
无。
## Impact
影响 `frame-forward-app/src/app` 与共享UI，不实现业务页面。
## Dependencies
`bootstrap-workspace`。
## Non-goals
- 不实现注册、器材或AI功能。
- 不确定完整视觉设计系统。
