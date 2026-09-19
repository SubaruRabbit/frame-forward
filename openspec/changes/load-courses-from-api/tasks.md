## 1. 课程目录契约

- [x] 1.1 在 `contracts` 为课程目录摘要新增 `lessonCount`，并以 OpenAPI 契约测试验证字段为非负整数。
- [x] 1.2 在 `frame-forward-server/course` 计算当前缓存内容版本的课时总数并写入目录响应，以服务层或控制器测试验证各课程摘要返回正确数量。

## 2. App 课程接入

- [x] 2.1 在 `frame-forward-app/src/features/learning` 定义目录与详情契约，并实现使用既有认证客户端的网络端口，以端口测试验证 `/courses`、`/courses/{courseId}` 路径和响应映射。
- [x] 2.2 在 `frame-forward-app/src/features/learning` 将学习页改为使用网络课程端口，以组件测试验证目录标题、分类、课时数及详情章节与课时的显示。
- [x] 2.3 在 `frame-forward-app/src/features/learning` 实现加载、空态、失败重试和详情不可用后的返回目录，并以组件测试验证各状态转换。

## 3. 集成验证

- [x] 3.1 在 `frame-forward-app/src/app` 将学习 feature 接入既有应用网络依赖，并以装配测试验证不再使用本地缓存作为目录来源。
- [x] 3.2 在 `frame-forward-server` 运行 `QUALITY_BASE_REF=origin/main mvn verify`，并在 `frame-forward-app` 运行 `npm run quality`；启动后端和 Android 模拟器，登录后验证目录课时数与课程详情可见。
