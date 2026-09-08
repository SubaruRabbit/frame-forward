# media 基础分层验证

遵守 Java 后端工程宪章，以原查询语义建立 Repository，将 Controller、错误映射、Mapper、目标 Manager 放入职责目录。根包 Manager 暂为唯一 Bean 的无逻辑兼容入口，不代表 media 迁移完成。

目录测试先失败（`/private/tmp/media-foundation-red.log`），迁移后 media 定向测试通过（`/private/tmp/media-foundation-green.log`）。新增查询参数、所有权、排序和 HTTP 成功/错误映射测试。

`mvn spotless:apply spotless:check` 与 `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 全 reactor 通过，日志 `/private/tmp/media-foundation-format.log`、`/private/tmp/media-foundation-verify.log`。OpenSpec strict validate、本批 diff --check 通过。无提交发布操作。

后续必须完成 Manager 消费者切换与兼容入口删除，以及 Service、Entity、DTO、查询投影和清理端口迁移；剩余七个业务模块的总目标保持进行中。

本批最终门禁：变更行覆盖率 95.64%，分支覆盖率 96.27%，CPD 0.00%。旧 Manager 的跨模块引用已定位到 shooting、generation、evaluation 的生产源码及对应测试；下一批按每 change 最多两个模块切换，最后启用目标 Manager Bean 并删除根包入口。
