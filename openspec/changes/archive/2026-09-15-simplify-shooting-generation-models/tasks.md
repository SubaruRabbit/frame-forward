## 1. 工作流回归测试

- [x] 1.1 在 frame-forward-server/shooting 先增加请求 builder、Entity 和 AI 任务输入精确测试，并验证待迁移实现尚未满足测试
- [x] 1.2 在 frame-forward-server/generation 先增加请求 builder、Entity 和完成回调持久化测试，并验证待迁移实现尚未满足测试

## 2. 模型与调用实现

- [x] 2.1 在 frame-forward-server/shooting 分类应用 Lombok、改用 AiTaskCreateRequest builder 并保留业务输入组装，验证 shooting 测试通过
- [x] 2.2 在 frame-forward-server/generation 分类应用 Lombok、改用 builder 或有收益的纯 converter，验证 generation 测试通过
- [x] 2.3 在 frame-forward-server/shooting 与 generation 将纯注入构造器改为 `@RequiredArgsConstructor`，验证回调和 Spring 装配通过

## 3. 批次验证

- [x] 3.1 在 frame-forward-server 执行 Spotless、shooting/generation/ai-workflow 相关测试，验证 operationType、input map 与 API 无回归
