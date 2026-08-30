## Purpose

定义登录用户维护个人机身、镜头和附件，以及设置主力器材的行为，使AI后续只能基于用户真实且兼容的设备生成建议。

## ADDED Requirements

### Requirement: Personal equipment collection
An authenticated user SHALL add and remove catalog cameras, lenses and accessories in a private equipment collection.

#### Scenario: Add first camera
- **WHEN** a user with no camera adds a catalog camera
- **THEN** the camera is saved and automatically becomes the primary camera

### Requirement: Single primary camera
A user SHALL have at most one primary camera and MAY switch it to another owned camera.

#### Scenario: Switch primary camera
- **WHEN** the user marks a second owned camera as primary
- **THEN** the previous camera loses primary status in the same operation

### Requirement: Executable equipment selection
The system SHALL flag incompatible body and lens combinations and SHALL NOT use them as the default combination for a shooting task.

#### Scenario: Select incompatible lens
- **WHEN** the user pairs a lens whose native mount does not match the selected body
- **THEN** the App shows the incompatibility and excludes that pair from default AI inputs
