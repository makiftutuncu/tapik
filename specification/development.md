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
