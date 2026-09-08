# 验证记录

2026-09-08：遵守 Java 后端工程宪章及 Git 宪章，仅移动 generation/evaluation 的四个单元测试。

- Red：规范包 ReferenceImageServiceTest 不存在，ClassNotFoundException；日志 `/private/tmp/mirror-generation-evaluation-red.log`。
- Green：四个测试类在新包执行 16 个用例，全部通过；排除 package/import/空白的测试主体 SHA-256 前后一致。
- Spotless apply/check 与完整 clean verify 成功，基线 `353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef`；变更行覆盖率 93.03%、分支 85.53%、CPD 0.00%，PMD 通过。
- 日志 `/private/tmp/mirror-generation-evaluation-format.log`、`/private/tmp/mirror-generation-evaluation-verify.log`；OpenSpec strict 校验通过。
- 未提交、合并或发布；回滚按 design 恢复本批测试路径/package 并重跑门禁。
