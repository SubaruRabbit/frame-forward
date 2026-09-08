## 目标与范围

generation 所有生产 Java 文件进入对应职责目录，无根包兼容入口；其他模块无需修改。

## 设计

- Controller/ExceptionHandler 归 controller，ReferenceImageService 和事务完成处理器 ReferenceImageCompletionProcessor 归 service，Business 归 business，Manager 归 manager。
- Entity/Mapper 归 model.entity/mapper；Request 改为独立 ReferenceImageRequest，NewReference 归 model.dto，异常独立放 business，避免 Business 引用 Service。
- ReferenceImageRepository 隔离原有 ShootingPlanMapper 和 ReferenceImageMapper 查询及写入。Manager 保留持久化去重、创建数据协调，提供按任务查找和保存结果的方法，不接触 MyBatis 或 MediaService。
- 将原 Manager.complete 的空记录/已生成判断、媒体注册、结果映射编排移入既有完成处理器，保留 @Transactional、supports、字段及调用顺序，通过 Manager 持久化。

## 验收

目录零违规、Manager 不依赖 Mapper/Service、Business 不反向引用 Service；原有请求校验和持久化测试通过。补充完成处理器支持判断、事务声明、重复/缺失/成功/失败分支与 HTTP 202/400/404 映射测试，验证查询过滤条件。Spotless、全 reactor clean verify、严格 OpenSpec 验证全部通过。

## 风险与回滚

风险为包可见性、Spring 扫描、完成回调事务及 DTO JSON 字段回归；以编译、分层回归、MockMvc 和真实 MySQL 集成验证。逆序恢复本批路径、引用和职责实现后重跑门禁，不修改数据或用户已有配置。
