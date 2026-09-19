## 1. 契约与数据建模

- [x] 1.1 在 `contracts` 更新课程详情的章节、课时和结构化教学内容响应契约，并以契约测试验证列表摘要与详情全文的边界。
- [x] 1.2 在 `frame-forward-server/bootstrap` 新增课程定义、章节、课时和版本内容的 Flyway 迁移，并以迁移集成测试验证表结构、外键和版本关联。

## 2. P0 课程种子内容

- [x] 2.1 在 `frame-forward-server/bootstrap` 种入摄影基础、微单操作和器材知识的完整章节、课时、示例、判断题与实拍任务，并以查询测试验证每类课程均可读取。
- [x] 2.2 在 `frame-forward-server/bootstrap` 种入八个首批 Sony/Nikon 机型教程及其受限菜单/固件提示，并以查询测试验证八个课程均具备可追溯版本元数据。

## 3. 服务端课程交付

- [x] 3.1 在 `frame-forward-server/course` 实现数据库课程目录和详情读取、DTO 转换及版本保留，并以单元和集成测试验证完整内容、404 与既有进度语义。
- [x] 3.2 在 `frame-forward-server` 更新课程 API 实现与 OpenAPI 契约校验，并运行 `mvn spotless:apply`、`mvn spotless:check` 和带 `QUALITY_BASE_REF` 的 `mvn verify`。

## 4. 客户端消费与端到端验证

- [x] 4.1 在 `frame-forward-app` 更新课程详情 TypeScript 类型和渲染，以组件测试验证章节、示例、练习及作业任务显示。
- [x] 4.2 在仓库根目录执行课程端到端路径验证：打开基础课程和八个机型教程、提交作业并检查进度；记录全部质量门禁结果。
