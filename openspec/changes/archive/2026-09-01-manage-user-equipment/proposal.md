## Why
用户需要记录现有器材并设置主力相机，AI才能优先生成可执行方案。
## What Changes
- 添加、查看和删除用户机身、镜头与附件。
- 设置唯一主力相机和常用镜头并校验兼容性。
## Capabilities
### New Capabilities
- `equipment/user-equipment`: 用户器材库与主力器材规则。
### Modified Capabilities
无。
## Impact
影响App `equipment`、服务端 `equipment` 和用户器材OpenAPI。
## Dependencies
`add-equipment-catalog`、`add-password-authentication`。
## Non-goals
- 不新增器材目录项。
- 不支持转接环和相机直连。
