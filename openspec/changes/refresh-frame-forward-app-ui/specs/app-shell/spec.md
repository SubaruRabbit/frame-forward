## ADDED Requirements

### Requirement: Cohesive mobile coaching shell
After authentication, the App SHALL present the four existing destinations in a visually consistent mobile shell that reflects the approved FrameForward UI direction. The shell SHALL preserve the selected destination, provide a clear active navigation state, respect device safe areas, and keep every navigation target accessible with a descriptive label and a touch target of at least 44 points.

#### Scenario: Selecting a destination
- **WHEN** an authenticated user selects 学习、作品 or 我的 from the bottom navigation
- **THEN** the corresponding destination is displayed, the selected item is announced as selected, and its active state is visually distinguishable without relying on color alone

#### Scenario: Safe-area constrained device
- **WHEN** the App is rendered on a device with a display cutout or gesture navigation area
- **THEN** the top content and bottom navigation remain fully visible and operable within the safe areas

### Requirement: Canva-aligned photography home composition
On the 首页 destination, the App SHALL expose a `LIGHT JOURNAL` photography-coach home composition before the existing capture and analysis feature entry points. It SHALL include the visible sections 今日练习, 今日摄影灵感, 快速学习 and 最近评分, and SHALL retain an accessible route to the existing capture workflow.

#### Scenario: Viewing the authenticated home
- **WHEN** an authenticated user opens 首页
- **THEN** the first viewport presents 记录今天的光, the 今日练习 primary action, two labelled inspiration cards, the quick-learning row and a recent-score summary in that order

#### Scenario: Using the primary capture action
- **WHEN** a user activates 开始拍摄 from 今日练习
- **THEN** the existing photo import and scene-analysis workflow is brought into view without changing its feature state ownership or network behavior

### Requirement: Consistent visual feedback for app shell states
The App SHALL use the shared visual language for shell loading, notices, cards and primary actions, with text labels and accessible feedback for state changes. The visual refresh SHALL NOT alter authentication behavior, persisted-route behavior, API calls, AI workflow execution or feature data.

#### Scenario: App startup
- **WHEN** the App is validating the session and restoring the saved route
- **THEN** it displays a labelled loading state in the shared visual language until the destination can be rendered

#### Scenario: Workflow feedback
- **WHEN** a workflow initiated from the 作品 destination succeeds or fails
- **THEN** the App presents a readable, accessible notice using the shared visual language while retaining the current destination
