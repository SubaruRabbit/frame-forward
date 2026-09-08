# generation 分层迁移验证

## 完成范围

原 8 个根包生产文件已重组为 13 个分层文件，无兼容入口。请求和持久化 DTO、业务异常已独立；Repository 隔离 Mapper，Manager 不再反向调用 MediaService；原事务完成处理器保留事务边界并承担媒体结果编排。

## 验证

- Red：`/private/tmp/generation-layer-red.log`，目录违规与规范 Manager 缺失的断言失败。
- 迁移后定向测试通过：`/private/tmp/generation-layer-test.log`。
- 新增完成处理器操作支持/事务/失败分支、HTTP 202/400/404、所有权和任务过滤条件测试。HTTP 测试补齐认证请求头后通过，未改认证逻辑。
- Spotless apply/check 通过：`/private/tmp/generation-layer-format.log`。
- 全 reactor `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过：`/private/tmp/generation-layer-verify.log`，包含 MySQL 集成验证。
- 变更行覆盖率 95.78%，分支覆盖率 93.70%，PMD CPD 重复率 0.00%；复杂度门禁通过。
- `openspec validate migrate-generation-layer-packages --strict` 通过，任务范围 `git diff --check` 通过。
- 旧根包及嵌套类型引用审计无残留；Business/Manager 无 Mapper、MyBatis 或反向 Service 依赖。

## 交付边界

遵守 Java 后端宪章及 Git 宪章。未提交发布，保留用户原有修改。剩余 ai-workflow、shooting、evaluation、portfolio 四个模块继续迁移。
