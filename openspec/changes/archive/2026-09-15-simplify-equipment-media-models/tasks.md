## 1. 兼容性测试

- [x] 1.1 在 frame-forward-server/equipment 先增加模型构造、MyBatis 访问器和 Entity/DTO 逐字段转换测试，并验证待迁移接口尚未满足测试
- [x] 1.2 在 frame-forward-server/media 先增加 MediaEntity 构造、Jackson 响应和 MediaConverter 逐字段测试，并验证待迁移接口尚未满足测试

## 2. 模型与转换实现

- [x] 2.1 在 frame-forward-server/equipment 分类应用 Lombok、实现 EquipmentConverter 并替换静态 `from` 调用，验证 equipment 测试通过
- [x] 2.2 在 frame-forward-server/media 分类应用 Lombok、实现 MediaConverter 并替换纯响应复制，验证 media 测试通过
- [x] 2.3 在 frame-forward-server/equipment 与 media 将纯注入构造器改为 `@RequiredArgsConstructor`，并验证两模块直接构造测试和 Spring 装配通过

## 3. 批次验证

- [x] 3.1 在 frame-forward-server 执行 Spotless、equipment/media 及其依赖测试，验证 API、MyBatis 和 Jackson 行为无回归
