## Context

See `proposal.md` for motivation. Git for Windows currently performs CRLF checkout for Java source, while the root POM requires UNIX line endings for Java. The POM formatter intentionally produces CRLF and currently downloads its SortPom ordering definition from GitHub. The affected scope is repository tooling, one server formatting resource, and `frame-forward-server/common` tests.

## Goals / Non-Goals

**Goals:**

- Make checked-out Java source conform to the existing LF requirement regardless of a developer's `core.autocrlf` setting.
- Make POM formatting independent of GitHub availability while preserving its existing CRLF output.
- Restore all already-checked-out server Java sources to the existing Spotless baseline.
- Make the architecture package checker yield the same directory-to-package result on Windows and Unix-like systems.

**Non-Goals:**

- Change the formatter's Java or POM style, global Git configuration, or server runtime configuration.
- Change Java source semantics or reformat files outside the server's Spotless scope.

## Decisions

- Add a root `.gitattributes` entry for Java files with `eol=lf`. Git attributes are versioned and take precedence over per-user CRLF checkout behavior. Do not apply the rule to POMs because their configured formatter intentionally emits CRLF. Alternative: document a local `git config` command; rejected because it is not reproducible for every clone.
- Vendor the current SortPom ordering XML under the server's version-controlled formatting configuration and replace the remote URL with a Maven-root-relative file reference. Alternative: keep the GitHub URL; rejected because it makes local quality gates depend on an unrelated external service.
- Run the established `mvn spotless:apply` task across the server reactor, then verify the full reactor formatting gate. This is required because Git attributes control future checkouts but do not rewrite Java files already present in a Windows worktree. Alternative: edit line endings manually; rejected because Spotless is the mandated formatter and single source of formatting truth.
- Normalize both `/` and `\\` to `.` when the architecture checker derives a package from a source file's relative directory. The checker currently replaces only `/`, so its Windows paths falsely differ from package declarations. Alternative: adjust Windows-only test expectations; rejected because that would hide an incorrect architecture check rather than make it portable.

## Risks / Trade-offs

- [Existing Windows working trees retain old CRLF Java content until normalized] → Apply Spotless to the server reactor during this change; future checkouts use the Git attribute.
- [Vendored sort order can diverge from upstream] → Preserve the current upstream definition verbatim and update it only through a reviewed change.
- [Architecture checks produce platform-dependent results] → Normalize directory separators before comparison and retain the existing tests as cross-platform regression coverage.

## Migration Plan

1. Add the Java Git attribute, vendor the current SortPom resource, and update the Maven reference.
2. Normalize all server Java sources with Spotless.
3. Run `mvn spotless:check` and the startup script.
4. Normalize architecture-checker directory separators and run the full quality gate on Windows.
5. Roll back by restoring the remote SortPom reference, removing the vendored resource and Git attribute, restoring the formatter changes, and reverting the separator normalization; then rerun the existing quality gate.
