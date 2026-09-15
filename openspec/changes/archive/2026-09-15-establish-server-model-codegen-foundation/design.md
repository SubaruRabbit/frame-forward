## Context

服务端父 POM 当前没有 Lombok、MapStruct 或显式注解处理器配置。后续五个模块迁移 change 都需要同一套可复现的编译基线；涉及项目 `frame-forward-server`，本 change 仅修改父 Reactor 配置和必要的编译验证。

## Goals / Non-Goals

**Goals:**

- 固定 Lombok、MapStruct 与 `lombok-mapstruct-binding` 的兼容版本。
- 让 Maven 主源码和测试源码都能稳定执行注解处理。
- 让 MapStruct 默认生成 Spring Bean，并对未映射目标字段编译失败。

**Non-Goals:**

- 不在本 change 迁移任何业务模型或 Bean。
- 不改变 Spring Boot、MyBatis Plus 等既有依赖版本。

## Decisions

1. 在父 POM properties 与 dependency management 固定版本，在 dependencies 提供 Lombok、MapStruct API，使所有子模块采用同一基线。相比逐模块重复版本，这能减少漂移。
2. 在 `maven-compiler-plugin` 的 annotation processor paths 中按 Lombok、binding、MapStruct processor 配置处理器。相比依赖自动发现，该方式可复现并明确处理顺序。
3. 使用编译参数设置 `mapstruct.defaultComponentModel=spring` 和 `mapstruct.unmappedTargetPolicy=ERROR`。相比每个 converter 重复配置，父级策略能防止遗漏。
4. 新增最小测试夹具同时引用 Lombok builder 与 MapStruct 生成实现，证明两个处理器协作；不把生成源码纳入版本控制。

## Risks / Trade-offs

- [处理器版本不兼容] → 选用稳定版本组合并通过 Maven clean test 验证生成结果。
- [父级处理器影响所有模块编译] → 本 change 先独立验证，业务模块只在后续 changes 使用注解。
- [严格未映射策略增加迁移成本] → 要求每个差异显式映射或 ignore，不降低全局策略。

## Migration Plan

1. 先补充会因缺少注解处理而失败的编译测试。
2. 配置依赖和编译器并使测试通过。
3. 执行父 Reactor 格式、测试和质量检查。
4. 若失败，恢复父 POM 与测试夹具；后续 changes 不得开始。
