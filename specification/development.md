# Development specification

## Toolchain

- Java 25
- Maven 3.9+ through the included wrapper
- Kotlin 2.4.10 or a later compatible 2.4 release
- Kotest 6.2

The build fails outside the supported Java and Maven ranges.

## Workflow

Every observable behavior follows this sequence:

1. Amend the product or DSL specification.
2. Add the smallest Kotest spec that demonstrates the behavior and observe it fail.
3. Implement the smallest coherent production change that makes it pass.
4. Refactor while the complete reactor remains green.
5. Run `./mvnw verify` before handing off a change.

Compile-failure behavior should eventually use a dedicated Kotlin compilation-test harness. Selecting that harness is
itself deferred until the first compile-failure behavior is implemented.

## Modules

The rewrite starts with one `dev.akif:tapik-core` artifact. New artifacts are introduced only when a specification
needs an independently consumable boundary. Maven module folders are flat beneath the repository root, omit the
`tapik-` artifact prefix, and use role prefixes such as `format-` and `plugin-`. The Kotlin serialization integration
is `dev.akif:tapik-format-kotlinx` with packages under `dev.akif.tapik.format.kotlinx`.

Kotlin source paths omit the common `dev/akif/tapik` package directories. A declaration in `dev.akif.tapik` lives
directly beneath `src/main/kotlin` or `src/test/kotlin`; subpackage paths begin after that common package.

| Module folder | Maven artifact |
| --- | --- |
| `core` | `dev.akif:tapik-core` |
| `format-kotlinx` | `dev.akif:tapik-format-kotlinx` |
| `fixtures` | `dev.akif:tapik-fixtures` |
| `plugin-compiler` | `dev.akif:tapik-plugin-compiler` |
| `plugin-core` | `dev.akif:tapik-plugin-core` |
| `plugin-openapi` | `dev.akif:tapik-plugin-openapi` |
| `plugin-maven` | `dev.akif:tapik-plugin-maven` |
| `plugin-maven-integration` | `dev.akif:tapik-plugin-maven-integration` |

`core` remains dependency-free. `format-kotlinx` depends on Kotlin serialization and converts its serializers into
core codecs and schemas without leaking Kotlin serialization types into endpoint contracts.

`core` exposes the minimal API registry provider contract needed by compiler-generated code. The `plugin-core` module
defines host-neutral registry loading, target configuration, target execution, and generated artifact contracts.
Target modules and adapters for Maven, Gradle, or command-line use depend on `plugin-core`; it must not depend on any
build-tool API.

Plugin module packages mirror their folder names: `plugin-compiler` uses `dev.akif.tapik.plugin.compiler`, `plugin-core`
uses `dev.akif.tapik.plugin.core`, `plugin-openapi` uses `dev.akif.tapik.plugin.openapi`, and `plugin-maven` uses
`dev.akif.tapik.plugin.maven`. The non-production `plugin-maven-integration` module verifies the complete Maven user
workflow against the reactor-built artifacts.

The non-production `fixtures` module contains ordinary Tapik definitions grouped by domain package. Its initial
`dev.akif.tapik.fixtures.library` package covers books, authors, and rentals. Target modules consume this artifact in
their tests so every interpreter is verified against the same contract instead of maintaining target-specific
fixtures.

The `plugin-openapi` module interprets compiled `Api` values directly. It does not scan the classpath or copy contracts
into a neutral metadata model. Its public document model represents the OpenAPI output itself, and deterministic JSON
is the first rendering format.
