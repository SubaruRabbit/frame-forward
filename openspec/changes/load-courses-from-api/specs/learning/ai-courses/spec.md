## ADDED Requirements

### Requirement: Authenticated course browsing in the App

The system SHALL let an authenticated user load the server-delivered P0 course catalog from the learning page and open a selected course's structured chapters and lessons.

#### Scenario: View delivered courses

- **WHEN** an authenticated user opens the learning page and the course catalog request succeeds
- **THEN** the page displays every returned course with its title, category, and server-provided lesson count

#### Scenario: Open a delivered course

- **WHEN** an authenticated user selects a course from the loaded catalog and its detail request succeeds
- **THEN** the page displays the returned structured chapters and lessons

### Requirement: Course catalog lesson total

The system SHALL include the total number of lessons in every authenticated cached P0 course catalog item without requiring the client to request each course detail.

#### Scenario: List cached course summaries

- **WHEN** an authenticated user requests the cached P0 course catalog
- **THEN** every returned course summary includes a non-negative lesson count for its current cached content version

### Requirement: Recoverable course delivery states

The system SHALL show explicit loading, empty, and failed states while loading the course catalog or a course detail, and SHALL allow the user to retry a failed request.

#### Scenario: Retry a failed catalog request

- **WHEN** the course catalog request fails and the user selects retry
- **THEN** the system issues a new catalog request and replaces the failed state with the resulting loading, empty, or content state

#### Scenario: Course no longer exists

- **WHEN** a selected course detail is no longer available
- **THEN** the system shows a recoverable failure state and lets the user return to the catalog
