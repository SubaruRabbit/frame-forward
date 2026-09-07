# evaluation/photo-evaluation Specification

## Purpose
定义登录用户对JPEG作品获得可解释且可复用的1～100分评价、画面优缺点、EXIF技术诊断和具体重拍建议的行为。

## Requirements

### Requirement: Structured photo evaluation
The `qwen3.8-max` workflow SHALL return a 1–100 total, weighted dimension scores, strengths, primary problems, one priority improvement and ordered retake steps.

#### Scenario: Evaluation succeeds
- **WHEN** a valid owned JPEG is evaluated
- **THEN** all result sections are stored and displayed in the PRD-defined order

### Requirement: EXIF-aware diagnosis with fallback
The system SHALL diagnose available exposure and equipment EXIF and SHALL continue visual evaluation when EXIF is missing while identifying diagnostic limits.

#### Scenario: EXIF is missing
- **WHEN** an otherwise valid JPEG contains no supported EXIF
- **THEN** visual scores and feedback are returned with a parameter-diagnosis limitation notice

### Requirement: Versioned stable result
The system SHALL reuse an existing evaluation for the same user, image content and scoring-rule version unless the user explicitly requests reanalysis. The App SHALL let a user explicitly request reanalysis from a completed evaluation and SHALL show loading, success, and retryable failure states.

#### Scenario: Duplicate evaluation
- **WHEN** the same content is submitted under the same rule version
- **THEN** the existing evaluation version is returned without another model call

#### Scenario: Reanalysis succeeds
- **WHEN** the user requests reanalysis from a completed evaluation
- **THEN** the App submits the reanalysis request and displays the returned structured evaluation

#### Scenario: Reanalysis fails
- **WHEN** the reanalysis request fails
- **THEN** the App preserves the previous evaluation and presents a retry action

### Requirement: Qualified conclusions
The evaluation SHALL distinguish observation from inference and SHALL NOT present artistic score or uncertain blur, focus or exposure causes as absolute fact.

#### Scenario: Blur cause is ambiguous
- **WHEN** image evidence cannot distinguish subject motion from camera shake
- **THEN** the diagnosis uses qualified language and offers checks for both causes
