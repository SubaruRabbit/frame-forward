# 媒体作品投影与清理端口迁移验证

按 Java 后端工程宪章，PortfolioMediaQuery 迁入 service、投影 PortfolioMediaItem 迁入 model.dto、WorkMediaCleanup 迁入 gateway；media 服务及 portfolio 生产/测试引用同步，无兼容入口。

目录断言先 Red，日志 `/private/tmp/media-portfolio-red.log`。增加投影归属、排序、空 EXIF 与无效 EXIF 回归测试；原 portfolio 删除、列表与过滤测试保留且通过。注册测试因 service 包新增查询组件补充 ObjectMapper 依赖；修复测试 Supplier 构造器重载歧义，未改生产行为。

`mvn spotless:apply spotless:check` 和 `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 全 reactor 成功，日志 `/private/tmp/media-portfolio-format.log`、`/private/tmp/media-portfolio-verify.log`。OpenSpec strict validate 与本批 diff --check 通过。无旧投影/端口 import 或嵌套 Item 引用。

media 根包只余 MediaEntity，仍需按迁移清单继续。其他业务模块亦未全部分层，总目标保持进行中。未提交发布；回滚见 design。

最终结果：188 个测试，零失败、错误、跳过；变更行覆盖率 95.84%，分支覆盖率 96.00%，CPD 0.00%，PMD 通过。待人工审查，未执行提交或发布。
