## Why

根目录 `reference-image` 与 `scene-analysis` 仍依赖限期例外，需迁入 RN 规范目录并建立受控公开入口。

## What Changes

- 完整迁移两个模块至 `src/features/**`，保持行为和测试不变。
- 更新 app 消费者并从例外清单删除对应文件。
- 执行质量、平台解析与 Metro 验证。

## Capabilities

纯重构，已设置 `skip_specs: true`。

## Dependencies

- `migrate-photo-review-portfolio-features-to-src`。

## Non-goals

- 不修改参考图、场景分析业务、API、状态或 UI。

## Impact

影响客户端 `reference-image`、`scene-analysis`、app 装配和迁移清单；服务端契约不变。
