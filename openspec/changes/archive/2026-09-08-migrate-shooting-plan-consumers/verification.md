# 验证记录

- Red：`/private/tmp/shooting-consumers-red.log`，旧 Mapper 类型断言失败。
- Green：generation、evaluation 生产及测试引用已全部切换规范类型，旧 import 扫描零结果；两模块类型回归测试通过。
- `mvn spotless:apply spotless:check` 通过，日志 `/private/tmp/shooting-consumers-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，含 MySQL 集成测试及覆盖率、CPD、复杂度门禁，日志 `/private/tmp/shooting-consumers-verify.log`。
- 不改变查询条件、HTTP 或数据库结构；未提交、未发布。需要回滚时仅逆向恢复本批 import 与类型测试后重跑门禁。
