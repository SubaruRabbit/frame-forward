# 产品需求文档版本索引

## 当前版本

- 版本：`v1.0.0-approved`
- 状态：评审通过
- 文档：[frame-forward-prd-v1.0.0-approved.md](versions/frame-forward-prd-v1.0.0-approved.md)
- 变更记录：[CHANGELOG.md](CHANGELOG.md)

## 文件命名规范

产品需求文档使用以下格式：

```text
frame-forward-prd-v{major}.{minor}.{patch}-{status}.md
```

示例：

```text
frame-forward-prd-v0.2.0-draft.md
frame-forward-prd-v1.0.0-approved.md
```

状态值：

- `draft`：编写或评审中
- `approved`：已经评审通过
- `deprecated`：已停止使用，仅用于历史追溯

## 版本号规则

- `major`：产品定位、核心范围或整体方案发生重大变化
- `minor`：新增功能模块、重要流程或成组需求
- `patch`：文字修正、需求澄清或不改变范围的小调整

评审中的文档使用 `0.x.x-draft`。首次正式评审通过后发布为 `v1.0.0-approved`。

## 更新流程

1. 不直接覆盖已经归档的版本文件。
2. 从当前版本复制出新的版本文件并更新版本号、状态和日期。
3. 在 `CHANGELOG.md` 中记录新增、调整、删除及待确认内容。
4. 更新本文件的“当前版本”链接。
5. 评审通过后保留草稿快照，并创建对应的 `approved` 版本。

## 历史版本

- [v0.3.1-draft](versions/frame-forward-prd-v0.3.1-draft.md)：明确个人优先、本地开发、暂缓部署及账号恢复方案
- [v0.3.0-draft](versions/frame-forward-prd-v0.3.0-draft.md)：确定镜头品牌、Qwen模型路由、注册方式和首版存储策略
- [v0.2.0-draft](versions/frame-forward-prd-v0.2.0-draft.md)：确定首批机型、账号策略、AI供应商和中国大陆首发范围
- [v0.1.1-draft](versions/frame-forward-prd-v0.1.1-draft.md)：确认中英文品牌名称
- [v0.1.0-draft](versions/photo-agent-prd-v0.1.0-draft.md)：产品名称确认前的初始评审稿
