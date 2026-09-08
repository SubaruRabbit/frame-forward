# course 迁移验证

## 完成范围

原 11 个根包生产文件重组为 23 个分层文件；Controller、Service、Business、Manager、Repository、Mapper、DTO、Entity、静态目录组件均已归位，无兼容入口。bootstrap 课程集成测试导入同步更新。

## 验证证据

- Red：`/private/tmp/course-layer-red.log`，目录违规和缺失规范 Manager 类的断言失败。
- 首轮迁移定向测试通过：`/private/tmp/course-layer-test.log`。
- 新增编排、协议映射及 SQL 条件测试；修正 Mockito 重设桩触发旧回调的测试问题后全量通过。
- `mvn spotless:apply spotless:check` 通过：`/private/tmp/course-layer-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 全 reactor 通过：`/private/tmp/course-layer-verify.log`，包含 MySQL 课程交付和版本迁移集成测试。
- 变更行覆盖率 95.59%，分支覆盖率 95.67%，CPD 重复率 0.00%，PMD 复杂度门禁通过。
- `openspec validate migrate-course-layer-packages --strict` 通过。
- 旧根包 import / 嵌套 DTO 引用审计无残留；Business、Manager 不包含 Mapper/MyBatis 或反向 Service 依赖；任务范围 `git diff --check` 通过。

## 交付边界

遵守 Java 后端工程宪章和 Git 宪章，未提交、发布或操作数据库结构。保留用户原有格式配置修改。整体迁移仍有 ai-workflow、shooting、generation、evaluation、portfolio 五个模块，不将本批视为整体完成。
