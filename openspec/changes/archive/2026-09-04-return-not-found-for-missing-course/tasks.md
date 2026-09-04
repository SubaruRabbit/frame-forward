## 1. 课程详情缺失资源响应

- [x] 1.1 `frame-forward-server/bootstrap`：先为认证用户查询不存在课程时返回 `404` 编写集成回归测试，并确认该测试在实现前失败。
- [x] 1.2 `frame-forward-server/course`：仅在课程详情控制器中将课程不存在的业务异常映射为 `404 Not Found`，并验证任务 1.1 的集成测试通过。

## 2. 服务端质量验证

- [x] 2.1 `frame-forward-server`：运行 `QUALITY_BASE_REF=HEAD mvn verify`，验证课程详情的正常与缺失资源路径均通过完整质量门禁。
