# 服务端 Java 目录迁移清单

## 最终状态（2026-09-08）

全部模块的生产目录迁移及单元测试镜像归层已完成。以下历史进度、113 文件基线映射及临时桥接设计仅用于追溯，不再代表待办；最终验收证据见 `verification.md` 的最终审计节。

当前生产 Java 文件共 202 个：ai-workflow 24、auth 23、bootstrap 3、course 23、equipment 35、evaluation 32、generation 13、media 12、portfolio 17、shooting 20；common 无生产 Java 文件，仅提供测试检查工具。根包只保留 bootstrap 启动类。各模块目录检查全部通过；旧根包类型引用与按同名被测类检查的测试镜像遗漏均为零。

最终职责修订：AiTaskCompletionProcessor、AccountDataCleanup、WorkMediaCleanup、WorkEvaluationCleanup 为 gateway 隔离端口；AI 完成回调经 Manager 调用 gateway，使用 model.dto.AiTaskCompletionContext，不向消费者暴露任务 Entity。PortfolioEvaluationQuery 留在 service，持久化操作通过 Manager/Repository 隔离。所有临时根包兼容入口已清理。

最后三批 `mirror-course-shooting-unit-tests`、`mirror-generation-evaluation-unit-tests`、`mirror-portfolio-unit-tests` 保留原测试主体，只调整目录/package/import。架构、迁移回归和 bootstrap 集成测试保留根测试包。

最新进度（2026-09-08，evaluation 收尾）：evaluation 已完成全部生产包迁移及 Mapper 隔离，会话创建、作品查询/清理经 Manager/Repository；portfolio 消费者同步。完整 clean verify 通过，见 `../migrate-evaluation-layer-packages/verification.md`。剩余 shooting、ai-workflow 两个模块。shooting 的 ShootingPlanEntity/Mapper 同时被 generation、evaluation 引用，需按两模块上限分批迁移；若暂时并存同名新旧 Mapper，必须显式区分 Spring bean 名并在收尾删除兼容类型与临时命名，避免扫描冲突。

最新进度（2026-09-08，portfolio 收尾）：portfolio 原 10 个根包文件重组为 17 个分层文件，DTO 纯数据、筛选规范化留在服务层，Repository 隔离持久化；删除失败/重试顺序及全量 clean verify 通过，见 `../migrate-portfolio-layer-packages/verification.md`。剩余 ai-workflow、shooting、evaluation 三个模块；evaluation 的作品查询目前直接依赖 Mapper，后续需经 Manager/Repository 并提取查询 DTO，和 portfolio 消费者同步迁移。

最新进度（2026-09-08，generation 收尾）：generation 原 8 个根包生产文件重组为 13 个分层文件，Repository 隔离 Mapper，媒体完成编排上移原事务 Service；完整 clean verify 通过，见 `../migrate-generation-layer-packages/verification.md`。剩余 ai-workflow、shooting、evaluation、portfolio 四个模块。portfolio 已初步确认无跨模块 Java 消费者，可单模块推进；Filter 的规范化逻辑需留在业务/服务层，DTO 仅承载数据，删除流程保留现有失败状态和清理顺序。

最新进度（2026-09-08，course 收尾）：course 的 11 个根包生产文件已重组为 23 个分层文件，DTO/异常独立，Repository 隔离 MyBatis，bootstrap 引用同步。完整 clean verify 通过，见 `../migrate-course-layer-packages/verification.md`。剩余 ai-workflow、shooting、generation、evaluation、portfolio 五个模块；下批可处理 generation，需将 Manager 内媒体服务调用移至完成处理 Service，并由 Repository 承担查询，保持完成回调和事务行为不变。

最新进度（2026-09-08，实体收尾）：media 全部生产 Java 文件已进入规范分层目录，根包实体及临时查询入口已删除，shooting、generation、evaluation 消费者同步完成迁移。全量 clean verify 通过，变更行覆盖率 95.49%、分支覆盖率 96.00%、重复率 0.00%，见 `../finish-media-layer-packages/verification.md`。下述实体分批设计已全部执行。剩余 ai-workflow、course、shooting、generation、evaluation、portfolio 的自身目录分层尚需继续；course 已完成初步依赖核对，可按 course + bootstrap 两模块范围推进。

后续进度（2026-09-08，Service/端口收尾）：MediaService、MediaResponse、UploadProgress 已迁入 service/model.dto；generation、course 已切换，根包 Service 兼容入口已删除，见 `../finish-media-service-package/verification.md`。作品查询及清理端口已完成迁移并通过完整门禁，见 `../migrate-media-portfolio-ports/verification.md`。media 根包只剩 MediaEntity。

## MediaEntity 后续批次设计输入

实体被 shooting、generation、evaluation 同时用于生产代码和 Mockito 返回值，不能直接改返回类型导致尚未切换的消费者编译失败，也不能用不安全泛型强转规避两模块上限。后续可按以下顺序建立独立 change：

1. media 单模块建立 model.entity.MediaEntity；根包实体暂继承目标数据类并保留表名映射。原查询返回旧类型保持兼容，同时为调用方提供返回规范实体类型的明确查询方法（如 findOwnedEntity、findEntityById），委托同一 Repository，不复制查询规则。用现有 MyBatis/MySQL 测试验证继承字段映射。
2. shooting、generation 在同一批切换实体类型和规范查询方法，包含 mock 返回值。
3. evaluation、media 最后一批切换，media Mapper/Repository/Service/测试统一规范实体，删除旧查询入口和根包实体，建立全 media 零目录违规断言并 clean verify。

此处是后续规格输入，不是已完成声明；具体实施前仍须建立并验证对应 OpenSpec change。

后续进度（2026-09-08，Manager 收尾）：shooting、generation、evaluation 已全部切换至 media.manager.MediaManager，media 根包兼容 Manager 已删除，目标 Bean 扫描与完整 clean verify 通过。详见 `../finish-media-manager-package/verification.md`。media 剩余根包为 Service、Entity、作品集查询投影与作品清理端口，整体目标仍在进行中。

后续进度（2026-09-08）：media 已完成 controller、mapper、repository 和目标 manager 的基础分层，完整质量门禁通过，见 `../migrate-media-layer-foundation/verification.md`。Manager 兼容入口与 Service/Entity/DTO/投影/清理端口尚待后续迁移；七个剩余业务模块的整体目标仍未完成。

后续进度（2026-09-07）：auth 的目标分层实现、九个消费者切换及最终清理已完成，详见 `../finish-auth-layer-packages/verification.md`。equipment 已完成 35 个生产类型分层及 shooting 消费者切换，详见 `../migrate-equipment-layer-packages/verification.md`；bootstrap 无待迁移业务依赖，已独立提前完成 config/repository 整理，详见 `../migrate-bootstrap-layer-packages/verification.md`。auth、equipment 根包已无生产 Java 文件，bootstrap 根包只保留启动类；AccountDataCleanup 的实际目标调整为 gateway，以保持依赖方向。以下表格保留为迁移前基线清单；media、course、ai-workflow、shooting、generation、evaluation、portfolio 自身目录尚待迁移。

基线：353b4b93b33b6b8a7b2a86afbdb7e25ae0f89aef。此清单是后续批次输入，本批未移动生产类。

## 逐文件映射

源路径相对 frame-forward-server；目标是同一业务根目录下的子目录，文件名保持不变。消费者列按源码与测试中的类型名称引用保守检索（可能包含同名类型），包含同模块引用；每批实施前须核对语义引用。验证 V：对应模块单元与架构测试、所有消费者编译、bootstrap 集成测试和完整 Maven verify。

| 源文件 | 目标子目录 | 引用模块（含测试） | 验证 |
| --- | --- | --- | --- |
| ai-workflow/src/main/java/com/frameforward/ai/AiTaskBusiness.java | business | ai-workflow | V |
| ai-workflow/src/main/java/com/frameforward/ai/AiTaskCompletionProcessor.java | service | ai-workflow、generation、shooting | V |
| ai-workflow/src/main/java/com/frameforward/ai/AiTaskController.java | controller | 无显式引用；复核扫描装配 | V |
| ai-workflow/src/main/java/com/frameforward/ai/AiTaskEntity.java | model/entity | ai-workflow、generation、shooting | V |
| ai-workflow/src/main/java/com/frameforward/ai/AiTaskExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| ai-workflow/src/main/java/com/frameforward/ai/AiTaskManager.java | manager | ai-workflow | V |
| ai-workflow/src/main/java/com/frameforward/ai/AiTaskMapper.java | mapper | ai-workflow | V |
| ai-workflow/src/main/java/com/frameforward/ai/AiTaskRuntime.java | service | ai-workflow、bootstrap、course、equipment、evaluation、generation、portfolio、shooting | V |
| ai-workflow/src/main/java/com/frameforward/ai/CourseGenerationValidator.java | business | ai-workflow | V |
| ai-workflow/src/main/java/com/frameforward/ai/PhotoEvaluationGraph.java | business | ai-workflow | V |
| ai-workflow/src/main/java/com/frameforward/ai/PlanGenerationGraph.java | business | ai-workflow | V |
| ai-workflow/src/main/java/com/frameforward/ai/ReferenceImageGraph.java | business | ai-workflow | V |
| ai-workflow/src/main/java/com/frameforward/ai/RetakeComparisonGraph.java | business | ai-workflow、evaluation | V |
| ai-workflow/src/main/java/com/frameforward/ai/SceneAnalysisGraph.java | business | ai-workflow | V |
| auth/src/main/java/com/frameforward/auth/AccountDataCleanup.java | service | auth、bootstrap、media | V |
| auth/src/main/java/com/frameforward/auth/AccountDeletionJobEntity.java | model/entity | auth | V |
| auth/src/main/java/com/frameforward/auth/AccountDeletionJobMapper.java | mapper | auth | V |
| auth/src/main/java/com/frameforward/auth/AccountEntity.java | model/entity | auth | V |
| auth/src/main/java/com/frameforward/auth/AccountMapper.java | mapper | auth | V |
| auth/src/main/java/com/frameforward/auth/AuthBusiness.java | business | auth | V |
| auth/src/main/java/com/frameforward/auth/AuthController.java | controller | auth、course、equipment、evaluation、generation、media、portfolio、shooting | V |
| auth/src/main/java/com/frameforward/auth/AuthExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| auth/src/main/java/com/frameforward/auth/AuthManager.java | manager | auth | V |
| auth/src/main/java/com/frameforward/auth/AuthService.java | service | ai-workflow、auth、course、equipment、evaluation、generation、media、portfolio、shooting | V |
| auth/src/main/java/com/frameforward/auth/PasswordPolicy.java | business | auth | V |
| auth/src/main/java/com/frameforward/auth/ProtectedResourceController.java | controller | 无显式引用；复核扫描装配 | V |
| auth/src/main/java/com/frameforward/auth/RefreshSessionEntity.java | model/entity | auth | V |
| auth/src/main/java/com/frameforward/auth/RefreshSessionMapper.java | mapper | auth | V |
| bootstrap/src/main/java/com/frameforward/bootstrap/AccountDatabaseCleanup.java | repository | bootstrap | V |
| bootstrap/src/main/java/com/frameforward/bootstrap/AccountDeletionConfiguration.java | config | bootstrap | V |
| bootstrap/src/main/java/com/frameforward/bootstrap/FrameForwardApplication.java | . | 无显式引用；复核扫描装配 | V |
| course/src/main/java/com/frameforward/course/AssignmentFeedbackEntity.java | model/entity | course | V |
| course/src/main/java/com/frameforward/course/CourseBusiness.java | business | course | V |
| course/src/main/java/com/frameforward/course/CourseCatalog.java | component | course | V |
| course/src/main/java/com/frameforward/course/CourseContentVersionEntity.java | model/entity | course | V |
| course/src/main/java/com/frameforward/course/CourseController.java | controller | 无显式引用；复核扫描装配 | V |
| course/src/main/java/com/frameforward/course/CourseManager.java | manager | course | V |
| course/src/main/java/com/frameforward/course/CourseMappers.java | mapper | course | V |
| course/src/main/java/com/frameforward/course/CourseRules.java | business | course | V |
| course/src/main/java/com/frameforward/course/CourseService.java | service | ai-workflow、bootstrap、course、evaluation、portfolio | V |
| course/src/main/java/com/frameforward/course/CourseVersion.java | model/dto | course | V |
| course/src/main/java/com/frameforward/course/LessonProgressEntity.java | model/entity | course | V |
| equipment/src/main/java/com/frameforward/equipment/AccessoryTypeEntity.java | model/entity | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/CameraEntity.java | model/entity | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/CatalogController.java | controller | 无显式引用；复核扫描装配 | V |
| equipment/src/main/java/com/frameforward/equipment/CatalogExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| equipment/src/main/java/com/frameforward/equipment/CatalogMappers.java | mapper | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/CatalogService.java | service | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/CompatibilityRules.java | business | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/EquipmentManager.java | manager | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/LensEntity.java | model/entity | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/UserEquipmentBusiness.java | business | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/UserEquipmentController.java | controller | ai-workflow、course、evaluation、generation、shooting | V |
| equipment/src/main/java/com/frameforward/equipment/UserEquipmentEntity.java | model/entity | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/UserEquipmentExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| equipment/src/main/java/com/frameforward/equipment/UserEquipmentMapper.java | mapper | equipment | V |
| equipment/src/main/java/com/frameforward/equipment/UserEquipmentService.java | service | equipment、media、portfolio、shooting | V |
| evaluation/src/main/java/com/frameforward/evaluation/EvaluationBusiness.java | business | evaluation | V |
| evaluation/src/main/java/com/frameforward/evaluation/EvaluationManager.java | manager | evaluation | V |
| evaluation/src/main/java/com/frameforward/evaluation/EvaluationWorkCleanup.java | repository | 无显式引用；复核扫描装配 | V |
| evaluation/src/main/java/com/frameforward/evaluation/PhotoEvaluationController.java | controller | 无显式引用；复核扫描装配 | V |
| evaluation/src/main/java/com/frameforward/evaluation/PhotoEvaluationEntity.java | model/entity | evaluation | V |
| evaluation/src/main/java/com/frameforward/evaluation/PhotoEvaluationExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| evaluation/src/main/java/com/frameforward/evaluation/PhotoEvaluationMapper.java | mapper | evaluation | V |
| evaluation/src/main/java/com/frameforward/evaluation/PhotoEvaluationService.java | service | ai-workflow、auth、course、evaluation、generation、portfolio、shooting | V |
| evaluation/src/main/java/com/frameforward/evaluation/PortfolioEvaluationQuery.java | repository | evaluation、portfolio | V |
| evaluation/src/main/java/com/frameforward/evaluation/RetakeComparisonController.java | controller | 无显式引用；复核扫描装配 | V |
| evaluation/src/main/java/com/frameforward/evaluation/RetakeComparisonService.java | service | ai-workflow、auth、course、evaluation、generation、portfolio、shooting | V |
| evaluation/src/main/java/com/frameforward/evaluation/RetakeLinkEntity.java | model/entity | evaluation | V |
| evaluation/src/main/java/com/frameforward/evaluation/RetakeLinkMapper.java | mapper | evaluation | V |
| evaluation/src/main/java/com/frameforward/evaluation/ShootingSessionController.java | controller | 无显式引用；复核扫描装配 | V |
| evaluation/src/main/java/com/frameforward/evaluation/ShootingSessionEntity.java | model/entity | evaluation | V |
| evaluation/src/main/java/com/frameforward/evaluation/ShootingSessionMapper.java | mapper | evaluation | V |
| evaluation/src/main/java/com/frameforward/evaluation/ShootingSessionService.java | service | ai-workflow、auth、course、evaluation、generation、portfolio、shooting | V |
| evaluation/src/main/java/com/frameforward/evaluation/WorkEvaluationCleanup.java | service | evaluation、portfolio | V |
| generation/src/main/java/com/frameforward/generation/ReferenceImageBusiness.java | business | generation | V |
| generation/src/main/java/com/frameforward/generation/ReferenceImageCompletionProcessor.java | service | generation | V |
| generation/src/main/java/com/frameforward/generation/ReferenceImageController.java | controller | 无显式引用；复核扫描装配 | V |
| generation/src/main/java/com/frameforward/generation/ReferenceImageEntity.java | model/entity | generation | V |
| generation/src/main/java/com/frameforward/generation/ReferenceImageExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| generation/src/main/java/com/frameforward/generation/ReferenceImageManager.java | manager | generation | V |
| generation/src/main/java/com/frameforward/generation/ReferenceImageMapper.java | mapper | generation | V |
| generation/src/main/java/com/frameforward/generation/ReferenceImageService.java | service | evaluation、generation、shooting | V |
| media/src/main/java/com/frameforward/media/MediaController.java | controller | 无显式引用；复核扫描装配 | V |
| media/src/main/java/com/frameforward/media/MediaEntity.java | model/entity | evaluation、generation、media、shooting | V |
| media/src/main/java/com/frameforward/media/MediaExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| media/src/main/java/com/frameforward/media/MediaManager.java | manager | evaluation、generation、media、shooting | V |
| media/src/main/java/com/frameforward/media/MediaMapper.java | mapper | media | V |
| media/src/main/java/com/frameforward/media/MediaService.java | service | course、generation、media | V |
| media/src/main/java/com/frameforward/media/PortfolioMediaQuery.java | service | equipment、media、portfolio、shooting | V |
| media/src/main/java/com/frameforward/media/WorkMediaCleanup.java | service | media、portfolio | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioBusiness.java | business | portfolio | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioController.java | controller | 无显式引用；复核扫描装配 | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioFavoriteEntity.java | model/entity | portfolio | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioFavoriteMapper.java | mapper | portfolio | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioManager.java | manager | portfolio | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioService.java | service | ai-workflow、course、evaluation、portfolio | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioWorkDeletionController.java | controller | 无显式引用；复核扫描装配 | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioWorkDeletionJobEntity.java | model/entity | portfolio | V |
| portfolio/src/main/java/com/frameforward/portfolio/PortfolioWorkDeletionJobMapper.java | mapper | portfolio | V |
| shooting/src/main/java/com/frameforward/shooting/SceneAnalysisCompletionProcessor.java | service | shooting | V |
| shooting/src/main/java/com/frameforward/shooting/SceneAnalysisController.java | controller | 无显式引用；复核扫描装配 | V |
| shooting/src/main/java/com/frameforward/shooting/SceneAnalysisEntity.java | model/entity | shooting | V |
| shooting/src/main/java/com/frameforward/shooting/SceneAnalysisExceptionHandler.java | controller | 无显式引用；复核扫描装配 | V |
| shooting/src/main/java/com/frameforward/shooting/SceneAnalysisMapper.java | mapper | shooting | V |
| shooting/src/main/java/com/frameforward/shooting/SceneAnalysisService.java | service | evaluation、generation、shooting | V |
| shooting/src/main/java/com/frameforward/shooting/ShootingBusiness.java | business | shooting | V |
| shooting/src/main/java/com/frameforward/shooting/ShootingManager.java | manager | shooting | V |
| shooting/src/main/java/com/frameforward/shooting/ShootingPlanController.java | controller | 无显式引用；复核扫描装配 | V |
| shooting/src/main/java/com/frameforward/shooting/ShootingPlanEntity.java | model/entity | evaluation、generation、shooting | V |
| shooting/src/main/java/com/frameforward/shooting/ShootingPlanMapper.java | mapper | evaluation、generation、shooting | V |
| shooting/src/main/java/com/frameforward/shooting/ShootingPlanService.java | service | evaluation、generation、shooting | V |

## 嵌套数据类型

下列 record、枚举及 Request 类需逐一检查，跨层数据抽至所属业务 model/dto，枚举可置 model；异常类保持与抛出层对应。列出声明，不把含业务方法的类自动当作贫血 DTO。

| 所属文件 | 数据声明 |
| --- | --- |
| ai-workflow/AiTaskBusiness.java | Creation |
| ai-workflow/AiTaskRuntime.java | State、CreateRequest、Created、Trace、Status、BadRequest |
| auth/AuthController.java | RegisterRequest、LoginRequest、RefreshRequest、ChangePasswordRequest、AccountDeletionRequest |
| auth/AuthService.java | SessionTokens、AccountDeletionJob、AccessGrant |
| course/CourseBusiness.java | ProgressSnapshot、LessonContext |
| course/CourseCatalog.java | Course、Lesson |
| course/CourseController.java | Assignment |
| course/CourseService.java | Progress、Feedback、ContentVersion |
| equipment/CatalogController.java | CatalogResponse |
| equipment/CatalogService.java | Camera、Lens、AccessoryType、Compatibility |
| equipment/CompatibilityRules.java | Result |
| equipment/UserEquipmentController.java | CreateRequest、UserEquipmentList、BodyLensCombinationList |
| equipment/UserEquipmentService.java | EquipmentKind、Item、BodyLensCombination |
| evaluation/PhotoEvaluationService.java | Request |
| evaluation/PortfolioEvaluationQuery.java | Detail、WorkflowContext |
| evaluation/RetakeComparisonService.java | Request |
| evaluation/ShootingSessionService.java | Request |
| generation/ReferenceImageManager.java | NewReference |
| generation/ReferenceImageService.java | Request、InvalidRequest |
| media/MediaService.java | MediaResponse、UploadProgress |
| media/PortfolioMediaQuery.java | Item |
| portfolio/PortfolioController.java | FavoriteRequest |
| portfolio/PortfolioService.java | Filter、Page、Favorite、DeletionJob |
| shooting/SceneAnalysisService.java | Request、InvalidRequest |
| shooting/ShootingPlanService.java | Request、InvalidRequest |

## 统计

生产 Java 文件：113；common 当前仍无生产 Java 文件。所有上表业务模块的目录治理均待实施。

## 特殊职责的迁移前置条件

- AiTaskRuntime 是带事务、调度和 SSE 的服务入口，归 service；各 Graph 承担结果校验、规则过滤或比较，归 business。
- AiTaskCompletionProcessor 是完成任务的用例端口，两种实现带事务并调用 Manager，归 service。SceneAnalysisCompletionProcessor 的所有权判断须保持原行为。
- PortfolioMediaQuery 调用 MediaManager 并映射只读投影，归 service；Item 单独抽取至 model/dto。
- PortfolioEvaluationQuery、EvaluationWorkCleanup 直接操作 Mapper，AccountDatabaseCleanup 直接执行 SQL，当前数据访问实现归 repository；后续须通过 Manager 隔离上层调用，不能仅移至 manager 后保留直接 SQL 或混合职责。
- AccountDataCleanup、WorkMediaCleanup、WorkEvaluationCleanup 为用例端口，归 service；后续处理 PortfolioManager 依赖清理端口的职责边界，不把接口搬目录视为边界问题已修复。
- CourseCatalog 提供预置内容，归 component，内部 Course、Lesson 抽至 model/dto；CourseVersion 是数据 record，归 model/dto。
- FrameForwardApplication 留在 bootstrap 根包。配置移至 config；扫描配置、包可见构造器与测试必须同步验证。

## 后续 change 批次与依赖顺序

每个下表单元格中的配对代表一个独立 change，最多修改两个模块；生产与测试引用均计入。后续 change 必须先形成具体规格再实施。此处列出的兼容过渡策略尚未实现，不放宽最终零违规要求。

1. 对一个提供方先建立目标层中的实际实现与数据类型，并保留必要的旧入口适配以保持尚未迁移的消费者可编译。旧入口只用于当前迁移，须列明删除批次，不可复制业务逻辑或当作最终合规结构。AuthController.bearer 先抽至协议组件，逐个消费者切换，最后移动 Controller。
2. 按以下提供方顺序迁移，每行先完成所有消费者配对再清理旧入口。消费者对新数据类型的引用、Spring 注入及契约序列化一起验证；涉及 Entity/Mapper 直连时先建立所属模块的查询接口，避免继续扩散持久化结构。

| 顺序 | 提供方 | 依次执行的消费者配对 change | 主要迁移类型 |
| --- | --- | --- | --- |
| 1 | auth | auth + media；auth + equipment；auth + course；auth + ai-workflow；auth + shooting；auth + generation；auth + evaluation；auth + portfolio；auth + bootstrap | AuthController.bearer、AuthService 及嵌套数据、AccountDataCleanup |
| 2 | equipment | equipment + shooting | UserEquipmentService 及嵌套数据 |
| 3 | media | media + course；media + shooting；media + generation；media + evaluation；media + portfolio | MediaService、MediaManager、MediaEntity、PortfolioMediaQuery、WorkMediaCleanup |
| 4 | ai-workflow | ai-workflow + course；ai-workflow + shooting；ai-workflow + generation；ai-workflow + evaluation；ai-workflow + bootstrap | AiTaskRuntime 及嵌套数据、AiTaskCompletionProcessor、AiTaskEntity、RetakeComparisonGraph |
| 5 | shooting | shooting + generation；shooting + evaluation | ShootingPlanEntity、ShootingPlanMapper，改为所属模块查询边界 |
| 6 | evaluation | evaluation + portfolio | PortfolioEvaluationQuery、WorkEvaluationCleanup 及投影数据 |
| 7 | course | course + bootstrap | CourseService 及嵌套数据 |

每行完成后单独安排「提供方 + bootstrap」收尾 change（若无 bootstrap 引用则只改提供方），迁移内部剩余类、移除旧入口、启用该模块零违规断言。generation、portfolio 无其他模块的显式 Java import 消费，可各以「业务模块 + bootstrap」完成内部迁移与集成验证。最后 bootstrap 单模块整理 config/repository；common 的检查工具始终留在测试源码。

同一配对若超过七项任务，继续按协议辅助、数据类型、服务入口拆成串行 change。每个 change 执行验证 V，不能将全局多模块移动伪装成两个模块变更。清理完所有旧入口、跨层数据和依赖边界后，才对全部模块执行零违规验收。

## 检查工具的适用边界

### 2026-09-08 AI 任务入口最新进度

AI 的六个 Graph/Validator、Manager、Mapper、Repository 已归层；公共 Service 与独立 DTO 完成 foundation、course/shooting、generation/evaluation、bootstrap 和实际实现收尾五批迁移。旧 Runtime、嵌套 DTO、过渡委托与临时 bean 名已删除，Business 不再反向依赖 AuthService/Runtime。完整门禁行覆盖率 93.04%、分支覆盖率 85.53%、CPD 0.00%。剩余 AI 的 AiTaskEntity/AiTaskCompletionProcessor 及两个回调消费者的边界迁移，之后执行全模块零违规审计；总体目标尚未完成。

### 2026-09-08 shooting 最新进度

shooting 已完成 foundation、generation/evaluation consumers、finish 三批迁移；根包生产类型和临时 Mapper bean 已清理。请求 DTO 和业务异常独立，Repository 隔离数据访问，目录零违规测试通过。最终完整门禁通过 230 个测试，变更行覆盖率 93.25%、分支覆盖率 86.96%、CPD 0.00%。详见 `finish-shooting-layer-packages/verification.md`。剩余 ai-workflow，尚不能标记总体迁移完成。

JavaLayerPackages 校验顶层源码 package 与目录、业务根包混放、常见类名后缀与职责包。源码用 JDK 解析，缺失源码目录、空目录或语法错误均失败。它不分析方法调用、嵌套 DTO 的所属层，也不自动判定 Graph/Query/Cleanup 的业务职责；这些类型以本清单和后续依赖测试、人工审查为准。bootstrap 根包启动类的允许规则应在 bootstrap 专项测试中明确实现，不能通过整体排除业务根包处理。
