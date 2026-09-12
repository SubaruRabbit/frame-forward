# Validation

Validated on 2026-09-13 from `frame-forward-app`.

| Gate | Result |
| --- | --- |
| `npm run quality` | Passed: Prettier, zero-warning ESLint, strict architecture check, TypeScript and all 29 Jest suites / 52 tests |
| `npm run architecture:test` | Passed: 12 fixtures, including strict-mode allow and reject cases |
| `npx tsc --noEmit -p tsconfig.android.json` | Passed |
| `npx tsc --noEmit -p tsconfig.ios.json` | Passed |
| Android production Metro bundle | Passed |
| iOS production Metro bundle | Passed |
| `npm run android:check` | Passed: `BUILD SUCCESSFUL in 10s` |
| `npm run ios:check` | Passed with project vendored CocoaPods and `DEVELOPER_DIR=/Applications/Xcode.app/Contents/Developer`: `** BUILD SUCCEEDED **` |

The final architecture scan passed for 122 Android and iOS source files with no root `features/**` tree and no legacy manifest. Initial native-build attempts only exposed sandboxed Gradle cache access and missing non-interactive Ruby/Xcode environment variables; the unchanged build commands passed after using the approved external build environment and the existing project vendored CocoaPods installation.

npm emitted its existing `http-proxy` deprecation warning, Metro emitted React Native's package-export fallback warning, Gradle emitted plugin deprecations, and Xcode emitted third-party/native metadata warnings. None failed a gate.
