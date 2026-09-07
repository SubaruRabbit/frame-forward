## Context

基线记录显示 `mvn test` 依赖本机 MySQL，`application.yml` 含默认口令。参见合规基线 SRV-03。

## Goals / Non-Goals

**Goals:** 测试使用受控隔离存储；运行时凭据只来自环境或受控密钥来源。

**Non-Goals:** 不变更生产数据库、API 或领域规则。

## Decisions

- 为测试提供独立 profile 与可重复夹具；不复用开发库，避免测试污染和外部前置条件。
- 生产配置不提供可用默认密码；缺失配置应明确失败。
- 先为现有失败测试建立可复现 Red 基线，再最小化调整配置与夹具。
- 在服务端父 POM 中将 Spotless 配置为所有模块 `src/main/java` 和 `src/test/java` 的唯一格式化入口，使用仓库内版本控制的 Eclipse JDT XML 配置；不以本机个人 IDE 配置或局部排除绕过门禁。

## Risks / Trade-offs

- [测试与生产 SQL 差异] → 使用与 MySQL 兼容的测试方案并保留关键迁移验证。
- [配置缺失阻断启动] → 记录必需环境变量与本地安全示例。
- [格式化基线产生大范围差异] → 仅执行由 Spotless 生成的格式化变更，并通过独立任务、`spotless:check` 和完整 Maven 测试验证。

## Migration Plan

先加入隔离测试配置并验证，再移除默认凭据；随后建立并应用格式化基线。失败可回退配置改动或格式化基线，不回退安全红线。
