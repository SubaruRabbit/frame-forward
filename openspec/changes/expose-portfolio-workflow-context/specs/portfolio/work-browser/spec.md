## MODIFIED Requirements

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
