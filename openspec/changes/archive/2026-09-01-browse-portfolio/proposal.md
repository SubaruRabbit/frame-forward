## Why
用户需要集中查看作品、评分、EXIF、拍摄方案和重拍关系。
## What Changes
- 提供作品列表、详情、收藏与题材/器材筛选。
- 展示评分、方案和重拍关系入口。
## Capabilities
### New Capabilities
- `portfolio/work-browser`: 私有作品集浏览与筛选。
### Modified Capabilities
无。
## Impact
影响App `portfolio`、服务端 `portfolio` 与作品集OpenAPI。
## Dependencies
`evaluate-photos`、`compare-retakes`、`add-password-authentication`。
## Non-goals
- 不做社区、分享、排行榜或高级成长分析。
- 不负责底层文件删除。
