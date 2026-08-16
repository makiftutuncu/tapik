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

The rewrite starts with one `core` artifact. New artifacts are introduced only when a specification needs an
independently consumable boundary. The Kotlin serialization integration is expected to become
`dev.akif.tapik:format-kotlinx` with packages under `dev.akif.tapik.format.kotlinx`.

`core` remains dependency-free. `format-kotlinx` depends on Kotlin serialization and converts its serializers into
core codecs and schemas without leaking Kotlin serialization types into endpoint contracts.

The non-production `fixtures` module contains ordinary Tapik definitions grouped by domain package. Its initial
`dev.akif.tapik.fixtures.library` package covers books, authors, and rentals. Target modules consume this artifact in
their tests so every interpreter is verified against the same contract instead of maintaining target-specific
fixtures.

The `openapi` module interprets compiled `Api` values directly. It does not scan the classpath or copy contracts into
a neutral metadata model. Its public document model represents the OpenAPI output itself, and deterministic JSON is
the first rendering format.
