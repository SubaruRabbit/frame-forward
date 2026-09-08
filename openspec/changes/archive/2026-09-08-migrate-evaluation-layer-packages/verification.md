# evaluation 分层迁移验证

## 完成范围

evaluation 原 18 个根包生产文件按职责分包，提取三个请求 DTO、六个用例异常和两个作品查询 DTO，新增两个 Repository 与会话 Manager。portfolio 同步查询、清理接口和 DTO 引用，无旧根包兼容入口。

## 验证证据

- Red：`/private/tmp/evaluation-layer-red.log`，目录违规与规范 Service 缺失断言失败。
- 定向测试通过：`/private/tmp/evaluation-layer-test.log`。持久化边界检查按 MyBatis BaseMapper 类型判定，避免误判 JSON ObjectMapper。
- 新增会话创建/请求/所有权、作品查询空投影/异常、缓存版本/所有权/完成候选过滤、清理顺序及失败传播、HTTP 200/201/202/400/404 测试通过。
- Spotless apply/check 通过：`/private/tmp/evaluation-layer-format.log`。
- 全 reactor `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过：`/private/tmp/evaluation-layer-verify.log`，包含 MySQL 集成测试。
- 变更行覆盖率 94.59%，分支覆盖率 88.43%，PMD CPD 重复率 0.00%；复杂度门禁通过。
- 严格 OpenSpec 校验、任务范围 diff 空白检查通过；旧根包和嵌套类型引用无残留；Service/Business/Manager 无 MyBatis 或 Mapper 导入。

## 交付边界

遵守 Java 后端工程宪章与 Git 宪章。未提交发布，未变更数据库或真实用户数据，保留用户已有修改。剩余 shooting、ai-workflow 两个模块继续迁移。
