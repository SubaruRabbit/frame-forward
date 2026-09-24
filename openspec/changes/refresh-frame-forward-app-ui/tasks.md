## 1. Theme and shared presentation

- [x] 1.1 Update `frame-forward-app/src/theme/tokens.ts` with semantic photography-workspace colour, spacing, radius and elevation tokens; verify `npm run typecheck` passes.
- [x] 1.2 Update `frame-forward-app/src/components/ScreenState.tsx` to consume shared tokens and expose readable loading/error/retry feedback; verify its Jest tests cover state labels and pass.

## 2. Authenticated application shell

- [x] 2.1 Refactor `frame-forward-app/src/app/AppShell.tsx` and any needed local presentational components to implement the Canva-inspired header, shell surfaces and safe-area-aware navigation without changing route ownership; verify AppShell tests retain route restoration and unauthenticated gating.
- [x] 2.2 Make every `frame-forward-app` bottom-navigation target and workflow notice accessible, selected-state-aware and at least 44pt tall; verify targeted Jest assertions cover labels, selected state and notice announcements.

## 3. Integration verification

- [x] 3.1 Run `npm run format:check`, `npm run lint`, `npm run architecture:check`, `npm run typecheck` and `npm test -- --runInBand --no-watchman` in `frame-forward-app`; fix all changed-scope failures and record results.
- [ ] 3.2 Run `npm run android:bundle:check` in `frame-forward-app` and manually inspect the authenticated shell at narrow phone width with a large font scale; verify the four routes, safe areas, loading state and workflow notice remain visible and operable.

## 4. Canva home fidelity correction

- [x] 4.1 Add local, descriptive photography assets and a presentational home composition in `frame-forward-app/src/app/` that follows the inspected Canva section order while preserving the existing photo-import entry point; add focused tests for the visible labels and capture action.
- [x] 4.2 Update tokens and navigation labels/styles for the Canva `LIGHT JOURNAL` hierarchy; verify every navigation target remains accessible and route persistence remains unchanged.
- [x] 4.3 Run the required quality suite and inspect the authenticated home on the Android emulator against the Canva reference.
