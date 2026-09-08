## Context

承接 reorganize-server-java-layer-packages 的迁移清单。用户在本轮明确要求继续后续迁移，按已确认方向实施本批。

## Goals / Non-Goals

将 shooting 切换到 auth 目标包。只涉及 auth、shooting；不调整外部行为。

## Decisions

目标包为 controller、service、business、manager、mapper、repository、model.entity、model.dto、component。AuthRepository 封装 MyBatis 查询，AuthManager 提供领域明确的数据操作和清理协调，不返回 Mapper。AccountDataCleanup 是清理适配端口，目标放 gateway 以避免 Manager 反向依赖 Service。密码校验的异常也移至 business，Service 保留现有异常语义。

Bearer 协议解析由 component.BearerToken 处理；Service 将无效结果映射成原 InvalidSessionException；Controller 通过 Service 使用。公开 DTO 抽取至 model.dto；AccessGrant 只作 Service 私有运行态，不跨层传输。

迁移期旧 AuthService 仅继承目标实现、保持单一 Bean 和原注入类型，旧 AccountDataCleanup 仅扩展新端口。不得复制业务逻辑。两者在所有消费者切换后由 finish-auth-layer-packages 删除；目标类型使用明确构造器，只有实际跨包使用的类型和成员开放可见性。

## Risks / Trade-offs

包变化可能影响 Spring/MyBatis 扫描、包可见成员和 DTO 序列化，使用全 reactor 测试与现有 HTTP 集成测试验证。迁移前后的认证与删除行为必须相同；不绕过任何门禁。

## Migration Plan

顺序：migrate-auth-layer-implementation → media → equipment → course → ai-workflow → shooting → generation → evaluation → portfolio → bootstrap 消费者批次 → finish-auth-layer-packages。每批验证 mvn spotless:apply、mvn spotless:check、QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn verify。回滚以逆序恢复本批包路径、引用及测试并重跑门禁，不回滚已有用户修改。
