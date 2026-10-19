# tapik Gradle plugin TODO

Restore Gradle support on top of the rewrite's compiler-generated registries and host-neutral generation targets. The
Maven reactor remains tapik's primary build; `tapik-plugin-gradle` is an independently consumable Gradle plugin built
and tested by its own small Gradle build. Preserve the historical plugin ID `dev.akif.tapik.plugin.gradle`, but define a
new execution-based DSL rather than carrying forward the package-scanning DSL removed by the rewrite.

For every public behavior, update the relevant specification first, add a failing Kotest or Gradle TestKit
specification, implement the smallest coherent behavior, and refactor with both Maven and Gradle suites green. Public
Gradle extension and task APIs require KDoc.

## P0 — Specify the Gradle host contract

- [x] Add a Gradle adapter specification.
  - Define Kotlin/JVM as the initial supported project type; explicitly defer Android and Kotlin Multiplatform.
  - Preserve `dev.akif.tapik.plugin.gradle` as the public plugin ID and publish `dev.akif:tapik-plugin-gradle`.
  - Define a named, declaration-ordered generation-execution DSL with target ID, include/exclude API sets, target
    configuration, and output-root configuration.
  - State that the new DSL is a 0.6 rewrite surface and is not source-compatible with the pre-rewrite scanning DSL.
  - Define explicit compiled-contract and same-project modes instead of inferring lifecycle behavior from resolved
    paths or task names.
  - Keep generation targets host-neutral and discover them through `GenerationTargetRegistry`; do not hard-code built-in
    target behavior in the Gradle adapter.

- [x] Specify Gradle lifecycle and source-visibility rules.
  - In compiled-contract mode, resolve registries from a dedicated contract dependency configuration and generate
    before ordinary main compilation so main source can use generated Kotlin.
  - In same-project mode, depend on compiled main output and generate afterward; ordinary main source cannot refer to
    those late declarations.
  - Define how late Kotlin sources are compiled separately and included in the produced artifact without replacing the
    main compilation's Kotlin module metadata.
  - Define generated resource registration and packaging in both modes; documentation artifacts must not be packaged as
    runtime resources.
  - Make direct task invocation follow the Gradle task graph, without Maven's stale-output direct mode.

- [x] Specify Gradle output ownership and layout.
  - Give one cacheable generation task sole Gradle ownership of each output tree and aggregate all configured target
    executions behind it.
  - Preserve generation-execution declaration order when invoking targets and reporting generated paths.
  - Materialize source, resource, and documentation artifacts below distinct stable subdirectories of the configured
    output root so source-set and packaging wiring is precise.
  - Merge duplicate paths only when both artifacts have the same nonblank sharing key and byte-identical content;
    otherwise fail before changing output.
  - Write the merged result once through `ArtifactWriter` under a stable Gradle task owner so removed executions release
    stale output and client/server endpoint types do not create overlapping Gradle task outputs.

- [x] Define compatibility, dependency, and diagnostic policy.
  - Record the initial supported intersection of Java 25, Kotlin 2.4.10, and Gradle 9.1–9.5; test both Gradle endpoints.
  - Define separate resolvable, non-consumable contract and generation-target configurations with built-in target
    defaults and support for custom target artifacts.
  - Require every resolved `dev.akif:tapik-*` dependency used for contracts or generation to match the Gradle plugin
    version before registry or target loading.
  - Diagnose unknown targets, targets misplaced on the contract classpath, registry service failures, linkage failures,
    missing compiled registries, and unsupported project types with Gradle-specific guidance.

## P1 — Establish the plugin build and compiler integration

- [x] Create the standalone `plugin-gradle` build.
  - Add a Gradle wrapper and a focused `settings.gradle.kts`/`build.gradle.kts` using `java-gradle-plugin`, Kotlin/JVM,
    Maven publishing, Kotest, and Gradle TestKit.
  - Read or receive the tapik version from one authoritative release value and fail when it differs from the root Maven
    version.
  - Resolve repository-built tapik artifacts from an isolated Maven repository prepared by the Maven reactor; tests and
    CI must not accidentally consume stale artifacts from the user's ordinary `mavenLocal()`.
  - Register the historical plugin ID, implementation class, display name, description, and generated plugin marker.
  - Add the production artifact to the tapik BOM without adding test fixtures or examples to the publication set.

- [x] Attach `tapik-plugin-compiler` through Kotlin's supported Gradle API.
  - Implement `KotlinCompilerPluginSupportPlugin` with compiler plugin ID `dev.akif.tapik` and coordinates matching the
    applied Gradle plugin version.
  - Apply it only to supported Kotlin/JVM compilations and preserve registry generation for contract modules without
    introducing compiler options or a second metadata model.
  - Add TestKit coverage proving a Gradle-compiled contract jar contains the generated `ApiRegistry` service provider
    and that a compilation without APIs leaves no compiler-owned registry behind.
  - Cover non-clean source addition, change, and removal so incremental registry synchronization behaves the same under
    Gradle as it does under Maven.

- [x] Introduce the public Gradle extension and lazy configuration model.
  - Model values with Gradle `Property`, `SetProperty`, `MapProperty`, `DirectoryProperty`, and managed nested beans;
    never capture `Project`, configurations, classloaders, or resolved tapik values in task actions.
  - Preserve generation-execution declaration order when snapshotting the named container into task inputs.
  - Validate blank and duplicate execution names, missing targets, contradictory API selection, and incomplete mode
    configuration before task execution where Gradle permits.
  - Add unit tests for conventions, overrides, Groovy/Kotlin DSL usability, and lazy task registration.

## P2 — Deliver compiled-contract generation

- [x] Implement isolated registry and target loading for Gradle.
  - Run generation through the Worker API with classloader isolation so contract and target dependencies do not leak
    into the Gradle daemon or task state.
  - Load targets only from the generation-target classpath and APIs from a child contract/runtime classloader with the
    correct thread context classloader.
  - Reuse `ApiCatalog`, `GenerationTargetCatalog`, `GenerationEngine`, `ApiSelection`, and `TargetConfiguration` rather
    than depending on Maven adapter internals.
  - Translate Gradle scalar/list/object configuration values into the complete host-neutral configuration tree while
    preserving list and map declaration order.
  - Restore all context and close disposable classloaders on success and failure.

- [x] Implement the aggregated cacheable generation task.
  - Declare contract and target artifacts as normalized `@Classpath` inputs, execution definitions and plugin version as
    value inputs, and the generated root as the task output.
  - Generate every configured execution before mutating destination output; merge artifacts according to the specified
    sharing rules and reject conflicts atomically.
  - Route artifacts into stable source/resource/documentation subdirectories and write them once with `ArtifactWriter`.
  - Log generated paths relative to the project in deterministic artifact order.
  - Add focused tests for multiple targets, shared Spring endpoint types, exclusive collisions, stale cleanup, removed
    executions, failure preservation, API selection, and nested target configuration.

- [x] Wire compiled-contract outputs into Kotlin/JVM builds.
  - Make generated Kotlin a lazy source-directory provider so compilation receives the task dependency automatically.
  - Make generated resources inputs of `processResources` without including generated source or documentation files.
  - Keep documentation output available as a task artifact without adding it to runtime jars.
  - Add a multi-project TestKit fixture where an application consumes a separately compiled contract, generates Spring
    client and server sources, implements the generated server API in main source, and compiles successfully.
  - Verify OpenAPI generation and a custom service-loaded target from the same contract fixture.

## P3 — Add same-project generation

- [ ] Support same-project documentation and resource targets.
  - Depend lazily on the supported main Kotlin compilation output and its runtime classpath.
  - Generate only after the compiler-produced registry exists, including when `tapikGenerate` is invoked directly.
  - Package generated resources at the correct point even though ordinary resource processing may already have run.
  - Add TestKit coverage for same-project OpenAPI, generated resources, API filtering, non-clean changes, and direct task
    invocation from a clean checkout.

- [ ] Compile and package late same-project Kotlin sources.
  - Create a separate generated Kotlin compilation/task with main output and runtime dependencies on its classpath.
  - Use a distinct stable module name and output directory so main Kotlin metadata is preserved.
  - Include compiled late classes and generated resources in the project's normal jar without recompiling user source.
  - Remove stale generated classes, nested classes, descriptors, resources, and module metadata after configuration or
    contract changes while preserving application-owned output.
  - Fail clearly when ordinary main source tries to rely on same-project late declarations, directing users to a
    compiled contract project.
  - Add source-generation and runtime TestKit fixtures for OpenAPI, Spring RestClient, Spring WebMVC, and shared endpoint
    types.

## P4 — Make the plugin production-grade

- [ ] Prove incremental, build-cache, and configuration-cache correctness.
  - Run each functional fixture twice with `--configuration-cache` and require reuse without reported problems.
  - Verify `UP-TO-DATE` behavior, local build-cache restoration after deleting outputs, and relocation to another
    project directory.
  - Verify changes to contract bytecode, runtime dependencies, target artifacts, API selection, nested configuration,
    execution order, plugin version, and output root invalidate exactly the necessary work.
  - Verify no two tapik tasks declare overlapping outputs and parallel execution cannot corrupt generated state.
  - Exercise Gradle 9.1 and 9.5 with Java 25 and Kotlin 2.4.10.

- [ ] Complete failure and compatibility coverage.
  - Test duplicate API IDs, empty selections, missing registries, incompatible tapik versions, unknown and duplicate
    targets, misplaced custom targets, malformed configuration, service-provider failures, linkage failures, and target
    exceptions.
  - Ensure diagnostics name the execution, target, affected classpath side, and corrective dependency placement where
    applicable.
  - Ensure failures leave the last successful generated result and Gradle task metadata usable on the next run.

- [ ] Add Gradle examples and public documentation.
  - Add runnable Gradle contract/application fixtures equivalent to the Maven examples without duplicating contract
    definitions into another metadata model.
  - Document plugin application, compiler activation, dependency configurations, both generation modes, named
    executions, target options, custom targets, generated output, task names, and troubleshooting.
  - Update README, navigation, migration, compatibility, development, and release documentation so Gradle is a supported
    host rather than a future or unavailable integration.
  - Use tested source snippets for Kotlin and Groovy DSL examples and keep the project name lowercase `tapik`.

- [ ] Integrate verification and publication.
  - Add a repeatable repository command that installs Maven artifacts into an isolated repository and runs the complete
    Gradle plugin check without relying on global state.
  - Add CI jobs for the Gradle support matrix, TestKit functional tests, configuration cache, build cache, and Gradle
    example builds alongside `./mvnw clean verify`.
  - Publish the implementation artifact, sources, API documentation, and plugin marker with the same version and release
    metadata as the Maven reactor.
  - Define and rehearse release ordering for Maven Central dependencies and the Gradle Plugin Portal; no consumer may
    observe a plugin version whose tapik runtime artifacts are unavailable.
  - Extend the isolated external-consumer rehearsal to resolve the published plugin marker and dependencies without the
    source checkout or ordinary local Maven repository.

## Completion

- [ ] The Gradle adapter specification, public KDoc, user guide, and implementation describe the same DSL and lifecycle.
- [ ] `./mvnw clean verify` passes.
- [ ] The standalone Gradle plugin unit and TestKit suites pass on Gradle 9.1 and 9.5 with Java 25.
- [ ] Configuration-cache, build-cache, non-clean, relocation, and parallel-execution checks pass.
- [ ] Maven and Gradle external-consumer release rehearsals pass from isolated repositories.
- [ ] The documentation build and desktop/mobile smoke tests pass.
- [ ] `git diff --check` passes for every changed file.
