## 1. 聚合边界测试

- [x] 1.1 在 frame-forward-server/evaluation 先增加请求 builder、Entity 和候选纯转换逐字段测试，并验证待迁移实现尚未满足测试
- [x] 1.2 在 frame-forward-server/portfolio 先增加 Entity、分页空值、JSON 聚合和候选纯转换测试，并验证待迁移实现尚未满足测试

## 2. 模型与转换实现

- [x] 2.1 在 frame-forward-server/evaluation 分类应用 Lombok，仅实现确定性 converter 并保留状态迁移，验证 evaluation 测试通过
- [x] 2.2 在 frame-forward-server/portfolio 分类应用 Lombok，仅替换纯字段复制并保留查询、筛选和聚合，验证 portfolio 测试通过
- [x] 2.3 在 frame-forward-server/evaluation 与 portfolio 将纯注入构造器改为 `@RequiredArgsConstructor`，验证直接构造和 Spring 装配通过

## 3. 批次验证

- [x] 3.1 在 frame-forward-server 执行 Spotless、evaluation/portfolio 及 media/shooting 相关测试，验证分页、详情、删除与 API 无回归
