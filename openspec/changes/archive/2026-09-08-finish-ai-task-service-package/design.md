## 目标与范围

仅 ai-workflow。用原 Runtime 的实际调度/SSE/恢复实现替换 service.AiTaskRuntime 中的过渡委托，恢复默认 bean 名 aiTaskRuntime，唯一 PostConstruct/Transactional 恢复方法不变，旧根包 Runtime 删除。所有内部类型改用独立 model.dto；Creation 提取为 AiTaskCreation，BadRequest/NotFound 提取为 business.AiTaskBadRequest/AiTaskNotFound。

AiTaskBusiness 迁 business，Controller/异常映射迁 controller。Business 移除 AuthService 字段；Service 先调用原请求校验再认证再创建。查询先通过 Business 获取任务，缺失直接 NotFound，再认证并由 Business 核对所有权和生成状态。保持原校验/认证次序，不改变错误优先级。状态机、模型路由、幂等冲突处理、回调和更新时间逻辑不变。

Entity 与完成回调仍在后续单独批次处理，本批不宣称 AI 模块全部合规。

## 测试与验收

先增加旧入口/委托清除失败测试。原 Runtime 单元测试迁至 service 镜像包，保留状态机、路由、幂等、重试、所有权、恢复、SSE 的全部业务断言；临时 Adapter 测试替换为实际入口契约测试，保留 JSON、空值、异常、恢复语义，新增提交后调度验证。验证 Business 无 AuthService/Runtime/Mapper 反向依赖，Controller 返回独立 DTO。

执行 Spotless apply/check、完整 clean verify（QUALITY_BASE_REF=353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef）、旧引用审计和严格 OpenSpec 校验。

## 风险与回滚

风险为事务提交时序、恢复重复、认证错误优先级、DTO 私有字段访问和 Spring 注入。用单元与完整 MySQL/Spring 测试验证。不降低门禁；回滚恢复旧实际实现和过渡委托后重跑门禁，不操作数据库或前序用户工作。
