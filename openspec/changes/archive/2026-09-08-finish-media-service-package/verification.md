# 媒体 Service 收尾验证

遵守 Java 后端工程宪章，将 course 的媒体引用切换到规范 Service；删除 media 根包兼容 Service，目标 Service/Primary 注册启用。事务、清理接口和业务行为保持。

先运行注册/目录失败断言（`/private/tmp/media-service-finish-red.log`），迁移后 course 类型断言、Service 唯一注册、两个清理接口同实例及原事务注解断言全部通过。

Spotless apply/check 与 QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的完整 clean verify 通过；日志 `/private/tmp/media-service-finish-format.log`、`/private/tmp/media-service-finish-verify.log`。OpenSpec strict validate 和任务范围 diff --check 通过。源码无旧 Service import 或嵌套 DTO 引用，旧 class 经 clean 清理。

尚余 Entity、作品查询和清理端口，以及其他业务模块迁移；总目标未完成。未提交发布。
