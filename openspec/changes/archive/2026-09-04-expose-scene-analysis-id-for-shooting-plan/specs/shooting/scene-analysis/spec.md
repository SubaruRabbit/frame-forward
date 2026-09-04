## MODIFIED Requirements

### Requirement: Structured environment result
The `qwen3.8-max` workflow SHALL return scene type, subject candidates, light, background complexity, compositional structures, usable positions and available accessory opportunities. A successful result SHALL also include the authenticated user's persisted `sceneAnalysisId` so that the App can request shooting plans for that completed analysis.

#### Scenario: Successful analysis
- **WHEN** a valid scene task succeeds
- **THEN** the App displays the structured analysis rather than unparsed model text and receives its `sceneAnalysisId`
