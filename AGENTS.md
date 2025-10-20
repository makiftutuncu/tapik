# Repository Guidelines

## Project Structure & Module Organization
- tapik’s Kotlin library lives in `core`, `codec`, `jackson`, and `types`, with `spring-restclient` shipping the first integration that consumes those endpoint definitions; future integrations should follow the same module layout.
- `playground` is a sandbox of runnable examples for manual verification—it is not part of the published artifacts but is useful for demonstrating endpoint definitions and consuming the plugin after a local publish.
- `gradle-plugin` ships the user-facing plugin that currently generates Spring RestClient code and will host additional client/server generators as new use-cases appear.
- Each module keeps Kotlin sources in `src/main/kotlin` and tests in `src/test/kotlin`; shared build conventions sit in `buildSrc/kotlin-jvm.gradle.kts`, and dependency versions live in `gradle/libs.versions.toml` with toolchain defaults in `gradle.properties`.

## Build, Test, and Development Commands
- `./gradlew build` compiles the library, plugin, and supporting modules while running unit tests.
- `./gradlew check` runs the full verification suite (tests, ktlint, plugin checks).
- `./gradlew :gradle-plugin:publishToMavenLocal` installs the tapik Gradle plugin locally so the `playground` sandbox can exercise plugin tasks against new changes.
- `./gradlew :playground:run` runs the sample REST client in `playground/src/main/kotlin/RestClientExample.kt` for manual testing of endpoint definitions.
- `./gradlew ktlintCheck` enforces formatting; apply fixes with `./gradlew ktlintFormat` before committing.
- Always apply formatting via `./gradlew ktlintFormat`; avoid manual whitespace-only edits.

## Coding Style & Naming Conventions
- Kotlin sources follow the official style with 4-space indentation and LF endings (`.editorconfig` enforces 120-character lines, UTF-8, and trailing newline).
- Keep packages under `dev.akif.<module>`; classes and interfaces use PascalCase, functions and properties use camelCase, and constants prefer UPPER_SNAKE.
- Avoid wildcard imports unless justified; keep generated files under `/build` or `/generated` out of version control.

## Testing Guidelines
- Default to `kotlin("test")` (JUnit 5 on the Gradle plugin) for unit coverage; place specs beside the code under `src/test/kotlin`.
- Name test files with the subject plus `Test` (e.g., `MarkdownDocumentationInterpreterTest`) and group tests by behavior using descriptive `@Test` method names.
- Run focused suites with `./gradlew :module:test`; ensure `./gradlew check` passes before opening a pull request.
- Favor JUnit 5 parameterized tests (`junit-jupiter-params`) where multiple inputs exercise the same behavior, and keep API-level integration flows under dedicated `integrationTest` source sets (e.g., `spring-restclient`) so they can be invoked independently via Gradle tasks like `integrationTest`.

## Commit & Pull Request Guidelines
- Follow the existing Git history: short, imperative commit subjects (e.g., “Add name to type definitions”) without trailing punctuation.
- PRs should explain the intent, summarize key changes, link Jira/GitHub issues when relevant, and attach output snippets or screenshots for user-visible changes.
- Verify formatting and tests locally, note any skipped checks in the PR, and request reviews from CODEOWNERS for touched modules.
