## Context

course 包含三个 MyBatis Entity、课程响应 records、模型创建和手工注入构造器；bootstrap 负责最终应用装配。涉及 `frame-forward-server` 的 course、bootstrap 模块。

## Goals / Non-Goals

**Goals:**

- 完成课程模型、纯转换和两模块纯注入构造器简化。
- 通过 bootstrap 装配测试验证所有生成 Bean。
- 在最终批次执行全 Reactor 质量门禁。

**Non-Goals:**

- 不改变课程内容版本、反馈生成、数据库清理装配或部署配置。
- 不替换具有明确装配语义的 `@Bean` 方法。

## Decisions

1. 课程 Entity 使用安全 Entity 注解组合，响应 record 保持不变。
2. `CourseConverter` 只处理 `CourseContentVersionEntity` 等到响应 DTO 的纯字段转换；课程目录和进度业务组装继续由 Business 完成。
3. course 与 bootstrap 中仅赋值 final 依赖的 Bean 使用 `@RequiredArgsConstructor`；显式 `@Bean` 装配保持可见。
4. bootstrap 测试必须验证 MapStruct Spring Bean 可发现、MyBatis 扫描仍只注册持久化 mapper。
5. 以完整 `QUALITY_BASE_REF=HEAD mvn verify` 作为整个迁移的最终门禁。

## Risks / Trade-offs

- [MapStruct `@Mapper` 与 MyBatis 扫描混淆] → converter 位于独立包；bootstrap 继续限定 MyBatis 注解类型。
- [课程响应字段变化] → Controller 与内容版本测试逐字段验证。
- [最终 Reactor 暴露跨模块遗漏] → 将修复限制在对应未完成 change，不降低门禁或扩大忽略范围。

## Migration Plan

1. 先补课程转换与 bootstrap 装配失败测试。
2. 完成 course 改造，再完成 bootstrap 构造器与装配验证。
3. 执行全量格式、测试、覆盖率、重复率与复杂度门禁。
4. 若最终门禁失败，定位到所属批次回滚；基础依赖最后回滚。
