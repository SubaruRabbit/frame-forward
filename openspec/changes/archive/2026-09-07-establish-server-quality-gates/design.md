## Context

参见 SRV-01：`mvn spotless:check` 无插件，且没有覆盖率与静态分析配置。

## Goals / Non-Goals

**Goals:** 让格式、测试、覆盖率与静态分析可本地和 CI 一致执行。

**Non-Goals:** 不修改产品规则或放宽核心系统阈值。

## Decisions

- Spotless 固定引用仓库内的 Eclipse JDT XML 配置，作为唯一可复现的格式基线。
- JaCoCo 生成 XML 报告；仓库内脚本读取 `QUALITY_BASE_REF` 与 Git diff，仅汇总变更的可执行 Java 行及分支覆盖率。
- 质量报告解析器接受 JaCoCo XML 内置的 `report.dtd` 声明，但禁用外部 DTD 加载和外部 schema 访问；解析器只读取报告自身内容，不访问网络或本地外部资源。
- `QUALITY_BASE_REF` 必须由本地或 CI 显式提供；找不到基线或 JaCoCo XML 时质量检查失败。变更代码行覆盖率低于 90% 或分支覆盖率低于 80% 时以非零退出码阻断 `mvn verify`；没有变更的可执行行时输出明确结果并通过。
- 静态分析保留可审计报告，复杂度与重复率阈值失败时阻断构建。
- 为消除 `PasswordPolicy.validate` 的既有复杂度违规，先以现有密码规则补充回归测试，再按单一规则判断拆分私有方法；不得改变密码校验语义或异常行为。
- 为消除 `MediaService.ingest` 的既有复杂度违规，先以现有媒体接收行为补充回归测试，再提取校验、图像处理和持久化的私有步骤；不得改变媒体处理结果、异常或文件清理语义。
- 格式化与业务迁移分开提交。

## Risks / Trade-offs

- [存量代码阻断] → 以独立格式化提交治理，不扩大排除范围。
- [既有复杂度阻断] → 仅拆分 `PasswordPolicy.validate` 的私有规则判断，并用回归测试锁定原有行为。
- [媒体复杂度阻断] → 仅拆分 `MediaService.ingest` 的私有处理步骤，并用回归测试锁定原有行为。
- [JaCoCo XML 解析阻断] → 使用含标准 `report.dtd` 声明的最小报告测试解析器；保留安全处理并禁止外部实体加载。

## Migration Plan

先配置检查和报告，再在独立提交中修正存量格式；Eclipse JDT XML 随配置纳入版本控制。生成代码仅按 `generated-sources` 的明确目录边界排除。回滚仅移除新增门禁配置。
