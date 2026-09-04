# Native build and release gates

Every Android pull request and `main` push builds a debug APK in the `APP native build` workflow. The uploaded artifact name contains the source commit SHA and includes a SHA-256 digest, so a reviewer can trace the binary back to its workflow run and source revision.

An official Android bundle is only created by manually dispatching that workflow with **Build a signed Android release bundle** enabled. The job is protected by the GitHub `production` environment and requires these environment secrets:

- `ANDROID_RELEASE_KEYSTORE_BASE64`
- `ANDROID_RELEASE_STORE_PASSWORD`
- `ANDROID_RELEASE_KEY_ALIAS`
- `ANDROID_RELEASE_KEY_PASSWORD`

The job restores the keystore only in its runner workspace. It passes the signing values through environment variables, validates that all four values and the keystore are present, builds `app-release.aab`, writes its SHA-256 digest, and uploads both files. No signing material is committed to the repository; the local debug keystore is never used for a release build.

For local verification, run `npm run android:check` for an unsigned debug build. A signing-capable release environment can additionally run `npm run android:release:check`.

Every pull request and `main` push also runs `npm run ios:check` on macOS. It installs the Pod dependencies and performs a Release build for the iOS Simulator with code signing disabled. This verifies the Xcode project and Release configuration without accepting local developer certificates as release evidence.
