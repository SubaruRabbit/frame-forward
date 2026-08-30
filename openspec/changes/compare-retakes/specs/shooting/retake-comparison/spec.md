## Purpose

定义原作品与重拍作品之间的显式关系和结构化改进说明，让用户看到构图、参数与主要问题的变化，而非只比较总分。

## ADDED Requirements

### Requirement: Valid retake relationship
An authenticated user SHALL link an owned evaluated work as a retake of another owned work from the same shooting session.

#### Scenario: Cross-user link attempt
- **WHEN** either work does not belong to the current user
- **THEN** the relationship is rejected

### Requirement: Structured comparison
The comparison SHALL show both images and scores, dimension changes, composition changes, parameter changes, improved problems, remaining problems and next practice advice.

#### Scenario: Comparison opens
- **WHEN** both linked works have completed evaluations
- **THEN** every comparison category is available in one result

### Requirement: Improvement beyond score
The system SHALL identify clear improvement only when at least one previous primary problem disappears or materially decreases, not solely because total score rises.

#### Scenario: Score rises without fixing issue
- **WHEN** the retake score is higher but the primary problem remains
- **THEN** the comparison does not claim that primary problem was improved
