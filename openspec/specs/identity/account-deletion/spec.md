# account-deletion Specification

## Purpose

Defines verified, irreversible account deletion so users can remove their private data while the product reports cleanup outcomes truthfully and prevents account recovery.

## Requirements

### Requirement: Verified account deletion

Account deletion SHALL require current-password verification and a second confirmation, then remove account, profile, equipment, progress, works, files and user AI results.

#### Scenario: Wrong password

- **WHEN** account deletion is requested with an incorrect current password
- **THEN** no user data is deleted

### Requirement: Truthful deletion failure

The system SHALL not report deletion complete when required records or files remain; failed cleanup SHALL be recorded and retryable.

#### Scenario: File cleanup fails

- **WHEN** a local media file cannot be removed
- **THEN** completion is withheld and cleanup can be retried without restoring product access to already hidden content

#### Scenario: Revoked-session job retry

- **WHEN** account deletion has revoked all ordinary sessions before cleanup finishes
- **THEN** the short-lived deletion-job credential can only query or retry that deletion job and cannot restore the account or create a new session

### Requirement: No automatic expiry or recovery

User images SHALL not expire automatically in the first version, and completed work or account deletion SHALL not be recoverable through the product.

#### Scenario: Image remains stored

- **WHEN** the user has not deleted a work or account
- **THEN** the image remains available without a retention countdown
