## Context

见 [proposal.md](./proposal.md)。服务端为 Java 21 多模块 Maven 工程，当前未提交的 `centralize-server-maven-versions` 变更已将显式版本集中到根 POM；本变更必须在其上增量修改，不能回退已有工作。

2026-09-14 通过 Maven Central 元数据和 Spring Boot 3.5.16 官方 BOM 核对版本。Boot BOM 管理的运行时组件应以框架兼容矩阵为准，独立库与构建插件采用最新稳定版，但避免无关的大版本迁移和格式化结果漂移。

## Goals / Non-Goals

**Goals:**

- 让全部模块继承 Spring Boot 3.5.16 与一致的根级版本属性。
- 在 Java 21 下验证依赖解析、编译、静态检查、单元测试和集成测试。
- 保持依赖集中管理结构，不在子模块重新声明版本。

**Non-Goals:**

- 不采用里程碑版本、Spring Boot 4 或脱离 Boot 3.5.16 兼容矩阵的数据库组件。
- 不调整业务代码、API、数据库迁移脚本、质量阈值或 Java 格式规则。

## Decisions

### 1. 框架托管组件优先与 Boot BOM 对齐

目标版本如下：

| 属性/组件 | 当前版本 | 目标版本 | 依据 |
| --- | ---: | ---: | --- |
| Spring Boot Parent | 3.5.8 | 3.5.16 | 用户指定的 3.5 系列稳定补丁版 |
| Flyway（插件与 `flyway-mysql`） | 10.20.1 | 11.7.2 | Boot 3.5.16 BOM 的 `flyway.version` |
| MySQL Connector/J（Flyway 插件依赖） | 9.4.0 | 9.7.0 | Boot 3.5.16 BOM 的 `mysql.version` |

Flyway 插件与运行时模块使用同一属性，MySQL 插件依赖使用项目属性但取 BOM 同值。备选方案是直接采用 Maven Central 最新 Flyway 13.6.0 和 MySQL 26.7.0；由于跨越 Boot 已验证的兼容边界，本次不采用。

### 2. 独立库和质量插件采用最新稳定版

| 属性/组件 | 当前版本 | 目标版本 |
| --- | ---: | ---: |
| metadata-extractor | 2.19.0 | 2.21.0 |
| JaCoCo Maven Plugin | 0.8.12 | 0.8.15 |
| Maven PMD Plugin | 3.26.0 | 3.28.0 |

以上版本由 Maven Versions Plugin 基于发布元数据识别，且不改变产品契约。备选方案是只升级 Spring Boot，但会继续保留已知的自管版本落后。

### 3. 已是最新或可能引发无关变更的版本保持不动

MyBatis Plus 3.5.17、Maven Checkstyle Plugin 3.6.0、Spotless Maven Plugin 3.10.2、Rewrite Maven Plugin 6.46.1、rewrite-static-analysis 2.41.1 与 Exec Maven Plugin 3.5.0 保持不变。Eclipse JDT 4.26 也固定不动，避免格式化器升级造成全仓 Java 文件重排。

所有版本仍只在根 POM `properties` 中声明，并由现有 `dependencyManagement` 和插件配置消费。

## Risks / Trade-offs

- [Boot 补丁升级改变传递依赖] → 比较升级前后的 effective POM/依赖树，并执行全量 Reactor 验证。
- [Flyway 10 升至 11 存在迁移兼容风险] → 采用 Boot BOM 已验证版本，运行现有 Flyway 集成测试；失败时整体回滚版本属性。
- [质量插件升级暴露新增规则或报告差异] → 不降低门禁或阈值，修复真实问题；若属于插件回归则单独记录并回退该插件。
- [当前工作区已有 POM 修改] → 仅修改目标属性，保留 `centralize-server-maven-versions` 的未提交内容并逐文件审查差异。

## Migration Plan

1. 记录当前解析版本和工作区差异。
2. 在根 POM 更新上述目标属性，不修改子模块和业务文件。
3. 检查 effective POM、依赖树、格式化和全量 Maven 门禁。
4. 若验证失败且无法在本变更范围内修复，恢复本变更涉及的版本值；不回退先前集中管理改动。
