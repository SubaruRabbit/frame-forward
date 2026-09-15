## Context

equipment 包含 4 个 MyBatis Entity 和多处 `Entity -> record DTO` 静态 `from` 转换；media 包含 `MediaEntity` 的手写构造器/getter 及响应转换。涉及项目 `frame-forward-server` 的 equipment、media 两个模块。

## Goals / Non-Goals

**Goals:**

- 按模型类别消除访问器和构造器样板代码。
- 用 Spring MapStruct converter 替代确定性字段复制。
- 将纯注入构造器改为 `@RequiredArgsConstructor`。

**Non-Goals:**

- 不改变字段可见性、器材兼容规则、媒体文件处理或持久化结构。
- 不将 UUID、路径处理、EXIF JSON 解析等逻辑放入 converter。

## Decisions

1. Entity 使用 `@Getter/@Setter/@Builder/@NoArgsConstructor/@AllArgsConstructor`，不使用会生成实体相等性和字符串输出的 `@Data`。
2. 现有 record DTO 保持 record；删除 DTO 内的 `from` 方法，由 `EquipmentConverter` 负责单对象和列表元素映射。
3. `MediaConverter` 负责 `MediaEntity -> MediaResponse` 的字段重命名与完成状态常量；文件路径规范化仍由 `MediaService` 完成。
4. converter 放在 `converter` 包并以 Spring Bean 注入，避免与 MyBatis `mapper` 包混淆。
5. `MediaService` 的配置值到 `Path` 转换构造器保留；其他仅赋值 final 依赖的 Bean 使用 Lombok 构造器。

## Risks / Trade-offs

- [Jackson 或 MyBatis 实例化变化] → 先写无参构造和序列化/映射回归测试。
- [MapStruct 常量或字段重命名错误] → converter 单元测试逐字段断言，未映射字段由编译阻断。
- [直接字段访问被误改] → 第一阶段保留字段可见性，仅移除已被注解替代的方法。

## Migration Plan

1. 先补充两模块模型、converter 和接口兼容性测试。
2. 迁移 Entity/DTO 后实现 converter，再替换调用方。
3. 最后迁移纯注入构造器并删除死代码。
4. 分别运行 equipment、media 及依赖测试；失败时按模块恢复手工转换。
