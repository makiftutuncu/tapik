# Product specification

## Purpose

Tapik lets people who build, consume, or document HTTP APIs define those APIs once in Kotlin. Definitions must feel
idiomatic, remain arbitrarily composable, and make incorrect, incomplete, or inconsistent contracts difficult or
impossible to express.

Kotlin definitions are the sole source of truth. OpenAPI is an output and will not be consumed as an input language.

## Core promises

1. An endpoint is an immutable, ordinary Kotlin value.
2. Its inferred generic type retains its exact structural contract, including ordered paths, queries, headers,
   request bodies, response alternatives, matcher kinds, formats, and presence modes.
3. DSL construction prevents invalid states where Kotlin's type system can express the rule.
4. Value-level invariants fail while the contract is built.
5. A target fails the build when it cannot faithfully interpret a valid Tapik contract.
6. Arbitrary Kotlin composition is supported; Tapik does not define a statically analyzable subset of Kotlin.
7. Published contract artifacts behave like contract definitions on a consumer's classpath.

## Initial targets

Targets are developed in this order:

1. OpenAPI 3.2.0 output, with later specification upgrades handled as explicit product changes
2. Spring RestClient clients
3. Spring Web MVC servers

Kotlin serialization is the default format integration. Jackson follows for the Spring ecosystem.

The initial OpenAPI target is a programmatic interpreter over explicitly supplied, compiled `Api` values. It runs
after contract compilation, reads their immutable endpoint graphs without reflection, and fails generation when a
valid Tapik construct cannot be represented by OpenAPI 3.2.0. A later Maven plugin wraps the same interpreter.

## Non-goals for the foundation

- OpenAPI consumption
- Kotlin Multiplatform
- TypeScript generation
- API compatibility analysis beyond ordinary Kotlin/build breakage
- Package scanning, reflective endpoint discovery, or a duplicate metadata universe
- User data-model generation or ownership
