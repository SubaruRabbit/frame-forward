## 目标与范围

course 全部生产 Java 按职责分包，bootstrap 只同步课程服务的测试引用。继承前序迁移批准范围，不保留根包兼容类型。

## 设计

- Controller、Service、Business、Manager 各归对应目录；CourseRules 归 business，静态目录 CourseCatalog 归 component，CourseVersion 归 model.dto。
- 嵌套课程、课时、进度、反馈、内容版本、提交请求和业务上下文记录提为 model.dto 的公开数据类型。保留字段及不可变列表复制，LessonContext 只提供原有简单数据投影。
- CourseNotFound 放 business，避免 Business 反向依赖 Service 的嵌套异常。
- 三个 Mapper 分为公开接口放 mapper，实体放 model.entity；CourseRepository 独占 MyBatis 查询及插入。Manager 保留查找/创建及进度去重协调，委托 Repository，查询条件、写入字段与顺序不变。
- 对跨包实际使用的类型、构造器和方法提供必要 public 可见性，不引入额外能力。

## 验收

目录检查零违规，Service/Business/Manager 无 Mapper 字段；原有规则、层测试和 MySQL 课程集成测试通过。新增 Service/Controller 回归覆盖目录、鉴权、课程缺失映射、进度与提交编排，新增查询条件测试。执行 Spotless apply/check、全 reactor clean verify 及严格规格验证。

## 风险与回滚

主要风险是跨包可见性、DTO JSON 字段、Mapper 扫描及 SQL 条件回归，以编译、单元测试和 MySQL 集成验证。逆向恢复本批路径、类型和引用并重新执行门禁；不修改数据库或用户已有配置。
