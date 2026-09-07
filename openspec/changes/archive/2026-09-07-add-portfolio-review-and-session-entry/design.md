## Context

See proposal.md for motivation. `portfolio/work-browser` already owns the authenticated work-detail surface, while `connect-photo-review-and-shooting-session-flows` defines the request and state handling for photo reanalysis, shooting-session creation, and retake comparison. The current portfolio detail has the source identifiers but no public handoff into those flows.

Affected project and modules: `frame-forward-app`, `features/portfolio`, public feature entry contracts, and `app` composition/navigation.

## Goals / Non-Goals

**Goals:**

- Make the work-detail identifier selection explicit and locally validated.
- Hand off minimal serializable identifiers and intent through public feature contracts.
- Preserve feature boundaries: Portfolio provides entry and selection; the target flow owns its request, operation state, retry, and result rendering.

**Non-Goals:**

- Do not place HTTP, DTO mapping, or target-flow operation state in Portfolio.
- Do not redesign the work browser, redefine session membership, or implement new server contracts.
- Do not expose data not already owned by the authenticated user.

## Decisions

- Use a small Portfolio-owned selection state derived from the displayed work: media id, selected owned plan, selected session, and a pair of eligible evaluation ids. This keeps disabled/unavailable actions deterministic. Passing a full work object was rejected because route parameters must stay minimal and data can become stale.
- Define public input contracts at the target feature boundary and let the app composition root translate Portfolio actions into navigation. Direct Portfolio imports of Photo Review or Shooting Session implementation were rejected because cross-feature access must use public contracts.
- Derive retake candidates only from completed evaluations carrying the selected session id, require distinct identifiers, and leave comparison unavailable otherwise. Permitting arbitrary portfolio evaluations was rejected because it would violate the same-session comparison requirement.
- Keep authorization as server-enforced and treat the owned detail dataset as UI eligibility only. Client-side ownership checks alone were rejected because they are not an authority boundary.

## Risks / Trade-offs

- [A work detail can be stale after a session or evaluation changes] → Re-enter or refresh the detail after target-flow success; unavailable state remains safe until fresh data arrives.
- [The dependent flow change is not yet applied] → Keep this change dependent on its public contracts and apply it after that change is implemented.
- [Several choices increase visual complexity] → Present choices progressively and keep unavailable actions explanatory rather than actionable.

## Migration Plan

1. Add the Portfolio selection and public handoff contracts with focused tests.
2. Wire the contracts in the app composition/navigation boundary after the dependent flows are available.
3. Roll back by removing the new detail actions; existing browse and detail behavior remains unchanged.
