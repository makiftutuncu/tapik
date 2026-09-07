# tapik rewrite stabilization TODO

This checklist converts the second-round review at `80d66cca` into implementation tasks. Follow the repository workflow
for every item: update the relevant file under `specification/` first, add a failing Kotest specification, implement the
smallest coherent correction, then refactor with `./mvnw verify` green.

## P1 — release blockers

- [x] Make optional and defaulted WebMVC parameter decoding structurally generated.

  Replace the unrestricted `String.replace` in `WebMvcGenerator.appendDecodedParameter` with decoder rendering that
  accepts the raw-value expression explicitly. Ensure parameter names cannot alter endpoint or inclusion property
  access, endpoint IDs, locations, or other generated source text.

  Acceptance criteria:

  - Optional and defaulted query/header decoding uses the local `raw` value without rewriting any other expression.
  - Generated source compiles when an endpoint property is named `pageRaw` and its optional query is named `page`.
  - Generated source compiles when an inclusion path overlaps an allocated `*Raw` parameter name.
  - Required, fixed, scalar, and repeated parameter behavior remains unchanged.

- [x] Reject generated artifact collisions with unowned files.

  Extend `ArtifactOwnership`/`ArtifactWriter` so a generated path may replace a destination only when that path is
  already owned by the same execution. An exact-path unowned file must cause generation to fail before any destination
  or ownership state is changed.

  Acceptance criteria:

  - An unowned file at the exact generated source, resource, or documentation path is never overwritten or claimed.
  - The failure identifies the colliding relative path and execution owner.
  - The original file bytes and ownership manifest remain unchanged after failure.
  - Regeneration still replaces paths already owned by the same execution.
  - Unrelated unowned files and paths owned by other non-conflicting executions remain untouched.

- [x] Reject late generated-output collisions with application classes and resources.

  Apply the same exact-path ownership rule in `GeneratedOutputSynchronizer` before copying staged output into
  `target/classes`. Treat an existing path absent from all tapik ownership state as application-owned.

  Acceptance criteria:

  - A generated `.class`, Kotlin module metadata file, or registration resource cannot overwrite an unowned output.
  - Collision detection completes before stale deletion or copying begins.
  - A failed synchronization preserves all application bytes, prior generated output, and ownership state.
  - Output previously owned by the same execution can still be replaced and removed as stale.
  - Conflicts with another execution continue to fail with both path and owner in the diagnostic.

- [x] Preserve already-encoded literal paths in generated RestClient requests.

  Separate the DSL's already-encoded literal path structure from raw path-variable, remaining-path, and query values
  that Spring must encode. Do not pass an encoded template through a builder mode that escapes percent signs again.

  Acceptance criteria:

  - A literal fragment such as `a%20b` is requested as `/a%20b`, never `/a%2520b`.
  - Encoded reserved characters in literal fragments remain byte-for-byte stable.
  - Ordinary path-variable values are encoded exactly once.
  - Every remaining-path element is encoded exactly once and remains one segment.
  - A black-box request test combines encoded literals, ordinary variables, remaining segments, and queries.

- [x] Complete shallow snapshot semantics across every public structural value.

  Audit all production modules for public collection and byte-array inputs/getters. Copy public collection inputs before
  storage and expose collections that cannot mutate stored structure, including through JVM/Kotlin mutable casts. Do not
  copy user-owned domain objects, formats, codecs, or values carried by them.

  The audit must include at least:

  - `DecodeResult.Failure.errors`;
  - `Uri.segments`, endpoint tags, and `StatusSet.statuses`;
  - compiler-generated `ApiRegistry.apis`;
  - `ApiCatalog.apis` and `GenerationTargetCatalog.targets`;
  - `GenerationRequest.apis`, `CompiledApi.endpoints`, `KotlinType.arguments`, and generated-source model collections;
  - every list/map in the public OpenAPI document, schema, component, and discriminator model;
  - all existing transport byte-array boundaries.

  Acceptance criteria:

  - Mutating an input collection after construction cannot change the tapik value.
  - Casting an exposed collection to a mutable JVM/Kotlin type cannot change stored structure.
  - Collection order and tag/set semantics remain as specified.
  - Data-class `copy` or component functions cannot reintroduce an unsnapshotted boundary.
  - A systematic snapshot test suite covers representative list, set, map, registry, OpenAPI, and byte values.

## P2 — protocol and architecture stabilization

- [ ] Define and enforce neutral HTTP media-type syntax and identity.

  Specify the accepted grammar and equality rules for `MediaType`. Validate malformed values before a target runs, and
  prevent semantically duplicate body representations such as type/subtype case variants. Keep core independent of
  Spring APIs.

  Acceptance criteria:

  - Malformed media types fail during contract construction, or every target rejects them during generation with API
    and endpoint context if construction-time validation is intentionally deferred.
  - Type/subtype and parameter-name case follow HTTP semantics.
  - Parameter-value comparison distinguishes case-sensitive values while handling defined case-insensitive values.
  - Semantically duplicate body media types fail the body uniqueness invariant.
  - OpenAPI never emits an invalid Content Object key.
  - Spring applications do not defer contract media-type syntax errors to request time or context startup.

- [ ] Compare `Accept` parameters using HTTP-aware semantics.

  Replace raw parameter string equality in `selectResponseMediaType`. Preserve the existing effective quality,
  specificity, declaration-order, and most-specific-`q=0` behavior.

  Acceptance criteria:

  - `text/plain;charset=utf-8` is compatible with `Accept: text/plain;charset=UTF-8`.
  - Parameter names compare case-insensitively.
  - Opaque parameter values retain the correct case-sensitive behavior.
  - Quoted and unquoted equivalent values behave consistently with Spring/HTTP parsing.
  - Mismatched non-quality parameters still reject the representation.
  - Existing wildcard, quality, specificity, invalid-header, and declaration-order tests remain green.

- [ ] Make Jackson schema derivation faithful to effective serializer shape.

  Detect Jackson behavior that changes a Kotlin type's JSON shape, including `@JsonValue`, property serializers, class
  serializers, and registered module serializers. Support only shapes that can be proven; otherwise require an explicit
  schema or fail format construction with `SchemaDerivationException`.

  Acceptance criteria:

  - An ordinary wrapper serialized through `@JsonValue` does not receive an object schema unless its wire value is an
    object.
  - Property-level custom serializers cannot silently leave a contradictory property schema.
  - Class/module serializers cannot silently leave a contradictory root schema.
  - Callers have a documented explicit-schema path for serializer behavior that cannot be inferred.
  - Conformance tests encode representative values and verify that their JSON shape agrees with the attached schema.
  - Existing Jackson visibility, naming, ignored-property, ordering, enum, recursion, default, and deprecation behavior
    remains intact.

- [ ] Give generated top-level Spring types stable cross-artifact identities.

  Replace or constrain selection-dependent numeric disambiguation for client/server/controller top-level types. The
  identity of a generated public type should not change because an unrelated earlier API is selected, and independent
  contract artifacts must not unknowingly publish the same generated class identity.

  Acceptance criteria:

  - Adding or excluding an unrelated same-simple-name API does not rename an existing generated public type.
  - Two APIs with the same simple class name but different qualified identities cannot publish the same generated
    top-level class/resource identity silently.
  - The policy works across separate same-module generated contract artifacts, not only within one request.
  - Client, handler, controller, artifact path, and WebMVC descriptor names use one coherent identity policy.
  - Naming remains deterministic and is documented as a compatibility contract.
  - Local endpoint/member collisions may continue using deterministic namespace-local allocation.

- [ ] Model Maven generation lifecycle mode explicitly.

  Replace the `lifecyclePhase != "generate-sources"` inference with explicit handling for compiled-contract
  `generate-sources`, same-module `process-classes`, and deliberately supported direct invocation behavior. Reject unsafe
  or undefined phase bindings with an actionable diagnostic.

  Acceptance criteria:

  - `generate-sources` registers generated sources/resources for ordinary main compilation without late compilation.
  - `process-classes` performs the documented late generated-source compilation and output synchronization.
  - Bindings such as `process-sources`, `compile`, `test-compile`, and `package` either have explicitly specified
    behavior or fail with the two supported alternatives.
  - Direct goal invocation clearly reports missing same-module prerequisites and does not imply that lifecycle phases
    were run.
  - Tests cover both supported modes, representative unsupported phases, and direct invocation.

## P3 — hardening

- [ ] Add contextual diagnostics for API registry provider and linkage failures.

  Give project-side `ApiRegistry` loading the same narrow failure translation as target loading. Handle known
  `ServiceConfigurationError` and `LinkageError` families without catching arbitrary `Throwable`.

  Acceptance criteria:

  - A malformed registry service provider fails with project-classpath context.
  - Missing/incompatible linked types point to dependency and tapik version alignment.
  - Maven reports a normal `MojoExecutionException` chain rather than an unwrapped `Error`.
  - Target-loading diagnostics remain unchanged.
  - Tests cover service construction failure and linkage failure.

- [ ] Preserve comments and string literals during Kotlin import optimization.

  Compute lexical code regions before removing imports or locating the package directive. Only real Kotlin directives
  in code may be removed or rewritten.

  Acceptance criteria:

  - Import-looking lines inside block comments, KDoc, quoted strings, and triple-quoted strings remain byte-for-byte
    present.
  - Package-looking lines inside comments and strings cannot be mistaken for the package directive.
  - Real existing imports are retained/deduplicated and newly inferred imports remain deterministic.
  - Qualified references inside comments and literals remain unchanged.
  - Existing collision and generated-source compilation tests remain green.

## Final verification

- [ ] Run the complete clean stabilization and release checks after all findings are addressed.

  Acceptance criteria:

  - `./mvnw clean verify` passes.
  - The documentation quality/preview build passes its cross-reference, link, snippet, and navigation gates.
  - The documented unsigned external-consumer rehearsal passes from a clean checkout:

    ```shell
    ./mvnw -Prelease -Dgpg.skip=true -Dcentral.skipPublishing=true \
      -Dtapik.release.rehearsal=true clean install
    ```

  - Staged production POMs, JARs, source artifacts, documentation artifacts, generated OpenAPI, and generated Spring
    code are inspected as release outputs.
  - No test/example module is staged for publication.
