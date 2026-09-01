# generation/reference-image Specification

## Purpose
定义用户基于已选拍摄方案生成视觉参考图的行为，使参考图表达现场构图和氛围，同时不被误解为真实可完全复现的成片。

## Requirements

### Requirement: Plan-bound reference generation
An authenticated user SHALL request a reference image only from an owned environment image and a selected shooting plan, using `qwen-image-3.0-pro`.

#### Scenario: Valid generation request
- **WHEN** the selected plan and source image belong to the current user
- **THEN** a traceable asynchronous generation task is created

### Requirement: Reference disclosure
Every generated reference SHALL state that it represents composition and atmosphere, may differ from reality, and is not the sole evaluation standard.

#### Scenario: Reference is displayed
- **WHEN** generation succeeds
- **THEN** the image and all three disclosure points appear together

### Requirement: Independent failure
Reference generation failure SHALL NOT remove or invalidate the associated text shooting plan.

#### Scenario: Image model fails
- **WHEN** the generation task reaches FAILED
- **THEN** the selected text plan remains available and the user can retry generation
