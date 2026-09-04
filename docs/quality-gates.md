# 质量门禁、发布与回滚

## 合并前质量门禁

`Cross-project quality gates` 工作流在每个拉取请求和 `main` 推送时运行。每个 job 都默认在失败时终止，任何失败都不得合并：

- APP：`npm run quality`，包含 Prettier、零警告 ESLint、Type Check 与 Jest。
- 服务端：`QUALITY_BASE_REF=<基线> mvn -q -DskipTests=false verify`，包含 Spotless、测试、变更代码 JaCoCo 覆盖率、PMD 圈复杂度与 CPD。
- 契约：Redocly lint、OpenAPI 兼容性检查与 Node 测试。
- 敏感信息：Gitleaks 全历史扫描。
- Git：新增提交 subject 必须符合 Conventional Commits。

本地使用 `npm run quality` 执行同一组 APP、服务端和契约门禁。服务端需要设置 `QUALITY_BASE_REF`；未设置时，本地聚合入口使用 `HEAD`，仅用于检查工作区相对当前提交的变更。

## 报告与保留

服务端 CI 始终上传 JaCoCo、PMD 与 CPD XML 报告，保留 30 天，名称包含触发提交 SHA。Android Debug APK 和受控 Release AAB 及其 SHA-256 摘要同样保留 30 天。敏感信息扫描结果不得作为普通构建产物上传；只保留受限 CI 日志和处理记录，避免二次扩散。

## 发布与回滚

正式 Android 包只能通过 `APP native build` 的手动受控 Release job 创建。该 job 受 `production` 环境保护，使用环境密钥签名，并产生可追溯的 AAB 与摘要。发布负责人必须在分发前确认：关联提交的跨项目质量门禁已通过、产物摘要匹配、版本号与变更说明正确、已知问题和负责人已记录。

发现发布问题时，按以下顺序处理：停止分发或灰度、保留失败产物和 CI 报告、评估服务端兼容性和数据影响、使用 Feature Flag 或服务端兼容逻辑止损、发布经完整门禁验证的修复版本。移动端已安装版本不能依赖覆盖式回滚，必须保留兼容和数据恢复方案。

## 例外

质量门禁不得通过跳过 job、降低阈值、扩大忽略规则或删除断言获得豁免。确实无法满足非红线条款时，必须先在 [例外登记册](constitution/exceptions.md) 中记录受影响规则、原因、风险、补偿验证、审批人、到期日、清理任务与回滚方式；获得适用负责人审批后，才可采用最小范围、可过期的例外。安全、敏感信息、数据正确性、支付和权益确认不存在例外。
