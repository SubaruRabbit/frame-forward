## Why

根目录 `learning` 与 `photo-import` 仍依赖限期例外，需迁入 RN 规范目录并建立受控公开入口。

## What Changes

- 完整迁移两个模块至 `src/features/**`，保持行为和测试不变。
- 更新 app 消费者并从例外清单删除对应文件。
- 执行质量、平台解析与 Metro 验证。

## Capabilities

纯重构，已设置 `skip_specs: true`。

## Dependencies

- `migrate-auth-equipment-features-to-src`。

## Non-goals

- 不修改课程缓存、照片选择上传、API、存储或 UI。

## Impact

影响客户端 `learning`、`photo-import`、app 装配和迁移清单；服务端契约不变。
