# 回调消费者验证

- generation、shooting 回调及测试切换 gateway/AiTaskCompletionContext，旧 AI 实体与旧回调 import 扫描零结果。
- Red：回调 superclass 仍为旧类，日志 `/private/tmp/ai-completion-consumers-red.log`。
- 原结果字段、事务注解、所有权、幂等和失败路径断言保留并通过。
- Spotless apply/check 通过，日志 `/private/tmp/ai-completion-consumers-format.log`。
- `QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef mvn clean verify` 通过，含 MySQL/Spring 集成，日志 `/private/tmp/ai-completion-consumers-verify.log`；行覆盖率 93.01%、分支覆盖率 85.53%、CPD 0.00%，复杂度门禁通过。
- 严格 OpenSpec 校验通过。遵守 Java 后端宪章，未提交发布；逆向恢复签名、引用和测试后可重跑门禁回滚。AI 旧桥接和实体待提供方收尾清理。
