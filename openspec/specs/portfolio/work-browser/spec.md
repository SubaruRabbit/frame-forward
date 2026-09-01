# portfolio/work-browser Specification

## Purpose
定义登录用户私有浏览其作品、评分、EXIF、拍摄方案和重拍关系的统一作品集，并提供有限且明确的收藏与筛选行为。

## Requirements

### Requirement: Private work list and detail
An authenticated user SHALL view only owned works and SHALL open a detail containing image, EXIF, evaluation, source plan and retake links when available.

#### Scenario: Request another user's work
- **WHEN** a user requests a work they do not own
- **THEN** the system reveals no work data

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
