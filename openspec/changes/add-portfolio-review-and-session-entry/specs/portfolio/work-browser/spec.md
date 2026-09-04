## ADDED Requirements

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
