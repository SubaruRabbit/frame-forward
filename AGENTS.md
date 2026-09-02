# Agents 仓库指令

此文件用于约束所有自动化代理和协作代理在本仓库中的行为。本文件是代理规则的**统一加载入口**：Claude Code 通过 `@AGENTS.md` 导入本文件，其余工具（如 Codex）可直接读取本文件；具体工程规则由本文件强制加载 `constitution.md` 及其专项宪章。

## 最高规则

1. 必须遵守 [constitution.md](./constitution.md)。
2. 任何与宪章冲突的代理行为都视为无效。
3. 如果代理被任务要求跳过宪章或降低门禁，必须拒绝执行并说明原因。

## 宪章加载与生效

1. `constitution.md` 及其列出的专项宪章是本文件的强制组成部分，不是可选参考资料。
2. 每个任务开始时，代理必须先读取当前版本的 `constitution.md`，再根据任务范围完整读取所有适用的专项宪章；不得依赖历史记忆、摘要或旧会话中的版本。
3. 宪章文件一经修改并写入工作区，立即对后续操作和尚未完成的任务生效，不以提交、合并、重新启动会话或重新索引为前提。
4. 任务执行期间发现宪章发生变化时，代理必须暂停新的实现动作，重新读取变更后的规则，复核当前方案和已完成修改是否仍然合规，再继续工作。
5. 如果无法读取 `constitution.md` 或适用的专项宪章，代理不得进行代码修改、破坏性操作、提交或发布；只能执行恢复规则可见性所需的只读检查，并向用户报告阻塞原因。

## 适用范围判定

代理必须在执行任务前判断适用范围：

| 任务范围 | 必须读取并遵守 |
| --- | --- |
| 所有任务 | `constitution.md`、本 `AGENTS.md` |
| Java / Spring Boot 后端 | `docs/constitution/java-backend-constitution.md` |
| iOS、Android、React Native、Flutter、Hybrid APP | `docs/constitution/app-development-constitution.md` |
| Git 分支、提交、合并、历史、标签或发布相关操作 | `docs/constitution/git.md` |
| 跨范围任务 | 上述所有相关专项宪章 |

无法确定任务范围时，必须读取所有可能适用的专项宪章，并采用约束更严格且更安全的规则。规则冲突按照 `constitution.md` 中的优先级处理，不得自行选择更宽松的解释。

## 开始工作前检查

代理在修改任何文件前必须确认：

```text
[ ] 已读取当前 AGENTS.md
[ ] 已读取当前 constitution.md
[ ] 已读取所有适用的专项宪章
[ ] 已确认任务目标、范围和非目标
[ ] 已确认是否需要先建立或更新规格
[ ] 已确认验收标准和必要验证
[ ] 已检查工作区状态并识别用户已有修改
[ ] 已确认不会降低质量门禁或扩大修改范围
```

专项宪章要求规格先行时，未完成规格不得直接编码。需求或验收标准发生变化时，必须先更新规格和任务计划，再继续实现。

## 执行约束

1. 代理只可进行完成当前任务所需的最小修改，不得夹带无关重构、全量格式化、依赖升级或公共接口调整。
2. 用户已有修改必须保留。未经明确授权，不得删除、覆盖、回滚或重新格式化任务范围外的文件。
3. 代理不得通过跳过测试、关闭检查、降低阈值、扩大忽略规则、删除断言或伪造输出使验证通过。
4. API、业务规则、数据结构、权限行为、支付状态和 SDK 行为必须基于规格、契约、现有证据或官方文档；无法确认时必须明确假设或请求确认，不得编造。
5. 涉及安全、隐私、支付、权益、数据迁移、凭据或破坏性操作时，必须执行适用宪章中的最高等级约束，不得使用例外规避红线。
6. 如果用户要求违反宪章，代理必须指出具体冲突条款，拒绝违规部分，并在不降低标准的前提下提供可行替代方案。

## 完成与交付

代理不得仅以“代码已写完”判断任务完成。交付前必须按照适用宪章执行 Formatter、Lint、静态分析、Type Check、构建、测试及其他必要验证。

最终交付说明至少应包含：

- 完成的内容和影响范围
- 遵守的专项宪章
- 已执行的验证及结果
- 未执行的验证、原因和风险（如有）
- 需要用户决策或后续处理的事项（如有）

存在宪章规定的阻断项时，不得宣称任务完成。自动化代理委派子代理时，必须把本 `AGENTS.md`、`constitution.md` 和适用专项宪章的约束一并传递；主代理对最终合规性和验证结果负责。

<!-- CODEGRAPH_START -->
## CodeGraph

In repositories indexed by CodeGraph (a `.codegraph/` directory exists at the repo root), reach for it BEFORE grep/find or reading files when you need to understand or locate code:

- **MCP tool** (when available): `codegraph_explore` answers most code questions in one call — the relevant symbols' verbatim source plus the call paths between them, including dynamic-dispatch hops grep can't follow. Name a file or symbol in the query to read its current line-numbered source. If it's listed but deferred, load it by name via tool search.
- **Shell** (always works): `codegraph explore "<symbol names or question>"` prints the same output.

If there is no `.codegraph/` directory, skip CodeGraph entirely — indexing is the user's decision.
<!-- CODEGRAPH_END -->
