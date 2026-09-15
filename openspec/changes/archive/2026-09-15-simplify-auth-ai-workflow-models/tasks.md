## 1. 安全与兼容测试

- [x] 1.1 在 frame-forward-server/auth 先增加敏感字段字符串输出、Entity 构造和删除任务转换测试，并验证待迁移实现尚未满足测试
- [x] 1.2 在 frame-forward-server/ai-workflow 先增加可变请求 builder、任务 Entity 和构造器兼容测试，并验证待迁移实现尚未满足测试

## 2. 模型与转换实现

- [x] 2.1 在 frame-forward-server/auth 应用安全的 Lombok Entity 注解并实现 AuthConverter，验证认证与账户删除测试通过
- [x] 2.2 在 frame-forward-server/ai-workflow 应用 DTO/Entity Lombok 注解并提供 AiTaskCreateRequest builder，验证任务状态、幂等和序列化测试通过
- [x] 2.3 在 frame-forward-server/auth 与 ai-workflow 将纯注入构造器替换为 `@RequiredArgsConstructor`，保留 AiTaskBusiness 配置构造器并验证 Spring 装配通过

## 3. 批次验证

- [x] 3.1 在 frame-forward-server 执行 Spotless、auth/ai-workflow 及消费者编译测试，验证敏感数据和跨模块兼容性门禁通过
