## Why

当前全仓代码缺少相对宪章的可复现合规基线，直接跨模块重构会扩大风险并违反 change 粒度规则。需先形成证据化差距清单和可执行拆分。

## What Changes

- 审计后端、APP、契约及质量门禁，记录条款、证据、风险和验证方式。
- 建立按依赖排序的后续 OpenSpec change 清单与统一验收基线。
- 本 change 不改变产品行为，设置 `skip_specs: true`。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。

## Impact

影响治理文档与后续 change 规划；不修改 API、数据、依赖或业务代码。

## Dependencies

依赖 `constitution.md`、全部专项宪章、批准版 PRD 和现有 OpenSpec 主规格。

## Non-goals

- 不在单个 change 中重构全部模块。
- 不修改外部行为、公共契约、数据库或依赖版本。
- 不降低或绕过现有质量门禁。
