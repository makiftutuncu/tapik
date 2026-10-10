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
- one explicit generation mode;
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

    execution("publicOpenApi") {
        sameProject()
        includeApis("Catalog")

        target("openapi") {
            value("version", "1.0.0")
            value("format", "yaml")
        }
    }
}
```

`sameProject()` and `compiledContract()` select the corresponding explicit mode and are mutually exclusive.
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

Every execution declares exactly one of two modes:

- **Compiled contract** reads APIs from artifacts on the dedicated contract dependency configuration. It is intended
  for consumers of a separately compiled contract and can make generated declarations available to ordinary main
  source compilation.
- **Same project** reads APIs from the current project's compiled Kotlin/JVM main output. It is intended for targets
  that must interpret APIs declared by that project; generated declarations are necessarily later than ordinary main
  source compilation.

The adapter never infers mode from resolved files, populated dependency configurations, output directories, task
names, requested tasks, or whether a registry happens to be visible. Explicit mode is part of the execution's task
input and diagnostic context. A single project may contain executions in either mode; the adapter applies the
mode-specific classpath and lifecycle rules to each one.

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
