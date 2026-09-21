# tapik follow-up findings TODO

This checklist converts the issues in `review/findings.md` into implementation tasks. For every public-behavior change, update the relevant specification first, add a failing Kotest specification, implement the smallest coherent change, and finish with `./mvnw verify`.

## P1 — Runtime and cross-target correctness

- [x] Make generated Spring WebMVC APIs fail fast when a handler is missing.
  - Add a specification for handler discovery and startup validation, covering both `@EnableTapikWebMvc` and Spring Boot auto-configuration.
  - Reproduce the failure with generated sources in a package outside the host application's default component-scan package.
  - Ensure every discovered generated API descriptor must resolve to exactly one compatible handler bean; do not silently leave an API unregistered when no handler exists.
  - Report a startup error that identifies the generated API/handler contract and explains that no implementation bean was found.
  - Preserve the supported single-handler and primary-handler cases, and do not create duplicate controller registrations when generated classes are otherwise visible to component scanning.

- [x] Generate shared endpoint input/output types for Spring client and server targets.
  - Specify a target-neutral ownership and naming model for generated contract types used by both the client and server.
  - Move shared request/body variants, response variants, response headers, and related endpoint types out of the generated client/server interfaces so both targets reference the same stable fully qualified types.
  - Ensure generating only the client or only the server still produces a complete, compilable result.
  - Ensure generating both targets does not emit duplicate files, create artifact-ownership conflicts, or produce target-dependent type identities.
  - Add an integration test in which a generated server handler delegates to the generated client and returns its response directly without mapping between otherwise identical types.
  - Preserve stable names and references across endpoint selection and repeated generation.

## P2 — Schema derivation and public contract API

- [x] Support standard Java time types and recursively configurable schema overrides in Jackson derivation.
  - Specify the built-in Jackson/OpenAPI mappings for at least `LocalDate` as a `string` with `date` format and `Instant` as a `string` with `date-time` format.
  - Make these mappings work when the types appear as object properties and inside nullable, collection, and map shapes—not only as root values.
  - Introduce a public configuration mechanism for registering a schema or schema provider for a custom Kotlin/Java type, and apply it recursively during object derivation.
  - Keep explicit root-schema support and serializer/schema compatibility validation intact.
  - Prevent derivation caches from leaking results between configurations with different custom registrations.
  - Add KDoc and tests for built-in temporal mappings, nested custom mappings, nullability/collection composition, and independent configurations.

- [x] Rename or hide `Api.id` so it cannot collide with common user contract properties.
  - Choose a non-conflicting name for the API's internal/public identity and update the relevant specification and KDoc.
  - Allow an `Api` subclass to declare its own `id` value for a resource path variable without conflicting with an inherited `String` property.
  - Add a compile-time usage test showing that an expression such as `get(root / id)` resolves to the user's path value rather than the API identity.
  - Update catalog selection, endpoint identity, generation naming, diagnostics, and any other internal consumers without changing their identity semantics.
  - Document the migration. Do not retain an `id` compatibility alias if doing so preserves the original collision.

- [x] Expose documentation through the smart constructors for headers, query parameters, and path variables.
  - Extend the public construction path for `Header`, `QueryParameter`, and `PathVariable` so callers can supply descriptions and deprecation state while retaining inferred value types.
  - Preserve existing concise calls and their defaults.
  - Verify that supplied documentation reaches generated OpenAPI parameters/headers and any other target that consumes it.
  - Add KDoc and compile-time/runtime tests for documented and deprecated values created through the smart constructors.

- [ ] Make the HTTP `Status` catalog exhaustive while retaining validated custom statuses.
  - Define named values for the complete standard HTTP status-code set supported by tapik's contract model, grouped or documented clearly enough to remain maintainable.
  - Provide a public way to construct a status from an integer within the supported HTTP status ranges.
  - Reject invalid/out-of-range codes with a clear diagnostic.
  - Preserve value equality and compatibility with endpoint outputs, OpenAPI generation, and Spring client/server generation.
  - Add coverage for every named status, valid custom codes, range boundaries, and invalid codes.

## P3 — Diagnostics and ergonomic defaults

- [ ] Make conflicting OpenAPI component diagnostics identify the actual difference.
  - Include the conflicting component name and a concise, deterministic comparison of the existing and candidate definitions.
  - Identify the differing schema path or fields where practical, including conflicts introduced by nested or normalized component names.
  - Include source/contract context when it is available without creating a parallel metadata model solely for diagnostics.
  - Add tests that assert useful messages for representative structural, requiredness, and nested-component conflicts.

- [ ] Add a neutral catalog of common HTTP headers.
  - Add discoverable `Header` members/factories for common request and response headers, including at least `Authorization`.
  - Give each predefined header the appropriate canonical wire name and value format while preserving type inference.
  - Keep custom header definitions available and avoid taking a dependency on Spring constants in the core model.
  - Document the predefined set and test its names, formats, and generated OpenAPI representation.

- [ ] Expand the common `MediaType` catalog and retain ergonomic custom construction.
  - Define a documented baseline of common media types comparable to the useful framework-standard constants, with canonical serialized values.
  - Preserve or improve the public construction path for custom media types without coupling the core model to Spring.
  - Decide and specify normalization, equality, and parameter handling for custom values such as charset-bearing media types.
  - Add tests for predefined values, custom values, equality, and their use in endpoint bodies and generated targets.

- [ ] Add enum helpers to the default format interfaces.
  - Expose an ergonomic generic member such as `StringFormat.enum<MyEnum>()` on the relevant defaults surface.
  - Encode enum values by name and decode them with a clear failure when the input does not match a constant.
  - Preserve the concrete enum type in inferred endpoint input/output types.
  - Specify how enum values appear in generated schemas/OpenAPI and ensure the format's schema and codec stay aligned.
  - Add tests for round trips, invalid input, schema enumeration, and use through the public defaults APIs.

## Completion

- [ ] All new and updated public APIs have KDoc.
- [ ] All affected specification files describe the final behavior.
- [ ] Focused regression and generated-source compilation tests pass.
- [ ] `./mvnw verify` passes from a clean checkout.
