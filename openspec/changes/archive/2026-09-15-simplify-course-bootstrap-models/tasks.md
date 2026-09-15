## 1. 最终批次测试

- [x] 1.1 在 frame-forward-server/course 先增加 Entity、CourseConverter 和课程响应逐字段测试，并验证待迁移实现尚未满足测试
- [x] 1.2 在 frame-forward-server/bootstrap 先增加 converter Bean 与 MyBatis mapper 扫描隔离测试，并验证最终装配约束可被检测

## 2. 模型与装配实现

- [x] 2.1 在 frame-forward-server/course 分类应用 Lombok、实现 CourseConverter 并替换纯字段复制，验证 course 测试通过
- [x] 2.2 在 frame-forward-server/course 与 bootstrap 将纯注入构造器改为 `@RequiredArgsConstructor`，保留显式 Bean 装配并验证启动测试通过

## 3. 全量验收

- [x] 3.1 在 frame-forward-server 执行 `mvn spotless:apply`、`mvn spotless:check` 和 `mvn test`，验证全 Reactor 功能与格式门禁通过
- [x] 3.2 在 frame-forward-server 执行 `QUALITY_BASE_REF=HEAD mvn verify`，验证覆盖率、重复率、复杂度、架构和报告门禁全部通过
