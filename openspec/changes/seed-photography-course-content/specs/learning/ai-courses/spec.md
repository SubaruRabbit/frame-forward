## MODIFIED Requirements

### Requirement: Cached P0 course delivery
The system SHALL persist and deliver complete cached structured content for photography basics, mirrorless operation, equipment knowledge, and core tutorials for every P0 camera model before acceptance. Each delivered lesson SHALL include its core knowledge, correct and incorrect examples, an interactive judgment exercise, a practical assignment, and an assignment-feedback objective. The course detail response SHALL return the cached chapters and lessons without requiring a new generation, and SHALL return `404 Not Found` for a course identifier that does not exist.

#### Scenario: Open baseline course
- **WHEN** an authenticated user opens a cached P0 course
- **THEN** the response contains the ordered chapters, lessons, teaching content, examples, exercise, practical assignment, and lesson objective without triggering course generation

#### Scenario: Open first-batch model tutorial
- **WHEN** an authenticated user opens the core tutorial for a supported Sony or Nikon P0 model
- **THEN** the response contains the cached model tutorial and any unverified menu or firmware information is qualified against current firmware and official instructions

#### Scenario: Open missing course
- **WHEN** an authenticated user requests a course identifier that is not in the cached P0 courses
- **THEN** the course detail endpoint returns `404 Not Found`
