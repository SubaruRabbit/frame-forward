## 1. Repository line-ending policy

- [x] 1.1 Add a root `.gitattributes` rule that enforces LF for Java files only; verify with `git check-attr eol -- frame-forward-server/common/src/test/java/com/frameforward/common/architecture/JavaLayerPackages.java` and confirm `pom.xml` has no LF attribute.
- [x] 1.2 Vendor the current SortPom ordering XML under `frame-forward-server/config/` and change the root POM to use the local file; verify no `sortOrderFile` URL remains and the local resource exists.

## 2. Server formatting baseline

- [x] 2.1 Run the existing Spotless formatter for the full `frame-forward-server` reactor; verify Java source files use LF and `mvn spotless:check` passes.

## 3. Startup verification

- [x] 3.1 Run the full reactor `mvn spotless:check`; verify no formatting violations remain.
- [x] 3.2 Run `scripts/start-server.ps1`; verify Maven builds and the Spring Boot process reaches application startup or report any unrelated runtime configuration blocker.

## 4. Cross-platform architecture validation

- [x] 4.1 Normalize Windows and Unix-like directory separators in `JavaLayerPackages` before package comparison; verify the existing architecture tests pass and the full reactor quality gate succeeds.
