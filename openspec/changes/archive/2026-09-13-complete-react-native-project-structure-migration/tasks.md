## 1. Prerequisite audit

- [x] 1.1 In `frame-forward-app`, confirm all five feature-pair migration changes are complete and `architecture/legacy-migration.json` contains only `features/shooting-session/**`; verify `npm run architecture:check` passes before finalization.

## 2. Shooting-session migration

- [x] 2.1 Move the complete `frame-forward-app/features/shooting-session/**` subtree to `src/features/shooting-session/**`, add its minimal public `index.ts`, and verify no source file remains under the old module path.
- [x] 2.2 Update `frame-forward-app/src/app/workflowComposition.ts` and external tests to import only `@features/shooting-session`, preserving module-local relative imports; verify TypeScript and shooting-session Jest tests pass.

## 3. Exception removal

- [x] 3.1 Update `frame-forward-app/scripts/check-architecture.mjs` so missing legacy manifest enables strict mode and any root `features/**` source fails; delete `architecture/legacy-migration.json` and verify isolated allow/deny fixtures cover strict rejection.
- [x] 3.2 Update `frame-forward-app/README.md` and `docs/release-gates.md` to remove the temporary merge/release prohibition while retaining permanent architecture gates; verify the documentation names no active migration exception.

## 4. Final validation

- [x] 4.1 Run Formatter, zero-warning ESLint, architecture fixtures/check, Android/iOS TypeScript resolution, all Jest tests, and both production Metro bundles for `frame-forward-app`; record every command and result.
- [x] 4.2 Run the `frame-forward-app` Android debug and iOS Release Simulator native builds, record `BUILD SUCCESSFUL`/`BUILD SUCCEEDED`, and keep completion blocked if either platform fails.
