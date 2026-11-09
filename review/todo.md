# Gradle and Maven plugin parity TODO

Implement the findings in `review/findings.md` in priority order. For every behavior change, update the relevant file
under `specification/` first, add a failing Kotest or Gradle TestKit specification, implement the smallest coherent
fix, and refactor only with the relevant Maven and Gradle suites green. Keep public packages under `dev.akif.tapik`,
write tapik in lowercase in prose and diagnostics, preserve declaration order, and add KDoc to changed public APIs.

## P1 — Restore lifecycle and compatibility correctness

### Same-project output producer dependencies

- [x] Specify how late same-project classes and resources expose their producing tasks to every runtime/output consumer
  without creating a `classes -> tapikGenerate -> classes` cycle.
- [x] Add a failing TestKit fixture with a custom task that consumes `sourceSets.main.output` or the main runtime
  classpath without being a `Test`, `JavaExec`, or the standard `jar` task.
- [x] Add a failing archive/plugin-task fixture, such as `bootJar`, `shadowJar`, or a focused equivalent, proving that
  late generated classes and resources are built before packaging.
- [x] Replace the task-type/name allowlist in `SameProjectWiring` with produced file collections, a dedicated variant,
  or equivalent Gradle producer metadata that travels with all consumers.
- [x] Verify direct `tapikGenerate` still performs generation only, the standard `jar` remains correct, custom consumers
  receive current output, and stale late classes/resources are removed after configuration changes.

### Project and composite dependency version alignment

- [x] Clarify in `specification/gradle.md` how external modules, project dependencies, and composite substitutions are
  identified and reported during tapik version validation.
- [x] Add a failing multi-project TestKit case where a resolved `dev.akif:tapik-*` project has a version different from
  the applied Gradle plugin.
- [x] Add a failing included-build dependency-substitution case for an incompatible local tapik component.
- [x] Change `tapikDependencyVersions` to inspect resolved component metadata for module and project component IDs,
  retaining classpath, coordinates/component identity, and resolved version in diagnostics.
- [x] Verify compatible project dependencies continue to work and all mismatches fail before worker loading or output
  mutation.

### Environment validation and cached task state

- [x] Update the compatibility and cache-identity specification so project type and supported Gradle, Java, and Kotlin
  versions cannot be bypassed by `UP-TO-DATE` or `FROM-CACHE` outcomes.
- [x] Add a failing TestKit case that generates valid output, changes only to an unsupported Kotlin plugin version, and
  requires the next invocation to fail instead of remaining up-to-date.
- [x] Add equivalent coverage for changing from Kotlin/JVM to an unsupported project type while preserving the output
  tree and task history.
- [x] Make compatibility-affecting environment values declared task inputs or move validation to a hook that executes
  before cached/up-to-date completion.
- [x] Re-run configuration-cache, local build-cache, output restoration, and relocation tests to ensure the new inputs
  are stable and intentional.

### Per-target compiler registry state

- [x] Extend the compiler-attachment specification to define registry-state identity for every JVM target and
  compilation to which the compiler plugin is attached.
- [x] Add a failing compiler-only TestKit project with two JVM targets whose `main` compilations alternately add,
  change, and remove APIs without clean builds.
- [x] Include Kotlin target identity and compilation identity in the registry-index path, with a stable filesystem-safe
  representation.
- [x] Verify each target retains its complete independent registry and that one target's compilation cannot delete or
  rewrite the other's incremental state.

## P2 — Align host semantics and consolidate shared behavior

### API include/exclude semantics

- [x] Reconcile `specification/gradle.md` with `specification/generation.md`: exclusions win when the same API ID is in
  both sets, matching `ApiSelection` and Maven.
- [x] Replace the Gradle overlap-rejection test with a failing parity test that includes and excludes the same API and
  verifies the exclusion is applied.
- [x] Remove the Gradle-only overlap validation from `TapikExecution` and keep blank/unknown/empty-selection validation
  in the host-neutral selection model.
- [x] Update Gradle reference and troubleshooting prose if it currently implies that overlap is invalid.

### Declaration-ordered target invocation

- [x] Reaffirm in the Gradle specification that declaration order governs actual target invocation, not only result
  aggregation and logging.
- [x] Add a failing worker test with delayed/custom target executions that records start order and exposes concurrent or
  non-FIFO invocation.
- [x] Execute the ordered execution snapshots sequentially inside one classloader-isolated work item, or otherwise
  enforce an await boundary that guarantees invocation order.
- [x] Preserve per-target artifact order, first-claim aggregation order, failure framing, atomic output preservation,
  configuration-cache compatibility, and parallel generation across distinct projects.
- [x] Confirm the implementation does not rebuild the target catalog and contract classloader unnecessarily for every
  execution.

### Same-project compile visibility

- [x] Update the same-project classpath specification to define which main compile and runtime dependencies are
  available during contract loading and late generated-source compilation.
- [x] Add a failing TestKit fixture whose contract or generated source requires a dependency declared `compileOnly`.
- [x] Build the generation and late-compilation classpaths from the necessary main compile and runtime configurations,
  deduplicating entries without losing producing-task dependencies or declaration order where observable.
- [x] Run tapik version validation over every classpath actually used for contract loading and late compilation.
- [x] Verify Maven `provided`/compile visibility and Gradle `compileOnly` visibility now behave equivalently.

### Target-owned defaults and cross-host configuration parity

- [x] Specify that optional built-in target defaults belong to generation targets and are not duplicated by host
  adapters.
- [x] Add tests proving an unset Gradle OpenAPI, RestClient, or WebMVC option is absent from the translated
  `TargetConfiguration`, while an explicitly configured value is present and typed correctly.
- [x] Make optional built-in Gradle properties optional task inputs and serialize only values explicitly supplied by the
  user; keep required values and clear Gradle diagnostics where useful.
- [x] Remove duplicated Gradle conventions for OpenAPI format/pretty/component naming and Spring package/suffix values,
  allowing the shared targets to apply and validate their defaults.
- [x] Add an automated Maven/Gradle parity fixture comparing default and explicitly configured OpenAPI, Kotlin source,
  and Spring registration output byte-for-byte, excluding intentional host layout and ownership files.

### Shared service-provider isolation

- [x] Define one host-neutral visibility rule for API-registry and generation-target service resources in
  `specification/generation.md`, while retaining host-specific classpath placement guidance.
- [x] Add Maven and Gradle tests with `ApiRegistry` and `GenerationTargetRegistry` providers deliberately placed on the
  wrong classloader side.
- [x] Add a failing Maven test proving an API registry packaged in a Maven plugin dependency is not treated as a project
  API.
- [x] Extract the child-only service-resource classloader/context session into `common-plugin` without introducing a
  parallel endpoint metadata model or build-tool dependency.
- [x] Use the shared isolation primitive from `ProjectApis`, `MavenTargets`, and `GradleGenerationLoader`, retaining each
  adapter's classpath labels, placement diagnostics, and lifecycle ownership.
- [x] Verify target providers cannot become contract APIs, contract providers cannot become executable targets, context
  classloaders are restored, and disposable classloaders close on every success/failure path.

### Relocatable compiler options and registry local state

- [x] Specify how Gradle compiler-option paths and the registry index participate in task inputs, local state, clean
  builds, cache restoration, and relocation.
- [x] Add a failing TestKit test that compiles a contract, restores or reuses compilation from cache in a relocated
  project, changes one source, and verifies the complete API registry remains present.
- [x] Replace absolute path strings passed as ordinary `SubpluginOption`s with Kotlin Gradle path-aware/internal options
  as appropriate.
- [x] Register the registry index as compilation-local state so Gradle understands its lifecycle without making the
  project directory part of the Kotlin compile cache key.
- [x] Verify source roots remain complete for incremental reconciliation and Kotlin compile cache reuse works across
  equivalent project locations.

### Contract-side provider diagnostics

- [x] Clarify that malformed, duplicate, or linkage-broken target providers on the contract side must retain their
  catalog-loading failure rather than degrading to an ordinary unknown-target diagnostic.
- [x] Add failing Gradle loader tests for malformed service declarations, duplicate target IDs, and linkage failures on
  `tapikContractClasspath` during an unknown-target request.
- [ ] Remove the blanket `runCatching` fallback around the misplaced-target catalog load; treat an absent catalog as
  empty while allowing actual catalog failures to propagate with their cause.
- [ ] Verify the corrected errors still name the execution, target, classpath side, placement correction, and original
  provider failure.

## P3 — Finish cross-platform diagnostics

### Safe generated-path logging

- [ ] Specify that logging failure cannot turn an already committed generation result into a failed task.
- [ ] Extract or reuse a neutral project-relative path display helper with an absolute-path fallback when paths cannot
  be relativized.
- [ ] Apply the same safe behavior to Gradle and Maven without changing deterministic generated-path order.
- [ ] Add platform-conditional coverage for output on a different filesystem root and verify successful output remains
  successful and correctly reported.

## Completion

- [ ] Every finding in `review/findings.md` has a corresponding specification change and regression test.
- [ ] Maven and Gradle use the shared catalog, selection, generation, configuration, artifact, ownership, and
  classloader primitives wherever behavior is host-neutral.
- [ ] Maven and Gradle parity fixtures pass for default/explicit target configuration, API selection, service-provider
  boundaries, generated bytes, and incompatible project dependencies.
- [ ] Same-project output is correct for standard and custom runtime/archive consumers, including non-clean changes and
  stale-output removal.
- [ ] Kotlin compiler registry tests pass for non-clean builds, multiple JVM targets, build-cache restoration, and
  relocation.
- [ ] `./mvnw verify` passes with Java 25.
- [ ] `./scripts/verify-gradle.sh` passes using both Gradle 9.1 and 9.8 with configuration cache enabled.
- [ ] The isolated Maven and Gradle external-consumer rehearsals pass and corresponding generated artifacts remain
  byte-identical.
- [ ] Public KDoc, Gradle reference, troubleshooting, compatibility, development, and release documentation match the
  implemented behavior.
- [ ] `git diff --check` passes for every changed file.
