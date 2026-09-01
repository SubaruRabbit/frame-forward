# identity/password-authentication Specification

## Purpose
定义不依赖短信验证码的密码账号与会话行为，使注册用户能够安全访问AI及个人数据，同时明确首版不提供账号恢复。

## Requirements

### Requirement: Username or email registration
The system SHALL register an account with a unique username or normalized email and an 8–64 character password containing at least two character categories.

#### Scenario: Duplicate identifier
- **WHEN** a registration uses an existing username or normalized email
- **THEN** the system rejects the request without creating another account

### Requirement: Protected authenticated session
The system SHALL issue renewable authenticated sessions after valid password login and SHALL deny protected resources when no valid session exists.

#### Scenario: AI request without session
- **WHEN** an unauthenticated client creates an AI task
- **THEN** the system returns an unauthenticated error and creates no task

### Requirement: Logout and password change
An authenticated user SHALL be able to log out, and SHALL be able to change the password only after verifying the current password.

#### Scenario: Successful password change
- **WHEN** the current password is correct and the new password is valid
- **THEN** the password changes and previously issued refresh sessions become invalid

### Requirement: No recovery promise
Registration and account settings SHALL state that password recovery and account recovery are unavailable in the first version, and SHALL expose no nonfunctional recovery entry.

#### Scenario: User reviews registration warning
- **WHEN** the registration form is displayed
- **THEN** the no-recovery warning is visible before submission
