## Context

GOV-02、OPS-01 显示聚合脚本遗漏门禁且无 CI。
## Goals / Non-Goals
**Goals:** 在受控 CI 阻断不合规变更并输出可追溯报告。
**Non-Goals:** 不以跳过检查或放宽阈值通过。
## Decisions

- 复用各项目本地命令，CI 只编排并保留报告。
- 提交信息按 Conventional Commits 校验。
## Risks / Trade-offs

- [外部服务不稳定] → 只运行隔离测试，集成环境单独管理。
## Migration Plan

先只读执行比对，再设为合并阻断；可回退工作流但不删除本地门禁。
