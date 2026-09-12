## 1. Module moves

- [x] 1.1 Move the complete `frame-forward-app/features/auth/**` subtree to `src/features/auth/**`, add its minimal public `index.ts`, and verify no source remains at the old module path.
- [x] 1.2 Move the complete `frame-forward-app/features/equipment/**` subtree to `src/features/equipment/**`, add its minimal public `index.ts`, and verify no source remains at the old module path.

## 2. Boundary updates

- [x] 2.1 Update all external consumers of `auth` and `equipment` in `frame-forward-app` to import their `@features/<module>` public entries; verify module-local tests retain valid relative access and TypeScript resolves all imports.
- [x] 2.2 Remove exactly the moved modules' file entries from `frame-forward-app/architecture/legacy-migration.json`; verify `npm run architecture:check` reports no missing, stale or newly added legacy file.

## 3. Batch validation

- [x] 3.1 Run Formatter, zero-warning ESLint, architecture fixtures/check, TypeScript, all Jest tests, Android/iOS platform TypeScript checks and both production Metro bundles for `frame-forward-app`; record all command results and leave the batch incomplete on any failure.
