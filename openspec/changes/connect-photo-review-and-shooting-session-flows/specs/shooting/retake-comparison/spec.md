## MODIFIED Requirements

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
The comparison SHALL show both images and scores, dimension changes, composition changes, parameter changes, improved problems, remaining problems and next practice advice. The App SHALL request and display the completed comparison for the selected retake evaluation.

#### Scenario: Comparison opens
- **WHEN** the user selects a retake evaluation with a completed comparison
- **THEN** every comparison category is available in one result
