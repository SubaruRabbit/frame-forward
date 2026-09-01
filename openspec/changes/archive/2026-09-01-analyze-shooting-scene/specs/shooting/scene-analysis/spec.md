## Purpose

定义将用户现场JPEG、拍摄目标和器材上下文转换为结构化环境理解的行为，为后续拍摄方案提供光线、空间、构图与安全依据。

## ADDED Requirements

### Requirement: Scene analysis input
An authenticated user SHALL create scene analysis with an owned environment JPEG, subject type, subject, target style, time constraint and selected equipment context.

#### Scenario: Missing environment image
- **WHEN** scene analysis is requested without an owned environment JPEG
- **THEN** the request is rejected before an AI task starts

### Requirement: Structured environment result
The `qwen3.8-max` workflow SHALL return scene type, subject candidates, light, background complexity, compositional structures, usable positions and available accessory opportunities.

#### Scenario: Successful analysis
- **WHEN** a valid scene task succeeds
- **THEN** the App displays the structured analysis rather than unparsed model text

### Requirement: Safety-qualified positions
The analysis SHALL identify visible hazards and SHALL exclude dangerous or prohibited positions from usable shooting locations.

#### Scenario: Roadway is visible
- **WHEN** a candidate camera position is within an active roadway
- **THEN** that position is not recommended and the result includes a safety warning
