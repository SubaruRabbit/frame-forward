# shooting/retake-comparison Specification

## Purpose
定义原作品与重拍作品之间的显式关系和结构化改进说明，让用户看到构图、参数与主要问题的变化，而非只比较总分。

## Requirements

### Requirement: Valid retake relationship
An authenticated user SHALL link an owned evaluated work as a retake of another owned work from the same shooting session.

#### Scenario: Cross-user link attempt
- **WHEN** either work does not belong to the current user
- **THEN** the relationship is rejected

#### Scenario: Cross-session link attempt
- **WHEN** the two owned evaluated works belong to different shooting sessions
- **THEN** the relationship is rejected

### Requirement: Session-scoped evaluation
An authenticated user SHALL create a shooting session from an owned selected shooting plan and SHALL associate each evaluated work used for retake comparison with that session. The App SHALL show loading, success, and retryable failure states while creating a session.

#### Scenario: Evaluation is attached to an owned session
- **WHEN** the user evaluates an owned work for a shooting session created from the user's plan
- **THEN** the evaluation retains the session identifier and the session plan context

#### Scenario: Session is created
- **WHEN** the user selects an owned shooting plan and creates a session
- **THEN** the App records the returned session and makes it selectable for later evaluation work

#### Scenario: Session creation fails
- **WHEN** the create-session request fails
- **THEN** the App leaves the selected plan available and presents a retry action

### Requirement: Structured comparison
The comparison SHALL show both images and scores, dimension changes, composition changes, parameter changes, improved problems, remaining problems and next practice advice.

#### Scenario: Comparison opens
- **WHEN** the user selects a retake evaluation with a completed comparison
- **THEN** every comparison category is available in one result

### Requirement: Improvement beyond score
The system SHALL identify clear improvement only when at least one previous primary problem disappears or materially decreases, not solely because total score rises.

#### Scenario: Score rises without fixing issue
- **WHEN** the retake score is higher but the primary problem remains
- **THEN** the comparison does not claim that primary problem was improved
