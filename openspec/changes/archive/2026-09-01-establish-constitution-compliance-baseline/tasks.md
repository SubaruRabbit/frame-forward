## 1. 建立审计基线

- [x] 1.1 `docs/constitution`：创建全仓合规矩阵，固定条款、适用区域、证据、状态、风险、整改边界和验证方式字段，并通过 Markdown lint 或人工表格完整性检查确认模板可用。
- [x] 1.2 `frame-forward-server`：对 Maven 配置、模块依赖、分层、测试、安全、可观测性和质量门禁执行只读审计，将可复现命令及文件/符号证据写入矩阵，并复跑所有引用命令确认结果一致。
- [x] 1.3 `frame-forward-app`：对 Feature 边界、状态与数据流、网络/存储、安全隐私、无障碍、测试和质量门禁执行只读审计，将可复现证据写入矩阵，并复跑所有引用命令确认结果一致。
- [x] 1.4 `contracts` 与 `scripts`：审计 OpenAPI 单一事实源、跨端兼容、校验脚本和提交/发布门禁，将可复现证据写入矩阵，并执行现有契约与聚合检查记录基线结果。

## 2. 拆分整改计划

- [x] 2.1 `openspec/CHANGE-PLAN.md`：按风险和依赖把已确认差距拆成后续 changes，确保每项只有一个主能力、最多涉及两个服务端模块或两个 APP feature，并逐项标明条款、前置依赖和验收命令。
- [x] 2.2 `openspec`：交叉检查矩阵中的每个非合规项都映射到一个后续 change 或有时限的例外记录，随后运行 `openspec validate establish-constitution-compliance-baseline` 并确认通过。
