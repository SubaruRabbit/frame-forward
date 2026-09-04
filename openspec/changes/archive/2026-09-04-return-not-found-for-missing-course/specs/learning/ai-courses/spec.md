## MODIFIED Requirements

### Requirement: Cached P0 course delivery
The system SHALL provide cached photography basics, mirrorless operation, equipment and P0 model courses generated with `qwen3.7-plus` before acceptance. The course detail endpoint SHALL return `404 Not Found` when the requested course does not exist.

#### Scenario: Open baseline course
- **WHEN** an authenticated user opens a P0 course
- **THEN** the cached structured content loads without requiring new generation

#### Scenario: Open missing course
- **WHEN** an authenticated user requests a course identifier that is not in the cached P0 courses
- **THEN** the course detail endpoint returns `404 Not Found`
