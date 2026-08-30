# FrameForward OpenSpec Change Plan

本计划以批准版PRD为需求基线。每个change只负责一个主能力；实现时一次只apply一个change，完成验证并archive后再进入依赖它的change。

## 执行顺序

| 阶段 | Change | 单一职责 | 任务数 | 直接依赖 |
|---:|---|---|---:|---|
| 0 | `bootstrap-workspace` | 初始化App、服务端和契约工程 | 4 | 无 |
| 1 | `build-app-shell` | Android导航、门禁和通用页面状态 | 4 | bootstrap |
| 2 | `add-password-authentication` | 密码账号与会话 | 7 | bootstrap, app-shell |
| 3A | `add-equipment-catalog` | 只读器材事实目录 | 4 | auth |
| 3B | `ingest-jpeg-media` | JPEG、EXIF与私有存储 | 5 | bootstrap, auth |
| 3C | `run-ai-tasks` | AI异步任务运行时 | 7 | bootstrap, auth |
| 4A | `manage-user-equipment` | 用户器材与主力相机 | 6 | catalog, auth |
| 4B | `deliver-ai-courses` | AI课程、版本与进度 | 7 | AI runtime, catalog, auth |
| 5 | `analyze-shooting-scene` | 现场环境结构化分析 | 5 | media, AI runtime, user equipment |
| 6A | `generate-shooting-plans` | 器材约束拍摄方案 | 5 | scene analysis, user equipment |
| 6B | `evaluate-photos` | 单张照片评分与诊断 | 6 | media, AI runtime, user equipment |
| 7A | `generate-reference-images` | 已选方案的参考图 | 5 | shooting plans, AI runtime, media |
| 7B | `compare-retakes` | 原作与重拍进步对比 | 5 | evaluation, shooting plans |
| 8 | `browse-portfolio` | 私有作品集浏览与筛选 | 5 | evaluation, retake comparison, auth |
| 9 | `delete-work-data` | 单作品及派生数据删除 | 5 | portfolio, media, evaluation |
| 10 | `delete-account-data` | 账号注销与全量清理编排 | 7 | auth, work deletion, courses |

同一阶段带不同字母的change，在其直接依赖完成后可以独立推进；这不表示必须并行执行。

## 粒度守则

- proposal只声明一个new capability；纯工程初始化显式 `skip_specs`。
- 每个change最多5条requirements，且每条至少一个scenario。
- 每个change保持3～7个tasks，每个task只指向一个项目或服务端模块并包含验证方式。
- apply过程中若任务超过7项、需要第二个主能力或跨越两个以上业务模块，立即停止并拆出新change。
- 不在当前changes中加入部署、商业化、短信验证、密码恢复、相机直连或课程CMS。
