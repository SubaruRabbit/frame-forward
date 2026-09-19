## 1. 数据库迁移

- [x] 1.1 在 `frame-forward-server/bootstrap` 新增 Flyway 迁移，为 `lesson_progress` 填充代理主键、替换复合主键为唯一约束，并以迁移集成测试验证存量记录不丢失。

## 2. 课程持久化映射

- [x] 2.1 在 `frame-forward-server/course` 为 `LessonProgressEntity` 映射单列主键，并通过课程进度持久化测试验证原有提交与查询语义不变。

## 3. 回归与质量验证

- [x] 3.1 在 `frame-forward-server/bootstrap` 扩展数据库文档或迁移测试，验证所有业务表具有主键、课程进度组合唯一性和升级后主键唯一性。
- [x] 3.2 在 `frame-forward-server` 执行 `QUALITY_BASE_REF=origin/main mvn verify`，并启动 `dev` Profile 验证不再出现 `LessonProgressEntity` 的 MyBatis-Plus 主键告警。
