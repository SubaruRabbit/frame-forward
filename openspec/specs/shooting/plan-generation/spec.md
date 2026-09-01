# shooting/plan-generation Specification

## Purpose
定义如何把已完成的环境分析与用户器材转换为少量可执行拍摄方案，每个方案同时覆盖机位、相机参数、构图、姿势和安全限制。

## Requirements

### Requirement: Two to three ranked plans
The system SHALL generate two or three plans labeled as safe, atmospheric or creative and SHALL identify the recommended plan.

#### Scenario: Plan generation succeeds
- **WHEN** a user requests plans from a completed scene analysis
- **THEN** two or three ranked structured plans are returned

### Requirement: Complete actionable plan
Each plan SHALL include position, distance, camera height, orientation, focal length, exposure starting points, metering, focus, drive mode, white balance, composition, pose, accessory use and ordered shooting steps.

#### Scenario: User opens a plan
- **WHEN** a generated plan is displayed
- **THEN** every required field is available without relying on free-form model text

### Requirement: Equipment-constrained recommendation
A plan SHALL prioritize the selected owned camera, compatible lens and owned accessories, and SHALL provide a no-new-equipment alternative.

#### Scenario: User has no flash
- **WHEN** flash could improve the scene but the user owns none
- **THEN** flash is not the only executable plan and an available-light alternative is included

### Requirement: Qualified parameters and safety
Exposure values SHALL be marked as starting points, incompatible settings SHALL be rejected, and unsafe positions SHALL not appear in a plan.

#### Scenario: Rule validation fails
- **WHEN** a model proposes a setting unsupported by the selected camera
- **THEN** the plan is corrected or rejected before delivery
