## Context

See proposal.md for motivation. Portfolio currently consumes an evaluation-module read projection, but serializes its evaluation, source-plan and retake values as untyped maps. The existing data already contains the latest evaluation, session and source plan; the missing read path is the session's completed owned evaluation set. A stable OpenAPI detail shape and App mapper are required before Portfolio can pass identifiers to other public feature flows.

Affected projects and modules: `contracts`; `frame-forward-server` portfolio and evaluation modules; `frame-forward-app/features/portfolio` infrastructure/domain boundary.

## Goals / Non-Goals

**Goals:**

- Add a backward-compatible typed `workflowContext` to the existing owned-work detail response.
- Derive every optional reference through account-scoped queries and represent absence without failing the base detail.
- Keep candidate payloads minimal and sufficient for same-session retake selection.

**Non-Goals:**

- Do not expose a general plan browser, create sessions, start evaluations, or create comparisons.
- Do not change the legacy untyped detail fields in this change.
- Do not return scores, image URLs, evaluation result content, or another account's candidate identifiers in the workflow context.

## Decisions

- Add `workflowContext` alongside the existing detail fields rather than redefining them. This preserves response compatibility while giving the App a stable, closed schema. Replacing legacy fields was rejected as a breaking client change.
- Source workflow references from the latest owned evaluation for the displayed work; fetch candidates using that evaluation's owned session and only include evaluations with persisted completed results. This matches the comparison precondition and prevents unrelated or incomplete work from being selectable. Client-side filtering of arbitrary evaluations was rejected because it cannot establish ownership or completion.
- Model absent evaluation, session and source plan as nullable subobjects and candidates as an empty list. Omitting fields or using sentinel IDs was rejected because callers could not distinguish absence safely.
- Keep account checks in the evaluation query and retain Portfolio's existing deletion guard. The projection is an eligibility view, not an authorization replacement; target write APIs continue enforcing ownership.

## Risks / Trade-offs

- [Legacy clients may ignore the new field] → the additive response remains compatible and current fields are unchanged.
- [A session can contain many completed evaluations] → return minimal identifier-only candidates; pagination is deferred until an observed session size requires it.
- [Latest evaluation changes between detail reads] → each response is a consistent request-time snapshot; subsequent actions continue to receive server-side validation.

## Migration Plan

1. Update OpenAPI and its baseline with the additive response schema.
2. Add evaluation-owned workflow projection tests, then make Portfolio assemble the projection and prove owner/deletion isolation with service and integration tests.
3. Map the new DTO into the App Portfolio domain and verify unavailable contexts remain non-actionable.
4. Roll back by removing the additive field and mapper consumption; existing detail response behavior is unchanged.
