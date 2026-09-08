# MediaManager 目标包收尾验证

按 Java 后端工程宪章完成 evaluation 消费者切换，删除 media 根包兼容 Manager，将 Component 注册迁入 manager.MediaManager。查询与业务逻辑未改，Service 测试 mock 同步目标类型。

根包存在性断言先 Red，日志 `/private/tmp/media-manager-finish-red.log`。迁移后目标扫描测试证明只注册一个规范类型的 Manager Bean，evaluation、shooting、generation 的类型断言与业务回归全部通过；源码中不再存在旧 Manager import。

`mvn spotless:apply spotless:check` 和 `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 全 reactor 成功；日志 `/private/tmp/media-manager-finish-format.log`、`/private/tmp/media-manager-finish-verify.log`。OpenSpec strict validate 和任务范围 diff --check 通过。

未提交发布；本批仅完成 Manager 收尾。接下来迁移 media 的 Service/DTO（显式 DTO 外部引用位于 generation 测试），再切换 course 的 Service 引用；Entity、查询投影和清理端口仍需后续批次。整体七个业务模块迁移目标仍未完成，不以本批替代总目标。

最终结果：174 个测试，零失败、错误、跳过；变更行覆盖率 95.64%，分支覆盖率 96.27%，CPD 0.00%，PMD 通过。旧入口删除后已执行 clean，验证不依赖旧 class 残留。
