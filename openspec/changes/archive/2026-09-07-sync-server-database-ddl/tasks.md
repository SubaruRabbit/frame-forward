- [x] 1. `frame-forward-server/bootstrap`：新增 V17 Flyway 迁移，统一课程交付表账户列与表级 MySQL DDL，并补充账户级外键；验证 SQL 仅追加新版本迁移。
- [x] 2. `frame-forward-server/bootstrap`：补充或调整迁移验证，覆盖空库执行、课程表结构和账户删除级联；验证相关测试可重复运行。
- [x] 3. `frame-forward-server`：执行 Spotless、单元/集成测试和完整 Maven 质量门禁；记录 MySQL 或环境前置条件导致的未执行项。

验证记录：修正后的 `frame-forward` 库已成功执行 V1–V17，重复执行 Flyway migrate 成功；V17 元数据和临时数据级联删除验证通过。`mvn -q verify` 已通过，Spotless 格式问题已按项目规则统一修复。
