# Gradle adapter specification

## Scope and identity

The Gradle adapter is a host for the same compiler-generated API registries, host-neutral generation targets,
selection rules, configuration tree, and generated artifacts used by other tapik hosts. It is published as
`dev.akif:tapik-plugin-gradle` and preserves the public Gradle plugin ID `dev.akif.tapik.plugin.gradle`.

The initial supported project type is Kotlin/JVM. A supported project applies Kotlin's JVM plugin and uses Java 25,
Kotlin 2.4.10, and a supported Gradle version. The tapik plugin does not turn a Java-only, Android, or Kotlin
Multiplatform project into a Kotlin/JVM project. Android and Kotlin Multiplatform integration are explicitly deferred;
their compilations, variants, source sets, and packaging rules are not part of the initial adapter contract.

## Configuration model

The plugin contributes one `tapik` extension. Its output root is backed by a Gradle directory property, shared by all
tapik generation in the project, and defaults below the project's build directory at `generated/tapik`. Calling
`outputRoot` replaces that convention with a directory or directory provider. Calling `execution(name) { ... }`
registers an entry lazily in a managed named container. Each entry describes one invocation of one generation target
and has:

- a unique, nonblank Gradle name used for diagnostics and stable execution identity;
- one nonblank target ID;
- exact API-ID include and exclude sets; and
- a target-owned configuration tree containing scalar, list, and object values.

Execution names do not select targets. A project may declare several differently named executions for the same target
with different API selections or target configuration. Their declaration order is observable: the adapter snapshots,
invokes, and reports executions in that order. Reconfiguring a named entry does not move it. `includeApis` and
`excludeApis` append exact IDs to their respective managed sets. API selection retains the host-neutral rules: an empty
include set selects every discovered API, exclusions are applied afterward, unknown IDs and an empty final selection
fail, and selected APIs retain catalog order.

The Kotlin DSL keeps the direct, block-oriented style of tapik's earlier Gradle integration while replacing its
generator-specific blocks with generic executions and targets:

```kotlin
tapik {
    outputRoot(layout.buildDirectory.dir("generated/contracts"))
    sameProject()

    execution("publicOpenApi") {
        includeApis("Catalog")

        target("openapi") {
            value("version", "1.0.0")
            value("format", "yaml")
        }
    }
}
```

`sameProject()` and `compiledContract()` select the corresponding explicit project mode and are mutually exclusive.
`target(id) { ... }` both selects the target and configures it, mirroring the earlier integration's useful property that
entering a generator block enabled that generator. It is generic rather than tied to built-in target names and may be
called exactly once per execution. `value` adds a scalar configuration entry; corresponding list and object builders
preserve the complete host-neutral tree and declaration order. The DSL methods write only to Gradle `Property`,
`SetProperty`, `MapProperty`, `DirectoryProperty`, and managed nested values. They do not resolve providers during
configuration. Equivalent Groovy DSL uses the same methods and behavior rather than a separate compatibility API.

The extension-level output root is the only user-configured filesystem root. A target still returns normalized relative
artifact paths and never writes files. The Gradle adapter owns the layout and materialization beneath that root; target
configuration cannot redirect an artifact outside it.

## Explicit generation modes

Every project using generation declares exactly one of two modes:

- **Compiled contract** reads APIs from artifacts on the dedicated contract dependency configuration. It is intended
  for consumers of a separately compiled contract and can make generated declarations available to ordinary main
  source compilation.
- **Same project** reads APIs from the current project's compiled Kotlin/JVM main output. It is intended for targets
  that must interpret APIs declared by that project; generated declarations are necessarily later than ordinary main
  source compilation.

The adapter never infers mode from resolved files, populated dependency configurations, output directories, task
names, requested tasks, or whether a registry happens to be visible. Explicit mode is part of the generation task's
input and every execution's diagnostic context. All executions in one project use that mode. Mixing modes would require
the same aggregated task to run both before and after main compilation, so it is not a supported configuration.

## Lifecycle and source visibility

The plugin registers one `tapikGenerate` task for the configured executions. Mode determines its relationship with the
Kotlin/JVM main compilation; task names, requested command-line tasks, and previously compiled files do not alter that
relationship.

| Mode | Registry input | Task relationship | Main-source visibility |
| --- | --- | --- | --- |
| Compiled contract | Dedicated contract dependency configuration | `compileKotlin` and `processResources` consume `tapikGenerate` outputs | Main source can use generated Kotlin |
| Same project | Compiled main output and runtime classpath | `tapikGenerate` depends on the main `classes` lifecycle | Main source cannot use generated Kotlin |

### Compiled-contract mode

The dedicated contract configuration resolves already compiled artifacts and their runtime dependencies. Project
dependencies on contract modules retain Gradle's normal producing-task dependencies, so generation never scans or
executes uncompiled source. `tapikGenerate` loads compiler-produced registries only from that resolved contract
classpath.

The generated Kotlin directory is registered lazily as a main Kotlin source directory backed by `tapikGenerate`.
Consequently `compileKotlin` runs generation first and compiles generated declarations together with application main
source. Application source may implement or invoke those declarations. The generated resource directory is likewise a
lazy input to `processResources`, so resources are copied into the ordinary main resource output and included in the
normal runtime artifact. These relationships come from providers and task dependencies rather than eager task
realization or string task-name ordering.

Generated documentation remains an output of `tapikGenerate` only. It is neither a Kotlin source directory nor an
input to `processResources`, the runtime classpath, or `jar` packaging unless the consuming build deliberately adds a
separate publication step.

### Same-project mode

`tapikGenerate` depends on the Kotlin/JVM main `classes` lifecycle and reads the resulting main output together with the
main runtime classpath. This guarantees that the compiler-generated `ApiRegistry` service provider, Kotlin contract
classes, Java main classes, and processed application resources exist before API loading. Running `tapikGenerate`
directly from a clean checkout therefore builds those prerequisites through the ordinary Gradle task graph.

The adapter does not add same-project generated Kotlin to the ordinary main Kotlin sources. Doing so would require
`compileKotlin` to depend on generation while generation already depends on main compilation. Main source that needs a
generated declaration must move the contract to a separately compiled project and use compiled-contract mode.

When same-project targets emit Kotlin, a separate Kotlin compilation consumes only the generated source and compiles
against the completed main output and its runtime dependencies. It has a stable module name distinct from the main
compilation and writes to a separate classes directory, preserving the main compilation's Kotlin module metadata. The
normal `jar` includes both class directories. Runtime consumers of the project are wired to the late classes without
making them inputs to ordinary main compilation.

Same-project generated resources are materialized in their own resource directory after `tapikGenerate`. The normal
`jar` includes that directory, and runtime-consuming tasks receive it alongside the late classes. The adapter does not
rerun or overwrite `processResources`. Documentation remains outside all runtime and packaging inputs in this mode as
well.

The late Kotlin compilation and packaging tasks depend on `tapikGenerate`; `tapikGenerate` does not finalize or invoke
them when requested alone. A direct `tapikGenerate` command means exactly generation plus its declared prerequisites.
There is no Gradle equivalent of Maven's special direct mode, no reuse of unchecked stale main output, and no behavior
branch based on how the task entered the graph.

## Host-neutral target boundary

Generation targets remain independent of Gradle. The adapter discovers `GenerationTargetRegistry` providers from its
generation-target dependency configuration and builds a `GenerationTargetCatalog`; it does not switch on known target
IDs or call built-in target implementations directly. Built-in and custom targets therefore cross the same service
provider, `GenerationRequest`, `ApiSelection`, `TargetConfiguration`, `GenerationEngine`, `GenerationResult`, and
`GeneratedArtifact` boundaries defined by the generation specification.

The execution's target ID selects exactly one discovered target. Target-specific configuration is translated into the
complete host-neutral configuration tree and validated by that target. Adding another target must not require a new
Gradle extension type, task implementation branch, or dependency from a target back to the Gradle API. The adapter
does not depend on or reuse Maven adapter types.

## Rewrite compatibility

Keeping the historical plugin ID is an application and discovery convenience, not DSL compatibility. The 0.6 Gradle
adapter is a new rewrite surface and is intentionally not source-compatible with the pre-rewrite scanning DSL.
Configuration based on package scanning or generator-specific blocks such as `basePackage`, `generatedPackageName`,
`endpointsSuffix`, `springRestClient`, `springWebMvc`, or `markdownDocumentation` is not recognized or emulated.

API discovery comes only from compiler-generated registries. Target selection comes only from named executions and
service-loaded target registries. Migration requires replacing old scanning configuration with ordinary tapik API
definitions, compiler-plugin activation, explicit generation mode, contract or target dependencies, and named target
executions. From its 0.6 baseline, this public Gradle DSL follows tapik's documented pre-1.0 Semantic Versioning policy.
