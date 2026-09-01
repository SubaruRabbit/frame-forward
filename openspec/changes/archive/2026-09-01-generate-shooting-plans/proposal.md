## Why
场景分析必须转换成适配用户器材、可以现场执行的具体拍摄步骤。
## What Changes
- 基于场景和器材生成2～3个结构化方案。
- 校验焦段、曝光、对焦、附件、姿势和安全规则。
## Capabilities
### New Capabilities
- `shooting/plan-generation`: 器材约束下的拍摄方案生成。
### Modified Capabilities
无。
## Impact
影响App `shooting-plan`、服务端 `shooting` 与方案OpenAPI。
## Dependencies
`analyze-shooting-scene`、`manage-user-equipment`。
## Non-goals
- 不生成参考图片。
- 不负责作品评分。
