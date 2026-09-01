# app-shell Specification

## Purpose
定义Android客户端在业务功能接入前必须具备的导航、登录门禁和通用页面状态，保证各功能change可独立挂载且体验一致。

## Requirements

### Requirement: Authenticated application navigation
The App SHALL expose 首页、学习、作品、我的 four top-level destinations after authentication and SHALL redirect unauthenticated access to the login flow.

#### Scenario: Unauthenticated launch
- **WHEN** no valid session exists at App launch
- **THEN** the App shows the login flow without exposing protected content

### Requirement: Restorable route state
The App SHALL restore the last valid top-level destination after process restart and SHALL fall back to 首页 when the saved destination is invalid.

#### Scenario: Restart on a valid destination
- **WHEN** an authenticated user restarts the App after viewing 作品
- **THEN** the App returns to 作品 after session validation

### Requirement: Common page states
Every feature screen mounted in the shell SHALL be able to present loading, empty, permission denied, network failure and retry states without a blank screen.

#### Scenario: Feature request fails
- **WHEN** a mounted feature reports a recoverable network error
- **THEN** the shell displays an error message and an actionable retry control
