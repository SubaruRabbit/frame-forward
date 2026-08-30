## Purpose

定义登录用户删除单张私有作品及其文件、EXIF和关联AI结果的行为，并保证失败清理可重试且不会被错误报告为完成。

## ADDED Requirements

### Requirement: Owned work deletion
An authenticated user SHALL delete only an owned work, including its original, previews, reference derivatives, structured EXIF and bound AI results.

#### Scenario: Delete owned work
- **WHEN** all records and files for an owned work are removed
- **THEN** the work becomes inaccessible and deletion is reported complete

### Requirement: Shared data preservation
Work deletion SHALL NOT remove shared equipment catalog or course content and SHALL NOT remove unrelated works.

#### Scenario: Delete one work
- **WHEN** a user deletes one of multiple works
- **THEN** the remaining works and shared reference data stay accessible

### Requirement: Retryable cleanup failure
The system SHALL not report completion while a required file or record remains and SHALL persist enough state to retry cleanup.

#### Scenario: Local file removal fails
- **WHEN** a required media file cannot be removed
- **THEN** deletion remains incomplete and a retry can continue cleanup
