# bootstrap 迁移验证

- 按 Java 后端工程宪章及已确认的迁移清单，将 AccountDeletionConfiguration 移至 config，AccountDatabaseCleanup 移至 repository；启动类保留根包。SQL、账户参数、执行顺序、扫描和显式 Bean 装配不变。
- Red：BootstrapPackageInventoryTest 检出原根包违规，日志 `/private/tmp/bootstrap-layer-red.log`。
- Green：目录、装配、清理 SQL 顺序与参数、失败传播及删除架构测试全部通过，日志 `/private/tmp/bootstrap-layer-green.log`。
- 最终执行 `mvn spotless:apply spotless:check` 和 `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify`，完整 12 项 reactor 全部成功。
- 166 个测试，零失败、错误、跳过；含账号删除及器材 HTTP/MySQL 集成测试。变更行覆盖率 95.27%，分支 96.27%，CPD 0.00%，PMD 通过。
- 日志 `/private/tmp/bootstrap-layer-format.log`、`/private/tmp/bootstrap-layer-verify.log`。OpenSpec strict validate 与本批 diff --check 通过。
- 未执行提交与发布，待人工审查。原有 formatter XML 尾随空白未触碰；其他业务模块的目录迁移仍待后续批次。
- 回滚仅逆向恢复本批路径与测试引用并重跑门禁，不涉及数据库内容恢复或删除。
