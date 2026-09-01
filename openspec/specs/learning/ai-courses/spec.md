# learning/ai-courses Specification

## Purpose
定义无需课程管理后台的AI摄影课程生成、缓存、事实约束、版本追溯、学习进度和实拍作业点评行为。

## Requirements

### Requirement: Cached P0 course delivery
The system SHALL provide cached photography basics, mirrorless operation, equipment and P0 model courses generated with `qwen3.7-plus` before acceptance.

#### Scenario: Open baseline course
- **WHEN** an authenticated user opens a P0 course
- **THEN** the cached structured content loads without requiring new generation

### Requirement: Traceable immutable content version
Every course version SHALL record model, prompt, source-material and generation versions and SHALL not overwrite a version after learning progress exists.

#### Scenario: Regenerate started course
- **WHEN** a course with user progress is regenerated
- **THEN** a new content version is created and prior progress remains linked to the old version

### Requirement: Equipment fact safeguard
Model-specific content SHALL use versioned equipment material and SHALL qualify uncertain menu or firmware details instead of inventing them.

#### Scenario: Menu path is not verified
- **WHEN** source material cannot confirm a camera menu path
- **THEN** the course tells the user to check current firmware and official instructions

### Requirement: Learning exercise loop
The system SHALL record lesson progress, accept practical assignments and return AI feedback focused on the current lesson objective.

#### Scenario: Submit lesson assignment
- **WHEN** a user uploads a valid assignment JPEG
- **THEN** feedback references the lesson objective and progress is updated
