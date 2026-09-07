## Why

auth、photo-import 需要从页面中隔离认证、存储和设备调用。
## What Changes

- 将两 Feature 接入 Application/Port 边界。
## Capabilities
### New Capabilities

无；内部重构，`skip_specs: true`。
### Modified Capabilities

无。
## Impact

仅两个 APP Feature。
## Dependencies

C2、C3。
## Non-goals

不改变登录或 JPEG 导入语义。
