## Context

See `proposal.md` for motivation. The foundation change established `src/**`, aliases, services boundaries and an AST checker, while an approved manifest keeps root `features/**` temporarily legal. OpenSpec limits one change to at most two app features, so five prerequisite changes migrate ten features; this finalizer owns only `shooting-session` plus removal of the exception.

Affected project: `frame-forward-app`. Affected modules: `shooting-session`, `src/app`, architecture tooling and migration/release documentation.

## Goals / Non-Goals

**Goals:**

- Move `shooting-session` without changing exports, runtime behavior, data contracts or tests.
- Make all production consumers use `@features/shooting-session` public entry exports.
- End with no source files under root `features/**`, no legacy exception, and passing strict architecture checks.
- Restore merge and release eligibility only after all quality, platform-resolution, Metro and native-build gates pass.

**Non-Goals:**

- Reorganizing feature internals beyond the minimum public entry.
- Changing screens, use-case sequencing, APIs, persistence, SDKs or native dependencies.
- Combining or renaming business features.

## Decisions

### 1. Finish through bounded prerequisite batches

Five prerequisite changes each migrate exactly two features. This finalizer migrates `shooting-session` and removes the exception only after those changes leave it as the sole manifest entry. This satisfies the per-change feature limit and provides independent rollback points. A single mass move was rejected because it would exceed the OpenSpec scope rule and obscure review evidence.

### 2. Preserve module internals and add one public entry

The existing `application`, `infrastructure`, screen and test files move together to `src/features/shooting-session/**`. A new `index.ts` exports only the screen/flow and composition symbols needed by `src/app`; module-local tests may keep relative internal imports. Flattening or redesigning internal layers was rejected because it adds unrelated behavioral risk.

### 3. App composition imports the feature public entry

`src/app/workflowComposition.ts` imports `createShootingSessionUseCases` and `createNetworkShootingSessionPort` from `@features/shooting-session`. No production consumer may deep-import the module. Re-exporting from a shared utility was rejected because it hides ownership and weakens the public-entry rule.

### 4. Remove, rather than disable, the exception

After the root feature tree is empty, delete `architecture/legacy-migration.json`. Update the checker so absence of the manifest means strict mode: any root `features/**` source is an error, with no warning path. Documentation must remove the temporary merge/release prohibition while retaining the permanent architecture gate. Extending the expiry or keeping an empty manifest was rejected because it could silently recreate legacy scope.

### 5. Validate platform graphs and native builds before lifting the block

Run isolated checker fixtures, full quality, Android/iOS TypeScript configurations, both production Metro bundles, Android debug build and iOS Release Simulator build. The exception and documentation block are removed in the same change but the change is not complete or releasable until every gate passes.

## Risks / Trade-offs

- [Prerequisite batches incomplete or out of order] → The finalizer checks that only `shooting-session` remains in the exact manifest before moving files.
- [Public entry omits a required symbol] → TypeScript, Jest and both Metro bundles must resolve all consumers.
- [Relative imports change meaning after the move] → Move the complete module subtree intact and let platform-aware architecture/type checks resolve every local edge.
- [Exception removal weakens enforcement] → Strict no-manifest mode rejects any root `features/**` source instead of skipping legacy validation.
- [Large native validation cost] → Reuse installed dependencies and build caches, but do not replace required builds with inferred evidence.

## Migration Plan

1. Complete the five prerequisite feature-pair changes, reducing the exact legacy manifest after each batch.
2. Confirm `shooting-session` is the only remaining root feature module.
3. Move its complete subtree to `src/features/shooting-session` and add the public `index.ts`.
4. Update app composition and any external tests to import the public entry; retain module-local relative imports.
5. Delete the empty root feature tree and `architecture/legacy-migration.json`; switch the checker and docs to permanent strict mode.
6. Run all quality, platform, bundle and native build gates and record results.

Rollback restores the module subtree, its exact manifest entries, previous checker behavior and the documented merge/release block as one unit. No data rollback is needed because runtime contracts and persistence formats do not change.
