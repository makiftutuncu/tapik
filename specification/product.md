# Product specification

## Purpose

tapik lets people who build, use, or document HTTP APIs define them once in Kotlin. Definitions should feel natural in
Kotlin, remain easy to combine, and make incorrect, incomplete, or conflicting endpoints difficult or impossible to
express.

Kotlin definitions are the sole source of truth. OpenAPI is an output and will not be consumed as an input language.

## Core promises

1. An endpoint is an immutable, ordinary Kotlin value.
2. Its inferred type retains the endpoint's structure, including ordered paths, queries, headers,
   request bodies, response alternatives, matcher kinds, formats, and presence modes.
3. DSL construction prevents invalid states where Kotlin's type system can express the rule.
4. Values enforce their own rules when created.
5. A target fails generation when it cannot faithfully represent a valid endpoint.
6. Arbitrary Kotlin composition is supported; tapik does not define a statically analyzable subset of Kotlin.
7. A published API artifact behaves like the same API defined in the consuming project.

tapik-owned structural values have shallow snapshot semantics. Public collection inputs are copied before they are
stored, and exposed collections cannot mutate the stored structure. Encoded byte arrays crossing a tapik transport
value boundary are copied on ingress and access. tapik does not copy user-owned domain objects, formats, codecs, or
values carried by those abstractions.

This boundary includes decode-error lists, URI segments, endpoint tags, status sets, generated API registries,
discovery catalogs, generation requests, compiled types/endpoints, generated-source model collections, and every
collection in the public OpenAPI model. Construction and `copy` both snapshot inputs; destructuring exposes only
the protected values. JVM mutable casts (including map views and entries) cannot modify stored structure. Lists and
maps retain declaration order and sets retain set semantics. Nested structural containers, such as response-header
lists and OpenAPI JSON keyword values, are protected at each container boundary; domain values are not deep-copied.
Generated registries retain their API instances but expose an unmodifiable list. User-supplied registry implementations
remain responsible for their own getters; catalogs always snapshot what they read from those providers.

## Initial targets

Targets are developed in this order:

1. OpenAPI 3.2.0 output, with later specification upgrades handled as explicit product changes
2. Spring RestClient clients
3. Spring Web MVC servers

Format integrations are opt-in. tapik supports Kotlin serialization and Jackson 3, and a project may use either or both.

The OpenAPI target reads selected, compiled `Api` values. It runs after API compilation, reads their immutable endpoint
values without reflective discovery, returns generated artifacts without writing them, and fails generation when a
valid endpoint cannot be represented by OpenAPI 3.2.0.

The Maven and Gradle adapters activate the compiler plugin, load generated API registries from configured API
dependencies or the current project, find generation targets through shared registries, translate host configuration
and API selection, invoke targets, and manage generated files and build lifecycle. A future command-line host can use
the same registries, selection rules, configuration, and generation results.

## Versioning and compatibility

Version 0.6.0 establishes the first compatibility baseline and is intentionally incompatible with earlier tapik
implementations. From that baseline, tapik follows Semantic Versioning with an explicit pre-1.0
convention: patch releases in one `0.y` line are backward-compatible, while an incompatible public change increments
the minor version. After 1.0, incompatible changes increment the major version.

The compatibility policy covers public Kotlin declarations in production artifacts, documented compiler, Maven,
and Gradle configuration, stable generation target IDs, and the documented names and signatures of generated public Kotlin
declarations. An incompatible change to any of these surfaces requires the corresponding version increment and
migration guidance. Internal implementation, generated formatting, and behavior outside the documented supported
surface are outside the compatibility policy.

The current supported surface being intentionally narrow does not make it experimental. Maven and Gradle Kotlin/JVM
are build hosts; the built-in targets are OpenAPI 3.2.0, Spring RestClient, and Spring WebMVC; same-project generation
occurs after ordinary main-source compilation; and format integrations require explicit schemas for serializer shapes
they cannot derive. New hosts, targets, and derivation coverage can be added compatibly.

tapik does not determine whether an API's endpoints remain compatible with a deployed version. API versioning and
rollout remain application responsibilities.

## Non-goals for the foundation

- OpenAPI consumption
- Kotlin Multiplatform
- TypeScript generation
- API compatibility analysis beyond ordinary Kotlin/build breakage
- Package scanning, reflective endpoint discovery, or a duplicate metadata universe
- User data-model generation or ownership
