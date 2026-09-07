## MODIFIED Requirements

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
