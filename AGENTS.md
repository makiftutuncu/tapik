# Repository Guidelines

## Project Structure & Module Organization
- tapik’s Kotlin library lives in `core`, `codec`, `jackson`, and `types`, with `spring-restclient` shipping the first integration that consumes those endpoint definitions; future integrations should follow the same module layout.
- `gradle-plugin` ships the user-facing plugin that currently generates Spring RestClient code and will host additional client/server generators as new use-cases appear.
- Each module keeps Kotlin sources in `src/main/kotlin` and tests in `src/test/kotlin`; shared build conventions now live under the `build-logic` included build (`build-logic/src/main/kotlin/buildlogic.convention.kotlin-jvm.gradle.kts`), and dependency versions live in `gradle/libs.versions.toml` with toolchain defaults in `gradle.properties`.

## Build, Test, and Development Commands
- `./gradlew build` compiles the library, plugin, and supporting modules while running unit tests.
- `./gradlew check` runs the full verification suite (tests, ktlint, plugin checks).
- Always apply formatting via `./gradlew ktlintFormat`; avoid trying to fix ktlint issues that can be auto-corrected.
- Gradle runs with configuration cache, task caching, and parallel execution enabled by default; pass `-Dorg.gradle.configuration-cache=false` if debugging cache issues locally.
- Run Gradle plugin validation only when needed by passing `./gradlew <task> -PrunPluginValidation`; release builds should supply that flag.

## Coding Style & Naming Conventions
- Kotlin sources follow the official style with 4-space indentation and LF endings (`.editorconfig` enforces 120-character lines, UTF-8, and trailing newline).
- Keep packages under `dev.akif.tapik.<module>`; classes and interfaces use PascalCase, functions and properties use camelCase, and constants prefer UPPER_SNAKE.
- The folders for the packages must start after `dev/akif/tapik` meaning the base package folders must be avoided for a more flat folder structure. For example a `dev.akif.tapik.test.Test` class must be in `test/Test.kt` file.
- Avoid wildcard imports unless justified, though wildcard imports for `dev.akif.tapik` packages and other tapik modules are acceptable when they improve clarity; keep generated files under `/build` or `/generated` out of version control.

## Testing Guidelines
- Default to `kotlin("test")` (JUnit 5 on the Gradle plugin) for unit coverage; place specs beside the code under `src/test/kotlin`.
- Name test files with the subject plus `Test` (e.g., `MarkdownDocumentationInterpreterTest`) and group tests by behavior using descriptive `@Test` method names.
- Run focused suites with `./gradlew :module:test`; ensure `./gradlew check` passes before opening a pull request.
- Favor JUnit 5 parameterized tests (`junit-jupiter-params`) where multiple inputs exercise the same behavior, and keep API-level integration flows under dedicated `integrationTest` source sets (e.g., `spring-restclient`) so they can be invoked independently via Gradle tasks like `integrationTest`.

## Dependency Management
- All module dependencies must be declared via catalog references in `gradle/libs.versions.toml`; avoid hard-coded coordinates in build scripts.
- Reuse catalog bundles (e.g., `libs.bundles.testCommon`) for shared test stacks to guarantee consistent versions across modules.

## Documentation
- Dokka must use the latest released version without local patches or custom workarounds.
- Apply a single shared Dokka configuration from the root build; modules should not duplicate task configuration.
- KDoc is mandatory for every public API and must include type parameter, parameter, property, return value, throws, and reference tags where applicable.
- Every code change must refresh the published Dokka output—update or add KDoc accordingly and run `./gradlew dokkaGenerate` to ensure the HTML is regenerated before shipping.
- Dokka builds should fail on undocumented symbols and publish source links back to `https://github.com/makiftutuncu/tapik`.
- Generated documentation must include the footer `© {currentYear} Mehmet Akif Tütüncü`.
- The documentation site is built with Antora. Source content lives under `docs/modules/ROOT`, the component descriptor is `docs/antora.yml`, and playbooks are `antora-playbook.yml` (release) and `antora-preview-playbook.yml` (PR preview).
- Use `npm install` once, then `npm run docs:preview` for local preview (writes to `build/site` and runs `docs/scripts/copy-api.sh`).
- Versioned docs are driven by `docs-v*` tags. The release workflow creates the docs tag from the release tag so Antora only scans tags that contain the Antora descriptor.

## Commit & Pull Request Guidelines
- Follow the existing Git history: short, imperative commit subjects (e.g., “Add name to type definitions”) without trailing punctuation.
- PRs should explain the intent, summarize key changes, link Jira/GitHub issues when relevant, and attach output snippets or screenshots for user-visible changes.
- Verify formatting and tests locally, note any skipped checks in the PR, and request reviews from CODEOWNERS for touched modules.
