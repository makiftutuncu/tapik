# Tapik Rewrite TODO

## WebMVC handler and adapter separation

- [x] Replace the generated WebMVC interface's public Spring mapping bridge with a generated adapter and automatic
  configuration.

  Generate a typed handler interface for each API. It should contain the API value, response types, and one abstract
  method per endpoint. User implementations should deal only with decoded Kotlin values and typed responses; raw wire
  values, Spring mapping annotations, content negotiation, encoding, and `ResponseEntity` should not appear in this
  interface.

  Generate a separate Spring WebMVC adapter for each handler interface. The adapter should own the mapping methods and
  delegate decoded requests to the typed handler. Its mapping methods are framework implementation details rather than
  part of the handler contract, so they do not need endpoint names with an `Http` suffix in the public handler API.

  Register generated adapters automatically. A user should only need to provide a Spring bean implementing the typed
  handler interface; they should not have to declare an adapter bean for every API. Registration should remain explicit
  and deterministic at generation/build time rather than scanning arbitrary packages or reflecting over user classes.
  One handler bean must remain able to implement multiple generated API interfaces.

  Before implementation, decide and specify:

  - how generated adapters and their auto-configuration are published and discovered;
  - whether the initial integration targets Spring Boot only or also provides a plain Spring registration mechanism;
  - how multiple candidate handler beans, qualifiers, conditions, and missing handlers behave;
  - how API instances are supplied to adapters;
  - adapter visibility, naming, proxy compatibility, and generated artifact identity;
  - integration tests covering zero user-written adapter configuration and multi-API handler composition.

## More

- [x] Reserve `plugin-` for compiler and build-tool integrations, and use `target-` for host-neutral generation target
  implementations. OpenAPI, Spring RestClient, and Spring WebMVC now use `target-` module, artifact, and package names.

- [x] Share target-neutral source generation used by the Spring targets through `common-plugin`. Sealed response
  hierarchies, byte-array value semantics, URI path templates, and status variant names are now defined once, while
  target compatibility and endpoint interpretation remain target-specific.

- [x] Optimize imports in generated Kotlin files. Qualified references are shortened deterministically where safe,
  while name collisions remain qualified and strings, characters, and comments are left untouched.

- [x] Keep format integrations opt-in. Core and generation targets consume only core format abstractions;
  `format-kotlinx` is a separately selected dependency and other integrations can replace or coexist with it.

- [ ] there should be a jackson 3 format module, implementing all and same functionality that kotlinx module has

- [ ] a lot of the utilities and private methods that are included in the generated files can be defined once in real
  code in corresponding modules so that generated code can refer to them. that will make generated files much cleaner.

- [ ] tapik should provide a BOM because it ships many modules and their version management will be much easier that way
  for the tapik users

- [ ] openapi target should also support yaml format with yml extension, in fact make it configurable and make yml the
  default format

## Remaining rewrite increments

- [ ] Add a plain Spring registration mechanism for generated WebMVC adapters without relying on component scanning.
  Keep Spring Boot auto-configuration as the zero-configuration path and reuse the same generated adapter descriptors.

- [ ] Complete the shared library fixture with author and rental operations. Use the expanded fixture in every target
  so new behavior is exercised against one representative contract.

- [ ] Add deterministic API include and exclude filters to host-neutral generation requests and expose them through
  the Maven plugin. Generation should continue to select every discovered API when no filters are configured.

- [ ] Allow source targets to consume APIs declared in the same Maven module without creating a compile/generate
  lifecycle cycle. Keep the existing compiled-contract dependency workflow available for separately published contracts.

- [ ] Support incremental Kotlin compilation and target regeneration without retaining stale API registry entries,
  generated classes, or runtime registration resources. Registry aggregation and generated outputs must remain
  deterministic and must not require users to run a clean build, including after target naming changes.

- [ ] Support multiple request-body media representations in generated RestClient clients. The generated API should
  make the selected representation explicit while retaining the single logical Kotlin body type.

- [ ] Add set, range, default, and described custom status matchers to the core DSL one matcher kind at a time. Preserve
  the concrete matcher kind in `Output` types and reject ambiguous alternatives while building the contract.

- [ ] Interpret each new status matcher in OpenAPI, RestClient, and WebMVC after its core representation is stable.
  A target that cannot represent a matcher must fail generation with the endpoint and matcher in its diagnostic.

- [ ] Add `path.remaining("name")` for required wildcard paths without weakening ordinary path-variable typing. Define
  its rendering and encoding once, then add explicit compatibility checks to each target.

- [ ] Let parameters, request bodies, and response alternatives carry their own OpenAPI-aligned documentation. Add
  these concepts incrementally without turning documentation into generic type noise on `Endpoint`.

- [ ] Extend the neutral schema algebra with union and discriminator concepts. Map them to OpenAPI before adding
  integration-specific derivation so unsupported polymorphism continues to fail early.

- [ ] Derive sealed and polymorphic Kotlin serialization models into the new union schema forms. Add shared schema and
  codec conformance cases that the Jackson integration can later reuse for behavioral parity.

- [ ] Introduce explicit API composition and nesting while preserving endpoint identity, inferred endpoint types, and
  declaration order. Separate API values must remain valid and require no migration.

- [ ] Add a small runnable Maven example that defines a contract artifact and consumes it for OpenAPI, RestClient, and
  WebMVC generation. Keep it aligned with the real plugin configuration through an automated build check.

- [ ] Restore the Tapik website and publish one unified multi-module Dokka API reference at `tapik.akif.dev`. Keep the
  per-module HTML documentation artifacts required by Maven Central alongside the unified site output.

- [ ] Audit the generated OpenAPI document model against OpenAPI 3.2 as new DSL concepts land. Protect each supported
  concept with the complete golden document and keep JSON and YAML renderers semantically identical.

- [ ] Perform a clean release-profile build and consume its staged artifacts from an external Maven fixture before the
  first 0.6.0 publication. Document the release and migration procedure once that rehearsal is repeatable.
