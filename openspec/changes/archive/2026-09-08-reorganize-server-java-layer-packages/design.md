## Context

动机见 proposal.md。当前 auth、equipment、media、ai-workflow、shooting、evaluation、generation、course、portfolio 的生产类混放在业务根包。bootstrap 同包包含启动、配置和清理实现；common 当前无 Java 源码。

AuthArchitectureTest 通过反射检查部分字段类型，没有校验源文件目录或 package。其他模块 Controller 还引用 AuthController.bearer，因此不能只移动文件而不分析调用方。

## Goals / Non-Goals

本 change 仅交付 common 的测试辅助工具、auth 中对现状的检测证据和后续迁移清单。全服务端迁移是总体目标，须拆成后续独立 change；本批不将现状检测通过等同于分层合规。

## Decisions

### 目录映射

在业务模块下保留 com.frameforward.<业务>，再按职责建立以下子包；这是对宪章职责的具体落地方案，并非宪章原文已有的目录模板。

| 职责 | 目标子包 |
| --- | --- |
| Controller、HTTP 异常映射 | controller |
| 用例、简单规则 | service |
| Business、Policy、Rules、业务校验 | business |
| Manager、复合流程协调 | manager |
| MyBatis Mapper | mapper |
| 持久化 Entity | model.entity |
| 跨层请求、响应和中间数据 | model.dto |
| 纯转换 | converter |
| 外部服务适配 | gateway / rpc |
| 通用技术组件 | component |
| Spring 装配 | config |

不建立空层或无职责的转发类。Graph、CompletionProcessor、Query、Cleanup 必须逐类按实际职责归类，不能仅按后缀搬迁。启动类允许在 bootstrap 根包；测试原则上镜像被测层，跨模块集成测试集中保留在 bootstrap。

### 检查方式与本批验收

common 提供测试专用检查工具，读取 Java 源文件路径和 package，报告错位、根包混放及角色不匹配；保持生产依赖不变。先用合法和非法夹具执行 Red → Green，覆盖目录/package 不一致、根包实体、错误层 Mapper 和正确分层。

auth 测试调用该工具，验证能明确识别当前 AuthController、AuthService、AuthBusiness、AuthManager、AccountEntity、AccountMapper 的混放位置。这是迁移前的现状测试，不是忽略名单，也不替换既有架构测试。后续 auth 迁移批次必须将它替换成零违规断言，不得长期保留现状断言作为合规证据。

选择小型测试工具，避免为路径检查引入新的生产框架；既有依赖方向测试继续执行。最终检查还须覆盖嵌套 DTO 和跨层依赖，不能仅靠类名断言宣称架构合规。

## Risks / Trade-offs

- 包迁移会影响 import、包可见成员、反射类名和 Spring/MyBatis 扫描 → 后续每批先记录引用清单并验证全部消费者，不能通过无差别 public 化解决。
- 多模块依赖导致一次搬迁超出两个模块 → 先拆分消费者对协议辅助方法和嵌套 DTO 的依赖，再迁移声明；每个后续 change 提案必须列出实际修改模块，不只统计被移动模块。
- 目录检查不能证明职责正确 → 保留依赖检查，人工复核规则、流程、持久化职责。
- 本批尚未消除混放 → 交付明确列出未完成模块，不能标记总体目标完成。

## Migration Plan

1. 本批完成检查工具和 auth 现状证据。
2. 后续先按消费者拆分 AuthController.bearer 等跨层调用和嵌套数据类型引用；每个 change 最多两个模块。
3. 根据完整依赖清单，为 auth、equipment、media、ai-workflow、shooting、evaluation、generation、course、portfolio 分别建立迁移 change；bootstrap 配置与清理实现另行治理。依赖清单决定具体顺序，不假定列表顺序即编译顺序。
4. 各批迁移同步 package、imports、测试包、扫描配置和相关文档，并新增对应零违规检查。
5. 每批执行 mvn spotless:apply、mvn spotless:check 和 QUALITY_BASE_REF=<实施前基线> mvn verify；不改变质量阈值。最终全模块扫描零违规并通过完整 reactor 验证，才完成总体目标。

本批无部署或数据迁移。回滚仅撤销本批测试工具、测试依赖和现状测试；后续包迁移回滚须同时恢复路径、package、引用和测试，重新运行完整门禁。
