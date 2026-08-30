## Context
作品、评分、方案和重拍关系由各自模块持有，portfolio负责私有读取视图。
## Goals / Non-Goals
**Goals:** 列表、详情、有限筛选和收藏。
**Non-Goals:** 社区、分享和删除文件。
## Decisions
- `portfolio` 模块保存作品索引与favorite，使用各模块公开查询投影组装详情，不直读其内部表。
- 游标分页与题材/相机/镜头筛选进入OpenAPI；缺失可选关联时返回显式availability状态。
- `portfolio` feature以列表和详情两层导航呈现，不实现复杂仪表盘。
## Risks / Trade-offs
- [跨模块详情查询变慢] → 首版本地使用接受组合查询，必要字段可后续建立读模型。
