## Context
两张作品已各自拥有版本化评价，比较只处理同一拍摄任务内的前后关系。
## Goals / Non-Goals
**Goals:** 结构化变化和基于问题的改善判断。
**Non-Goals:** 改写单张评分和全局成长分析。
## Decisions
- `evaluation` 模块维护最小 `shooting_session`：session 仅归属一个用户和一个已选拍摄方案；不承担通用任务管理能力。
- 评价可显式关联一个用户拥有的 session，并保留该 session 的计划上下文；无 session 的单张评价行为保持不变。
- `evaluation` 模块保存有向retake关系，并同时校验两个评价、其媒体和 session 均归当前用户，且 session 相同。
- 比较器以两份评价、EXIF和plan目标为输入；确定性计算分数与参数差，AI只总结视觉问题变化。
- “已改善”必须引用原primary problem和新证据，不能由总分差直接推出。
## Risks / Trade-offs
- [不同拍摄目标不可比] → session 固定绑定一个已选方案；只允许同一 session，目标变化提示新建任务。
