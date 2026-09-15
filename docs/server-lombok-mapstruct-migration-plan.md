# FrameForward Server Lombok 与 MapStruct 整体改造计划

## 1. 目标

在不改变 OpenAPI 契约、JSON 序列化结果、MyBatis 持久化行为和业务规则的前提下：

- 使用 Lombok 消除 DTO、Entity 与 Spring Bean 中的样板代码。
- 使用 MapStruct 收敛 Entity、DTO 之间的纯字段转换。
- 保留现有 `record` DTO 的不可变性，不为统一注解而退化为可变 JavaBean。
- 保留包含配置转换、参数校验或派生逻辑的显式构造器。
- 将改造拆为可独立验证、可独立回滚的 OpenSpec changes。

## 2. 当前基线

| 项目 | 数量或现状 | 改造判断 |
| --- | ---: | --- |
| 服务端 Maven 模块 | 11 | 分批迁移，每个 change 最多涉及两个模块 |
| Spring Bean | 约 76 个 | 纯构造器注入 Bean 优先使用 `@RequiredArgsConstructor` |
| 显式生产代码构造器 | 约 74 个 | 排除异常构造器、模型语义构造器和配置转换构造器后迁移 |
| `record` DTO | 42 个 | 默认保持 record，仅在确有创建便利时使用 `@Builder` |
| 可变 class DTO | 8 个 | 按 Jackson 兼容要求使用 Lombok 精简 |
| MyBatis Entity | 20 个 | 使用 Getter/Setter/Builder/构造器注解，不默认使用 `@Data` |
| Lombok | 未引入 | 在父 POM 统一版本和编译配置 |
| MapStruct | 未引入 | 在父 POM 统一版本、处理器和严格映射策略 |

## 3. 统一设计规则

### 3.1 Lombok 注解矩阵

| 类型 | 默认注解 | 约束 |
| --- | --- | --- |
| 现有 record DTO | 保持 record；可选 `@Builder` | 不改成 `@Data` class，不改变 JSON 字段与构造参数顺序 |
| 可变请求 DTO | `@Data`、`@Builder`、`@NoArgsConstructor`、`@AllArgsConstructor` | 保持 Jackson 可反序列化；第一阶段不强制修改字段可见性 |
| MyBatis Entity | `@Getter`、`@Setter`、`@Builder`、`@NoArgsConstructor`、`@AllArgsConstructor` | 不默认使用 `@Data`，避免可变实体相等性和敏感字段输出风险 |
| Spring Bean | `@RequiredArgsConstructor` | 仅替代只给 `final` 依赖赋值的构造器 |
| 异常和值对象 | 保留语义构造器 | 不为了注解统一而删除校验或异常上下文 |

### 3.2 MapStruct 规则

- 转换接口统一放在各业务模块的 `converter` 包，避免与 MyBatis `mapper` 混淆。
- 默认使用 Spring component model，并启用未映射目标字段编译失败。
- Lombok 与 MapStruct 同时工作时配置 `lombok-mapstruct-binding`。
- MapStruct 只承担确定性的字段复制、重命名、集合转换和简单常量映射。
- UUID、当前时间、权限判断、状态迁移、数据库查询、JSON 容错和业务聚合不得下沉到 converter。
- 对 intentionally unmapped 字段必须逐项显式 `ignore`，禁止全局放宽门禁。

### 3.3 兼容性红线

- 不修改 OpenAPI 路径、请求/响应字段、状态码和认证要求。
- 不改变 Jackson 的字段名、null 行为和反序列化能力。
- 不改变 MyBatis 表名、列映射、主键和无参实例化能力。
- 不把密码哈希、访问令牌、删除令牌等敏感字段加入自动 `toString()`。
- 不以 Lombok 或 MapStruct 为由改变现有分层和业务逻辑。

## 4. Change 拆分与实施顺序

| 顺序 | Change 名称 | 涉及范围 | 核心交付 | 前置依赖 | 主要验证 | 回滚边界 |
| ---: | --- | --- | --- | --- | --- | --- |
| 1 | `establish-server-model-codegen-foundation` | 服务端父 POM、编译配置 | 引入 Lombok、MapStruct、处理器绑定与严格映射参数；建立编译冒烟测试 | 无 | Reactor 编译、注解处理器生效、Spotless/PMD 兼容 | 恢复父 POM 与冒烟测试 |
| 2 | `simplify-equipment-media-models` | equipment、media | 试点 Entity/DTO Lombok 化；新增 `EquipmentConverter`、`MediaConverter`；替换纯字段转换 | Change 1 | MyBatis 实体、Jackson、列表映射、媒体响应/API 回归 | 恢复两模块手写构造器与转换方法 |
| 3 | `simplify-auth-ai-workflow-models` | auth、ai-workflow | 敏感 Entity 安全注解；Bean 构造器精简；新增 `AuthConverter`；治理 AI 请求 builder 使用 | Change 1 | 认证/删除任务序列化、敏感信息、任务状态与幂等测试 | 恢复两模块模型和显式构造器 |
| 4 | `simplify-shooting-generation-models` | shooting、generation | 精简请求/实体和 Bean；转换纯创建数据；保留工作流输入组装逻辑 | Changes 1、3 | 场景分析、拍摄计划、参考图工作流与回调测试 | 恢复两模块模型与转换调用 |
| 5 | `simplify-evaluation-portfolio-models` | evaluation、portfolio | 精简 Entity/请求/Bean；仅转换确定性字段；保留 JSON 解析、筛选、分页和聚合 | Changes 1、2、4 | 评价、翻拍关联、作品分页/详情/删除测试 | 恢复两模块模型与转换调用 |
| 6 | `simplify-course-bootstrap-models` | course、bootstrap | 精简课程模型和 Bean；新增课程纯转换；复核启动装配与最终全量一致性 | Changes 1、2、3 | 课程接口、内容版本、Spring Context、全 Reactor 质量门禁 | 恢复两模块改造；最后回滚 Change 1 |

## 5. 各 Change 验收门槛

| 阶段 | 必须满足的验收结果 |
| --- | --- |
| Red | 先补充或调整能够证明序列化、持久化、转换和装配兼容性的失败测试 |
| Green | 以最小 Lombok/MapStruct 改动使新增测试和受影响模块测试通过 |
| Refactor | 删除已被安全替代的手写 getter、setter、构造器和纯转换代码 |
| 格式 | 执行 `mvn spotless:apply` 后，`mvn spotless:check` 通过 |
| 模块验证 | 受影响模块及其依赖模块测试通过 |
| 全量验证 | `QUALITY_BASE_REF=<已确认基线> mvn verify` 通过 |
| 契约 | OpenAPI 与 Controller 契约测试无差异 |
| 安全 | 敏感字段不出现在生成的 `toString()`，认证和删除流程无行为变化 |

## 6. 重点风险与控制措施

| 风险 | 触发场景 | 控制措施 |
| --- | --- | --- |
| MapStruct 看不到 Lombok 生成的访问器 | 两个注解处理器执行顺序不兼容 | 配置 `lombok-mapstruct-binding`，加入编译冒烟测试 |
| Entity `equals/hashCode` 行为改变 | 对 Entity 使用 `@Data` | Entity 默认只用 Getter/Setter，不生成相等性方法 |
| 敏感信息进入日志 | Entity 自动生成 `toString()` | 禁止敏感 Entity 使用默认 `@Data`；必要时显式排除字段 |
| Jackson 反序列化失败 | 删除无参构造器或改变属性访问方式 | 可变请求 DTO 保留无参构造器，并执行请求反序列化测试 |
| MyBatis 映射失败 | 缺少无参构造器/getter/setter | Entity 使用 Lombok 生成对应成员，并执行持久化测试 |
| API 输出发生变化 | record 改 class、字段改名或 null 策略变化 | record 默认不改；执行 Controller/OpenAPI 回归测试 |
| converter 承载业务规则 | 为减少代码把 UUID、时间、查询或状态流塞入映射器 | converter 限定为纯转换，业务值由调用方提供 |
| `@RequiredArgsConstructor` 无法替代配置构造器 | 构造器包含 `@Value` 或字符串到类型的转换 | `MediaService`、`AiTaskBusiness` 保留构造器或另建配置对象 change |
| 跨模块共享 DTO 同时破坏多个消费者 | 字段私有化或构造方式一次性变化 | 第一阶段保持兼容访问方式，按依赖顺序迁移消费者 |

## 7. 完成定义

整体改造仅在以下条件全部满足后完成：

1. 六个 OpenSpec changes 均已评审、按依赖顺序实施并完成验证。
2. 所有可安全替代的纯注入构造器已改为 `@RequiredArgsConstructor`。
3. 可变 DTO 和 Entity 的样板访问器/构造器已由分类后的 Lombok 注解替代。
4. 现有 record DTO 继续保持不可变与契约兼容。
5. 所有识别出的纯对象复制由模块 converter 负责，业务拼装仍留在原业务层。
6. 全量 Formatter、测试、静态分析、覆盖率、重复率和复杂度门禁通过。
7. 每个 change 都具备独立回滚说明，且没有降低任何质量门禁。
