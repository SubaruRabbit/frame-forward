## Context

按已批准迁移清单继续 equipment，唯一跨模块消费者为 shooting。

## Goals / Non-Goals

目标：根包无生产 Java，Controller/Service 的跨层 DTO 独立于 model.dto，Mapper 公共类型各占一文件，Manager 不依赖 MyBatis，Business 不反向依赖 Service。非目标：改变接口、数据或器材兼容性规则。

## Decisions

迁入 controller、service、business、manager、repository、mapper、model.entity、model.dto。EquipmentRepository 封装查询；EquipmentManager 代理数据入口与主相机更新协调。器材类型存在性规则保留业务层。DTO 只携带数据与纯字段转换；业务异常放 business，避免 Business 反向依赖 Service。只开放跨包确实需要的成员，保持事务注解和 Mapper 注解。

## Risks / Trade-offs

包可见性、扫描、序列化与查询语义可能回归。先增加目录及边界失败测试，再迁移；使用单测和既有 HTTP 集成测试验证。

## Migration Plan

先测试，再迁移 equipment 并同步 shooting；Spotless 后执行完整 clean verify（QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef）。验收零目录违规、无反向依赖、全门禁通过。回滚仅逆向恢复本批包名和消费者引用，保留前序及用户修改，然后重跑完整门禁。
