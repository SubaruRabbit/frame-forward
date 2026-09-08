## Context

media 对多个模块暴露 Service、Manager、Entity、查询投影与清理端口，按每 change 不超过两个模块的约束分批切换。

## Goals / Non-Goals

本批先建立 controller、manager、repository、mapper 的实际职责目录；目标 Manager 不出现 Mapper/SQL。接口、事务、权限与文件处理行为不变，不将本批视为 media 全部完成。

## Decisions

MediaRepository 原样承接查询，目标 manager.MediaManager 调用 Repository。原根包 MediaManager 仅继承目标类，保留唯一 Component Bean；不复制逻辑，媒体内服务转用目标类型。Mapper 迁入 mapper，实体暂保持现有位置，避免同时修改所有消费者。Controller 和异常映射直接迁移。

## Risks / Trade-offs

包移动可能导致扫描、类型注入或所有权过滤失效。先加失败架构测试，再验证查询参数、排序、HTTP 映射和全 reactor。临时入口不是完成状态，后续必须清除。

## Migration Plan

顺序：本批 → Service/Entity/DTO 与查询清理端口的目标实现及兼容层 → course、shooting、generation、evaluation、portfolio 各消费者批次 → media 全部根包清理与零违规断言。之后继续 ai-workflow、shooting、evaluation、course、generation、portfolio。全目标保持所有剩余模块完成，不缩减为本批。

执行 Spotless apply/check 和 QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify。回滚只逆向恢复本批包路径及构造器引用，保留前序与用户修改，再跑门禁。
