## Why
用户需要通过明确验证注销账号并清除全部私有数据，且首版不提供恢复。
## What Changes
- 经密码复验与二次确认后注销并清理全部用户数据。
## Capabilities
### New Capabilities
- `identity/account-deletion`: 不可恢复的账号注销与清理编排。
### Modified Capabilities
无。
## Impact
影响App账号设置、服务端 `auth`、`media` 和账号删除OpenAPI。
## Dependencies
`add-password-authentication`、`delete-work-data`、`deliver-ai-courses`。
## Non-goals
- 不提供软删除恢复、账号恢复或自动到期。
- 不提供单作品删除；该能力由 `delete-work-data` 负责。
