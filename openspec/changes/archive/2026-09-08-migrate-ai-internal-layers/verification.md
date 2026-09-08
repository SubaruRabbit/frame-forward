# AI 内部分层验证

- 仅 ai-workflow：五个确定性规则类归 business，Manager/Mapper 归对应目录，Repository 隔离数据访问；保留原更新时间、查询条件和状态规则。
- Red：目标 business.SceneAnalysisGraph 不存在，日志 `/private/tmp/ai-internal-red.log`。
- 新增 Repository 条件/参数与写入测试、场景安全和方案边界测试。测试元数据配置启用实际使用的驼峰映射，不改变列名断言。
- 完整门禁曾报告分支覆盖率 77.19%；补充真实规则分支测试后通过，未调整门禁。
- `mvn spotless:apply spotless:check` 通过，日志 `/private/tmp/ai-internal-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，含 MySQL 集成验证，日志 `/private/tmp/ai-internal-verify.log`。
- 最终变更行覆盖率 93.55%、分支覆盖率 85.93%、CPD 0.00%，复杂度门禁通过。
- `openspec validate migrate-ai-internal-layers --strict` 通过；本批 `git diff --check` 通过。
- 遵守 Java 后端宪章；未提交发布。回滚仅恢复本批路径、引用和 Repository 提取后重跑门禁，不触碰数据库及用户工作。
- 尚余根包 Runtime/DTO、Business、Controller/异常映射、Entity/完成回调和 RetakeComparisonGraph；本批不代表整体完成。
