## Context

See proposal.md. The foundation migration already provides aliases, services boundaries and the AST architecture checker. This batch affects only `photo-review` and `portfolio` in `frame-forward-app`; the temporary manifest remains active for every unmigrated feature.

## Goals / Non-Goals

**Goals:**

- Move both complete module subtrees to `src/features/**` without behavioral changes.
- Add minimal public entries and update `src/app/workflowComposition.ts` 与 `src/app/AppShell.tsx` to use them.
- Remove exactly these two modules from the legacy manifest while keeping it exact.

**Non-Goals:**

- Redesigning internal folders, business flows, UI, contracts, storage or SDK behavior.
- Migrating any third feature or removing the overall exception.

## Decisions

### Preserve complete module subtrees

Move source and module-local tests together so relative internal imports retain their meaning. Flattening or renaming internals was rejected because this batch is a structural migration only.

### Publish minimal feature entries

`src/features/photo-review/index.ts` and `src/features/portfolio/index.ts` export only symbols required outside their module: 点评用例/网络适配器；作品集屏幕、用例和网络适配器. External production imports use `@features/<module>`; tests inside a module may keep relative internal access.

### Reduce the exception atomically

Delete the moved files' exact entries from `architecture/legacy-migration.json` in the same edit. The checker must pass with no stale manifest entries and no new root feature files. The exception itself remains until the finalizer.

### Keep validation proportional but platform-aware

Run Formatter, ESLint, architecture fixtures/check, TypeScript, Jest, Android/iOS platform TypeScript resolution and both Metro bundles. Native projects and dependencies are unchanged, so native builds remain the finalizer's responsibility.

## Risks / Trade-offs

- [Missing public export] → TypeScript, app tests and both Metro bundles must resolve consumers.
- [Relative path drift] → Move each complete subtree intact before changing external imports.
- [Manifest mismatch] → Architecture check compares the manifest to all remaining root feature files.
- [Behavioral change hidden in cleanup] → Limit edits to paths, public entries and imports; existing tests remain unchanged.

## Migration Plan

1. Move `photo-review` and `portfolio` complete subtrees into `src/features`.
2. Add minimal public entries and update all external consumers.
3. Remove the two modules' entries from the legacy manifest.
4. Run the required quality, platform-resolution and bundle gates and record results.

Rollback restores both subtrees, their manifest entries and previous consumer imports as one unit.
