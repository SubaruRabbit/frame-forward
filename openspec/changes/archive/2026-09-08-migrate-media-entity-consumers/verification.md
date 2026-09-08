# 验证记录

- shooting、generation 的媒体实体引用与查询调用已切换到规范包。
- Red：`/private/tmp/media-entity-consumers-red.log`。
- Spotless 格式化和检查通过：`/private/tmp/media-entity-consumers-format.log`。
- 全 reactor `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过：`/private/tmp/media-entity-consumers-verify.log`，包含消费者回归测试和 MySQL 集成验证。
