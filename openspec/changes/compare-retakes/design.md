## Context
两张作品已各自拥有版本化评价，比较只处理同一拍摄任务内的前后关系。
## Goals / Non-Goals
**Goals:** 结构化变化和基于问题的改善判断。
**Non-Goals:** 改写单张评分和全局成长分析。
## Decisions
- `evaluation` 模块保存有向retake关系并禁止跨用户关系。
- 比较器以两份评价、EXIF和plan目标为输入；确定性计算分数与参数差，AI只总结视觉问题变化。
- “已改善”必须引用原primary problem和新证据，不能由总分差直接推出。
## Risks / Trade-offs
- [不同拍摄目标不可比] → 只允许同一shooting session，目标变化提示新建任务。
