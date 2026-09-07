# portfolio/work-browser Specification

## Purpose
定义登录用户私有浏览其作品、评分、EXIF、拍摄方案和重拍关系的统一作品集，并提供有限且明确的收藏与筛选行为。

## Requirements

### Requirement: Private work list and detail
An authenticated user SHALL view only owned works and SHALL open a detail containing image, EXIF, evaluation, source plan and retake links when available. The detail SHALL also expose a typed workflow context containing the work media identifier, its latest owned evaluation identifier and optional session identifier, the owned source plan identifier and plan context when available, and the selected owned session's completed evaluation candidates when available. Each candidate SHALL include only its evaluation identifier and media identifier. Missing optional associations SHALL be represented by null values or an empty candidate collection.

#### Scenario: Request another user's work
- **WHEN** a user requests a work they do not own
- **THEN** the system reveals no work data or workflow context

#### Scenario: Detail has a completed session evaluation
- **WHEN** an authenticated user opens an owned work whose latest completed evaluation belongs to an owned shooting session
- **THEN** the detail returns the media and evaluation identifiers, the session identifier, the session source-plan identifier and context, and the completed owned evaluation candidates for that session

#### Scenario: Detail has no optional workflow association
- **WHEN** an authenticated user opens an owned work without an evaluation, source plan, or shooting session
- **THEN** the detail remains available and returns null optional workflow references with an empty comparison-candidate collection

#### Scenario: Deleted work is requested
- **WHEN** a user requests a work whose deletion has started
- **THEN** the system reveals no work data or workflow context

### Requirement: Detail workflow entry for owned work
An authenticated user viewing an owned work detail SHALL be able to start a reanalysis for that work, select an owned shooting plan to start a shooting session, and select two completed evaluations from the same owned session to open their retake comparison. The detail SHALL expose an unavailable state when the required media, plan, session, or completed evaluations are absent.

#### Scenario: Reanalysis starts from an owned work
- **WHEN** the user selects reanalysis for an owned work with a media identifier
- **THEN** the system opens the photo-evaluation flow with only that media identifier and the optional selected session identifier

#### Scenario: Session starts from an owned plan
- **WHEN** the user selects an owned shooting plan and starts a session from the work detail
- **THEN** the system opens the shooting-session flow with the selected plan identifier and its plan context

#### Scenario: Same-session evaluations open comparison
- **WHEN** the user selects an original evaluation and a different retake evaluation that both belong to the same owned session
- **THEN** the system opens the retake-comparison flow with the two evaluation identifiers

#### Scenario: Required workflow data is unavailable
- **WHEN** the work detail lacks a required identifier or no eligible same-session evaluation pair exists
- **THEN** the unavailable action is not submitted and the user is given a clear unavailable explanation

### Requirement: Portfolio filtering
The portfolio SHALL filter owned works by subject, camera and lens and SHALL support toggling favorite state.

#### Scenario: Filter by camera
- **WHEN** the user selects one owned camera
- **THEN** the list contains only works recorded with that camera

### Requirement: Empty and partial data handling
The portfolio SHALL display clear empty states and SHALL still show a work whose optional EXIF, evaluation or plan is absent.

#### Scenario: Work has no EXIF
- **WHEN** the user opens an owned work without EXIF
- **THEN** the image remains accessible and EXIF is marked unavailable
