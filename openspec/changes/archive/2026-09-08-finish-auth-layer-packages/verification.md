# auth 分层迁移验证记录

2026-09-07，遵守 Java 后端工程宪章、Git 宪章与已确认迁移计划，通过 openspec-apply-change 流程实施。工作区原有 common 检查工具和 auth 测试依赖已保留，未提交或发布。

## 完成范围

本轮完成 auth 分层实现、九个消费者引用切换、auth 最终清理，共 11 个串行 change。各消费者自身业务类目录未在这些 change 中迁移。

auth 当前 23 个生产 Java 文件按 controller、service、business、manager、repository、mapper、model/entity、model/dto、component、gateway 分层。公开请求及响应 DTO 独立，私有 AccessGrant 保留为服务内部运行态。Manager 不再返回 Mapper；SQL 条件封装于 Repository。AccountDataCleanup 作为适配端口位于 gateway，避免 Manager 反向依赖 Service。

根包 AuthService、AccountDataCleanup 兼容入口已删除，Controller/异常映射已移动；全部模块源码和测试已无这三个根包类型的实际引用。质量脚本的合成 diff 夹具仍使用虚拟旧路径，属于独立测试数据，不是实际源码引用。删除和移动均可按 Git 基线及本轮变更逆序恢复。

bootstrap 的 Mapper 扫描限定为已有 @Mapper 标记的接口，避免清理端口被错误注册；没有扩大扫描、改变数据表或关闭检查。

## 验证

- 先将 auth 检查改为全模块零违规，并新增独立请求 DTO 断言；迁移前按预期报告五个根包文件违规，以及目标 Controller 类缺失。完成迁移后通过。
- auth 新增 HTTP 映射测试，覆盖注册、登录、刷新、注销、修改密码、删除任务创建/查询/重试、受保护资源及四类错误响应，验证请求字段、凭据传递、响应字段和状态码。
- 九个消费者批次分别执行了完整 Maven verify，全部通过；每批变更行覆盖率 94.27%、分支 96.59%、CPD 0.00%。
- 最终执行 `mvn spotless:apply spotless:check -B -ntp`，通过。
- 最终执行 `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify -B -ntp`，通过。149 个测试，0 失败、0 错误、0 跳过。干净构建包含全部模块编译、单元测试、MySQL 容器集成与数据库迁移测试、PMD 和 CPD；变更行覆盖率 95.00%、分支覆盖率 96.59%、CPD 重复率 0.00%。
- auth 的根包源码目录和干净构建后的根包 class 目录均无直接文件；全生产源码目录检查零违规。
- `git diff --check` 通过。宪章、外部契约、数据库迁移和依赖版本未修改，未降低质量门禁。

## 尚未完成

equipment、media、ai-workflow、shooting、generation、evaluation、course、portfolio、bootstrap 自身的目录迁移仍需后续批次。下一步为 equipment 与 shooting 消费者配对；不能将本轮 auth 完成等同于服务端整体分层完成。

未执行发布、合并评审和回滚演练，因为本轮未提交或部署。回滚需逆序恢复本轮目录与调用方引用，并重新执行完整质量门禁；不得回滚原有无关修改。
