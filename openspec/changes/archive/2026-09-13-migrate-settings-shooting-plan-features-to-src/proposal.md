## Why

根目录 `settings` 与 `shooting-plan` 仍依赖限期例外，需迁入 RN 规范目录并建立受控公开入口。

## What Changes

- 完整迁移两个模块至 `src/features/**`，保持行为和测试不变。
- 更新 app 消费者并从例外清单删除对应文件。
- 执行质量、平台解析与 Metro 验证。

## Capabilities

纯重构，已设置 `skip_specs: true`。

## Dependencies

- `migrate-reference-image-scene-analysis-features-to-src`。

## Non-goals

- 不修改设置、拍摄方案业务、API、状态或 UI。

## Impact

影响客户端 `settings`、`shooting-plan`、app 装配和迁移清单；服务端契约不变。
