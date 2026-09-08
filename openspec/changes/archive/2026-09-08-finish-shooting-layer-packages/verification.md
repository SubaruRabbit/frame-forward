# shooting 分层收尾验证

## 实现范围

- 仅修改 shooting：Controller / Service / Business / Manager / Mapper / Entity 归入对应目录；两个请求 DTO、五个业务异常独立成类。
- ShootingRepository 负责原有查询及保存，Manager 保留场景与方案的去重、创建协调。业务层不再反向引用 Service 内嵌类型。
- 删除两个临时根包 ShootingPlan 类型及 legacyShootingPlanMapper bean；规范实体和 Mapper 保留。没有修改 HTTP、数据库字段、SQL 条件和事务边界。
- 增加目录零违规测试，将 Manager 纳入禁止直接依赖 Mapper 的架构断言；保留原业务与完成回调测试。

## 验证证据

- Red：目录测试在迁移前报告根包违规，日志 `/private/tmp/shooting-finish-red.log`。
- `mvn spotless:apply spotless:check` 通过，日志 `/private/tmp/shooting-finish-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，日志 `/private/tmp/shooting-finish-verify.log`。
- 全 reactor 230 个测试，失败、错误、跳过均为 0，包括 MySQL/Spring 集成验证。
- 变更行覆盖率 93.25%，分支覆盖率 86.96%，CPD 0.00%，PMD 圈复杂度门禁通过。
- 全服务端 Java 扫描无旧 shooting 根包类型引用或临时 Mapper bean；Business/Manager 无 MyBatis 和反向 shooting.service 引用；本批范围 `git diff --check` 通过。
- `openspec validate finish-shooting-layer-packages --strict` 通过。

## 交付与回滚

遵守 Java 后端专项宪章；依 OpenSpec 任务分批实施。未提交、未发布，人工评审与发布验收未执行，不将本地验证视为发布批准。回滚按逆序恢复本批类型路径、引用、Repository 提取与对应测试后重跑完整门禁，不触碰数据库和用户已有修改。

本批完成不代表所有模块完成：ai-workflow 尚待迁移。
