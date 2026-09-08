## Context

按两模块上限继续迁移，本批仅涉及 media、generation。

## Goals / Non-Goals

MediaService 移至 service，MediaResponse、UploadProgress 移至 model.dto；媒体内调用与 generation 的生产和测试引用同步。原 Service 暂留唯一 Bean 的无逻辑兼容入口，供 course 使用。 保留全部公开方法行为、事务注解和响应字段；不将本批视为整体迁移完成。

## Decisions

响应 record 独立到 model.dto，保留字段顺序和名称。实体仍待后续独立迁移，本批只开放跨包确需调用的构造器。临时 Service 仅继承目标实现，保持单一 Bean，不复制逻辑，course 切换后删除。异常仍由服务持有，错误映射引用规范类型，业务规则不改。

## Risks / Trade-offs

包可见性、Spring 事务代理与 DTO 序列化可能回归；先增加目标路径/类型失败断言，再同步调用方。保留既有 JPEG、去重、EXIF、所有权和文件清理行为，通过对应测试和完整 reactor 验证。

## Migration Plan

依赖已完成 MediaManager 收尾 → 本批 → 后续 media Entity、作品查询和清理端口 → 其余业务模块。执行 Spotless apply/check 与 QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的 clean verify。回滚逆向恢复本批包名、引用、可见性、Bean 注解与 DTO 后重跑门禁，不改变用户数据或前序修改。
