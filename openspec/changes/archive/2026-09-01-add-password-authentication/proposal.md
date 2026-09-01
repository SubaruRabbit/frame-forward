## Why
所有核心能力和AI调用都要求注册登录，需要先建立无短信依赖的账号边界。
## What Changes
- 支持用户名或邮箱注册、登录、刷新、退出和已登录改密。
- 加入密码规则、统一失败提示与受保护路由。
## Capabilities
### New Capabilities
- `identity/password-authentication`: 密码账号与会话行为。
### Modified Capabilities
无。
## Impact
影响App `auth`、服务端 `auth` 和认证OpenAPI。
## Dependencies
`bootstrap-workspace`、`build-app-shell`。
## Non-goals
- 不做短信、邮箱验证、找回密码或账号恢复。
- 不做账号注销。
