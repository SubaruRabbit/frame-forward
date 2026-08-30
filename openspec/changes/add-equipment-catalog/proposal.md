## Why
AI建议必须依据准确的机身、镜头与附件事实，需先建立受控器材目录。
## What Changes
- 提供P0机身、六类镜头品牌和七类附件目录。
- 记录卡口、画幅、焦段与兼容性字段。
## Capabilities
### New Capabilities
- `equipment/catalog`: 可查询的摄影器材事实目录。
### Modified Capabilities
无。
## Impact
影响服务端 `equipment`、目录数据和查询OpenAPI。
## Dependencies
`bootstrap-workspace`、`add-password-authentication`。
## Non-goals
- 不提供目录管理后台。
- 不管理用户自己的器材。
