## Context
评分需要JPEG、可选EXIF、器材上下文与版本化规则。
## Goals / Non-Goals
**Goals:** 稳定评分、技术诊断、重拍动作和无EXIF降级。
**Non-Goals:** 重拍对比与后期处理。
## Decisions
- `evaluation` 模块先解析确定性质量信号，再由 `qwen3.8-max` 输出分项证据；总分由版本化权重规则计算。
- `(user, contentHash, ruleVersion)` 建唯一复用键；显式重新分析创建新rule execution版本。
- 结果schema区分观察、推测和缺失字段；App按固定顺序渲染并标记艺术评分非绝对标准。
## Risks / Trade-offs
- [模型审美波动] → 固定schema、低随机性、缓存同版本结果并建立基准样本评测。
