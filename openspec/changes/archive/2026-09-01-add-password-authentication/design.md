## Context
参见 `proposal.md` 和密码认证spec。首版无短信、邮箱验证与恢复流程。
## Goals / Non-Goals
**Goals:** 安全凭据、可失效会话和App门禁。
**Non-Goals:** 第三方登录与账号注销。
## Decisions
- `frame-forward-server/modules/auth` 使用Spring Security、MySQL 8.4 与 MyBatis-Plus；账号及刷新会话通过受迁移管理的表持久化，密码采用自适应带盐散列，刷新会话以散列标识持久化并支持轮换。
- access token短时有效，refresh会话由服务端撤销；退出和改密使相关refresh会话失效。
- `contracts` 定义五个认证接口；`frame-forward-app/features/auth` 保存会话，敏感令牌进入Android安全存储。
## Risks / Trade-offs
- [无恢复导致永久失去访问] → 注册和设置页明确警告，改密要求当前密码。
- [标识枚举] → 登录失败使用统一响应。
