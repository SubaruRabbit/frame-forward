# ai/task-runtime Specification

## Purpose
定义所有Qwen工作流共享的异步任务生命周期、模型路由、结构校验、重试和客户端恢复行为，使具体AI能力保持稳定且可追溯。

## Requirements

### Requirement: Observable AI task lifecycle
Every AI operation SHALL use QUEUED, RUNNING, SUCCEEDED, FAILED or CANCELLED and SHALL expose its current state by task ID.

#### Scenario: App reconnects
- **WHEN** the App restarts while a task is RUNNING
- **THEN** querying its task ID returns the current state and any completed result

### Requirement: Structured output validation
The runtime SHALL validate model output against the workflow schema, retry invalid output at most twice, and return FAILED without exposing partial output after exhaustion.

#### Scenario: Invalid output persists
- **WHEN** three consecutive outputs fail schema validation
- **THEN** the task becomes FAILED, preserves its input references and allows manual retry

### Requirement: Configured Qwen routing
The runtime SHALL select model IDs from server configuration and SHALL record workflow, model, prompt, rule and schema versions for every task.

#### Scenario: Task completes
- **WHEN** a configured Qwen workflow succeeds
- **THEN** its trace identifies the actual model and all relevant versions

### Requirement: Progress delivery and idempotency
The runtime SHALL provide SSE progress plus query fallback and SHALL prevent duplicate AI execution for repeated creation with the same valid idempotency key.

#### Scenario: Duplicate create request
- **WHEN** the client repeats a task creation request with the same idempotency key
- **THEN** the server returns the original task ID without starting another model call
