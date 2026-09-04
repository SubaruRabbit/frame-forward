## Why

课程详情契约已声明不存在的课程返回 `404`，但服务端未映射该业务异常，导致客户端无法按契约处理该场景。现在需要将已发布的 OpenAPI 行为落实到服务端。

## What Changes

- 为不存在的课程详情请求返回明确的 `404 Not Found`。
- 增加该响应的服务端契约回归测试。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `learning/ai-courses`: 明确课程详情请求在课程不存在时的响应语义。

## Impact

- 影响课程模块的异常映射与测试，以及 `GET /courses/{courseId}` 的实际 HTTP 响应。

## Dependencies

- 已批准的 `learning/ai-courses` 主规格与 `contracts/openapi.yaml`。

## Non-goals

- 不修改课程进度、作业提交或其他接口的缺失资源处理。
- 不修改 OpenAPI 中已声明的课程详情响应。
