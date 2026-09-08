# 媒体 Service 与 DTO 迁移验证

遵守 Java 后端工程宪章，MediaService 实现移入 service，两个响应 record 移入 model.dto，generation 同步目标 Service 和 DTO。实体构造器因跨包调用开放 public，字段、方法行为、异常、响应及事务语义不变。

路径断言先 Red（`/private/tmp/media-service-red.log`）；迁移后 media、generation 及依赖链测试通过（`/private/tmp/media-service-green.log`）。增加上传空值/损坏、重复文件复用、生成图片及进度、文件边界与清理失败、旋转和 EXIF 白名单测试。

`mvn spotless:apply spotless:check` 和基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的完整 clean verify 全部通过，日志 `/private/tmp/media-service-format.log`、`/private/tmp/media-service-verify.log`。OpenSpec strict validate 和任务范围 diff --check 通过。

只有 course 仍使用旧 Service 入口，下一批移除。Entity、查询投影、清理端口和其余模块仍待迁移；未提交发布。
