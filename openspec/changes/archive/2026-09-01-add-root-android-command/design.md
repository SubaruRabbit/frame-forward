## Context

See `proposal.md` for the motivation and scope. The repository contains the React Native application in `frame-forward-app`, while the developer-facing Android entry point is expected to be runnable from the repository root. The change also includes the already-required file-picker migration within the app module.

## Goals / Non-Goals

**Goals:**

- Keep the root command as a thin npm delegation to the app's existing `android` script.
- Keep the file-picker integration inside the photo-import feature and preserve the JPEG upload contract.
- Limit the affected areas to the repository npm metadata and the `frame-forward-app` photo-import dependency, implementation, and regression test.

**Non-Goals:**

- No changes to React Native, Gradle, Android SDK, server modules, or deployment configuration.
- No new product capability or API contract.

## Decisions

- **Root npm delegation:** Use a private root `package.json` with `npm --prefix ./frame-forward-app run android`. This keeps dependency resolution and React Native CLI execution owned by the app module, while providing a deterministic repository-level entry point. A shell wrapper was not chosen because it would add platform-specific behavior and duplicate npm's existing script resolution.
- **Maintained document picker:** Use `@react-native-documents/picker` and its `pick`, `types.images`, cancellation, and requested-type APIs. This matches the React Native 0.87-compatible package identified in the proposal and avoids retaining the incompatible picker implementation.
- **JPEG upload boundary:** Keep the selected document response as the input to `uploadJpeg`; map only `uri`, `name`, and `type` into the multipart file field. This preserves the existing server-facing upload shape and keeps picker details out of the upload transport.
- **Regression coverage:** Test the photo-import screen at the picker boundary by mocking the maintained package, asserting the image type request, and confirming that a valid JPEG exposes the upload action. Android installation remains an environment-level verification rather than a new application abstraction.

## Risks / Trade-offs

- **[Android tooling/device availability]** Root command verification depends on a configured Android SDK and connected emulator or device → verify the delegation and app tests locally even when an install target is unavailable, and report any environment limitation.
- **[Picker response compatibility]** Document metadata can vary by provider → retain the JPEG MIME/name validation and provide a user-visible error for unsupported selections.
- **[Uncommitted repository state]** The archive operation will move this change directory → archive only after artifact status, task completion, and validation are rechecked.

## Migration Plan

No runtime migration is required. Run the app's dependency installation/update and the targeted tests, verify the root `npm run android` delegation, then archive this completed OpenSpec change. Rollback consists of restoring the root npm metadata and the prior picker integration from version control.
