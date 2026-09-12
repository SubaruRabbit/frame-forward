# Validation

Validated on 2026-09-13 from `frame-forward-app`.

| Gate | Result |
| --- | --- |
| `npm run quality` | Passed: Prettier, zero-warning ESLint, architecture check, TypeScript and all 29 Jest suites / 52 tests |
| `npm run architecture:test` | Passed: 10 fixtures |
| `npx tsc --noEmit -p tsconfig.android.json` | Passed |
| `npx tsc --noEmit -p tsconfig.ios.json` | Passed |
| Android production Metro bundle | Passed |
| iOS production Metro bundle | Passed |

npm emitted its existing `http-proxy` deprecation warning and Metro emitted React Native's package-export fallback warning; neither gate failed.
