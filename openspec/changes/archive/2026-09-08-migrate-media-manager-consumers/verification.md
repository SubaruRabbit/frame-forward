# Manager 消费者切换验证

按 Java 后端工程宪章，shooting、generation 的生产及测试引用切换至 manager.MediaManager，未修改业务逻辑。新增类型断言先 Red（reactor 在 shooting 的旧类型断言失败），切换后两个模块类型断言及原业务测试通过。

Spotless apply/check 与基线 353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef 的完整 clean verify 通过。日志为 `/private/tmp/media-manager-consumers-red.log`、`/private/tmp/media-manager-consumers-format.log`、`/private/tmp/media-manager-consumers-verify.log`。OpenSpec strict validate 通过。未提交发布；evaluation 引用和根包兼容入口由下一批清理。
