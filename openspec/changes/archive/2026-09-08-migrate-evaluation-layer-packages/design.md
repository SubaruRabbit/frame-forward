## 目标与范围

evaluation 全部生产 Java 进入职责子包；portfolio 仅同步作品查询、清理接口及其 DTO 的导入与类型名，不改行为。

## 设计

- Controller/异常处理器归 controller；三个用例 Service、作品只读查询和作品清理实现归 service；Business/Manager 归对应目录；Mapper/Entity 归 mapper/model.entity。
- WorkEvaluationCleanup 归 gateway；三个 Request 提取为独立 model.dto 类型，六个 Invalid/NotFound 按用例命名归 business，避免 Business 反向引用 Service。
- 作品查询的 Detail/WorkflowContext 提取为 PortfolioEvaluationDetail/PortfolioWorkflowContext；空投影构造留在查询服务，DTO 纯数据。
- EvaluationRepository 承担评测、会话查询、重拍关联与作品清理的 SQL。EvaluationManager 保留缓存绕过、关联去重与清理协调。PortfolioEvaluationQuery 只依赖 Manager 和 JSON 组件，不依赖 Mapper。
- ShootingSessionRepository 隔离方案所有权查询与会话插入，ShootingSessionManager 委托数据访问；ShootingSessionService 保留请求校验、鉴权、会话创建和原事务边界。
- 清理实现保留 Primary 与原组件注册语义，经 Manager 先清理关联再清理评测，不扩大删除条件。

## 验收

目录零违规，Service/Business/Manager 无 Mapper/MyBatis，Business 无反向 Service 引用；无旧根包兼容入口。原请求、缓存、重拍、作品上下文测试通过；补充会话创建/边界、作品查询空值/异常、清理顺序与 SQL 所有权条件、控制器映射回归。执行 Spotless、全 reactor clean verify 和严格规格校验。

## 风险与回滚

敏感风险是作品清理范围、缓存键、会话所有权和事务回归；保留原 SQL/版本条件/执行顺序，通过单元测试及 MySQL 集成验证。逆序恢复本批路径、类型、构造器和引用后重跑门禁，不修改数据库结构和真实用户数据，保留用户已有修改。
