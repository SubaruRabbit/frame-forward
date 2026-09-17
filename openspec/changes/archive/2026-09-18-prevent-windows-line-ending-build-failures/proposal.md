## Why

Windows Git checkout converts Java source to CRLF while the server's Spotless gate requires LF, causing local startup to fail before Spring Boot runs. The repository needs a versioned line-ending policy so every developer gets a buildable checkout.

## What Changes

- Enforce LF checkout for Java source through repository Git attributes.
- Vendor the existing SortPom ordering definition and reference it locally so formatting does not require GitHub.
- Apply the existing Spotless formatter across the server reactor to normalize all already-checked-out Java sources.
- Make the server architecture package checker normalize Windows path separators before comparing directory names with Java package declarations.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

None.

## Impact

Changes repository tooling, one formatting resource, server Java source line endings, and a test-only architecture checker; no API, business behavior, dependency, or data-model changes.

## Dependencies

- Existing Maven Spotless configuration, Eclipse JDT style file, and SortPom ordering definition.

## Non-goals

- Changing the global Git configuration, Java or POM formatting rules, or application startup behavior.
