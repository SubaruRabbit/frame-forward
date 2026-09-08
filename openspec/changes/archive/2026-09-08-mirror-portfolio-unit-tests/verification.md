# 验证记录

2026-09-08：遵守 Java 后端工程宪章及 Git 宪章，仅移动 portfolio 三个单元测试并新增目录回归。

- Red：规范包 PortfolioBusinessTest 不存在，ClassNotFoundException；日志 `/private/tmp/mirror-portfolio-red.log`。
- Green：business/manager/service 三个测试类分别执行 3/2/4 个用例，全部通过；排除 package/import/空白的测试主体 SHA-256 前后一致。
- Spotless apply/check 与完整 clean verify 成功，基线 `353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef`；267 个测试，0 失败、0 错误、0 跳过。变更行覆盖率 93.03%、分支 85.53%、CPD 0.00%，PMD 通过。
- 日志 `/private/tmp/mirror-portfolio-format.log`、`/private/tmp/mirror-portfolio-verify.log`；43 个未归档迁移 change 严格校验全部通过。
- 未提交、合并或发布；回滚按 design 恢复本批测试路径/package 并重跑门禁。
