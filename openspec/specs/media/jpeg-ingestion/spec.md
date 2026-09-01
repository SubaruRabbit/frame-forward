# media/jpeg-ingestion Specification

## Purpose
定义环境照片和作品JPEG从Android导入到私有本地存储的统一入口，包括文件校验、EXIF解析、方向处理与敏感定位信息隔离。

## Requirements

### Requirement: Valid JPEG ingestion
The system SHALL accept decodable JPEG files up to 50 MB and SHALL reject oversized, corrupt or content-type-mismatched files with a clear reason.

#### Scenario: Corrupt JPEG upload
- **WHEN** a selected `.jpg` file cannot be decoded as JPEG
- **THEN** ingestion fails and no usable media record is created

### Requirement: EXIF and orientation processing
The system SHALL parse supported photography EXIF and honor EXIF orientation for preview and AI analysis without silently overwriting the original file.

#### Scenario: Rotated camera image
- **WHEN** a JPEG stores portrait orientation in EXIF
- **THEN** preview and analysis use the corrected orientation while the original remains unchanged

### Requirement: Privacy-preserving AI copy
The system SHALL remove GPS and unrelated sensitive metadata from the copy sent to AI while retaining separately parsed exposure and equipment fields needed for analysis.

#### Scenario: JPEG contains GPS
- **WHEN** an ingested JPEG has latitude and longitude EXIF
- **THEN** the AI input omits those fields and the scoring EXIF retains only allowed photography fields

### Requirement: Owned and recoverable upload task
Uploaded media SHALL be private to its owner, expose progress, and support retry without duplicating a completed upload.

#### Scenario: Retry after interrupted upload
- **WHEN** a network interruption occurs before upload completion
- **THEN** the user can retry and receives one completed media resource
