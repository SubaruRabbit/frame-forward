## Why

完成已批准 Java 分层迁移中的单元测试镜像约定。

## What Changes

generation 的 ReferenceImageServiceTest→service；evaluation 的 PhotoEvaluationServiceTest、PortfolioEvaluationQueryTest、RetakeComparisonServiceTest→service，仅移动 package/目录并清理同包 import，保留全部断言。

## Capabilities

纯目录重构，无业务变化。

## Impact

仅 generation、evaluation。

## Dependencies

依赖 mirror-course-shooting-unit-tests，沿用整体迁移范围。

## Non-goals

不改生产代码、测试逻辑、质量阈值、接口或数据库，不提交发布。
