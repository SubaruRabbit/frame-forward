# portfolio 分层迁移验证

## 完成范围

原 10 个根包生产文件已重组为 17 个分层文件，无兼容入口。DTO/异常独立，筛选规范化留在 Service；Repository 隔离收藏与删除任务 Mapper，Manager 保留原清理协调和失败重试状态。

## 验证证据

- Red：`/private/tmp/portfolio-layer-red.log`，目录违规及规范 Manager 缺失断言失败。
- 迁移后定向测试通过：`/private/tmp/portfolio-layer-test.log`。
- 新增筛选上下限/默认值/空白参数、游标、所有权、HTTP JSON/200/202/404、删除失败与重试调用顺序、Repository 查询条件测试，全部通过。
- Spotless apply/check 通过：`/private/tmp/portfolio-layer-format.log`。
- 全 reactor `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过：`/private/tmp/portfolio-layer-verify.log`，包含 MySQL 集成测试。
- 变更行覆盖率 95.96%，分支覆盖率 92.90%，CPD 重复率 0.00%；PMD 复杂度门禁通过。
- `openspec validate migrate-portfolio-layer-packages --strict` 和任务范围 `git diff --check` 通过。
- 旧根包 import/嵌套 DTO 引用无残留；Business/Manager 无 Mapper/MyBatis 或反向 Service 依赖。

## 交付边界

遵守 Java 后端工程宪章与 Git 宪章。未提交发布、未修改数据库或删除真实用户数据，保留用户已有配置修改。剩余 ai-workflow、shooting、evaluation 三个模块继续迁移。
