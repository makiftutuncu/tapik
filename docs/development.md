# Development & Contribution Guide

tapik uses a multi-module Gradle build with Kotlin DSL, configuration cache, and extensive code generation. Follow these practices when contributing.

## Repository Layout

- **Core libraries**: `core`, `codec`, `jackson`
- **Generators**: `spring-restclient`, `spring-webmvc`, `common-plugin`
- **Gradle integration**: `gradle-plugin`, shared conventions in the `build-logic` included build
- **Docs**: MkDocs site in `docs/` and `mkdocs.yml`

Module directories mirror the base package requirement: files under `src/main/kotlin` start immediately after `dev/akif/tapik` (for example, `core/src/main/kotlin/http/Endpoint.kt` declares `dev.akif.tapik.http.Endpoint`).

## Tooling & Commands

| Task | Command | Notes |
| --- | --- | --- |
| Full build & tests | `./gradlew build` | Compiles all modules, runs unit tests |
| Verification suite | `./gradlew check` | Includes ktlint and plugin checks |
| Formatting audit | `./gradlew ktlintCheck` | Use during CI or pre-commit |
| Auto-format | `./gradlew ktlintFormat` | Apply before committing formatting fixes |
| Dokka docs | `./gradlew dokkaGenerateHtml` | Must succeed; fails on undocumented public APIs |
| Generate outputs | `./gradlew tapikGenerate` | Refreshes generated clients, controllers, docs |

Tips:

- Pass `-PskipLint` for quick local iteration; CI should run without it.
- Disable configuration cache while debugging with `-Dorg.gradle.configuration-cache=false`.
- Plugin validation runs only when `-PrunPluginValidation` is set.

## Coding Standards

- Kotlin style follows `.editorconfig` (4-space indent, 120-char lines, LF endings).
- Avoid wildcard imports unless they simplify tapik package usage.
- Keep packages under `dev.akif.tapik.<module>`; respect the flattened directory structure.
- Document every public API with KDoc, including type parameters, parameters, returns, and throws.
- Generated sources belong under `build/` or `generated/`; keep them out of VCS unless explicitly required.

## Testing

- Use `kotlin("test")` with JUnit 5 (`junit-jupiter-params` for parameterized cases).
- Place unit tests adjacent to the module (`src/test/kotlin`).
- For focused runs: `./gradlew :module:test`.
- Integration flows (e.g., Spring-specific) should live in dedicated source sets such as `integrationTest`.
- Favour descriptive test names that outline behaviour rather than method names.

## Documentation Workflow

- Update KDoc and run `./gradlew dokkaGenerateHtml` before shipping changes.
- Regenerate MkDocs content (`mkdocs build`) locally if you modify Markdown or navigation.
- Generated documentation must include the footer `© {currentYear} Mehmet Akif Tütüncü`; the Dokka configuration enforces it—do not strip it.

## Dependency Management

- Declare dependencies through the version catalog (`gradle/libs.versions.toml`).
- Reuse bundles like `libs.bundles.testCommon` to keep versions aligned.
- Avoid hard-coded coordinates in build scripts; prefer catalog aliases.

## Contribution Checklist

1. Update or add tests that cover the behaviour you changed.
2. Run `./gradlew ktlintFormat build` and ensure both pass.
3. Refresh generated outputs (`tapikGenerate`, Dokka) if your change affects them.
4. Verify MkDocs renders locally (`mkdocs serve`) when touching documentation.
5. Craft concise, imperative commit messages (e.g., `Add Spring WebMVC generator`).
6. Open a PR with intent, summary, linked issues, and screenshots/logs for user-visible changes.
7. Request review from the relevant CODEOWNERS.

By following these guidelines, you keep tapik’s generators deterministic, its DSL approachable, and its documentation trustworthy.
