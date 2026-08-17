# Generation specification

## Boundaries

Generation is independent of Maven, Gradle, command-line parsing, and a particular target. A target receives all APIs
selected for one execution, a host-neutral configuration tree, and returns generated artifacts without writing them.
Host adapters select lifecycle phases or tasks, translate their configuration syntax, declare inputs and outputs,
and materialize the returned artifacts.

Every target has a unique non-blank ID. Selecting an unknown target, registering duplicate target IDs, supplying no
APIs, or supplying APIs with duplicate IDs fails before target execution. Target-specific configuration is parsed and
validated by the target so every host observes the same behavior.

Generation targets are contributed through host-neutral target registries discovered with Java's service-provider
mechanism. Build-tool adapters and future command-line applications load the same registries instead of hard-coding
known targets. Target IDs must be unique across all loaded registries and are ordered deterministically.

Generated artifact paths are relative and normalized. A generation result cannot contain the same path twice.

## API registries

A compiled contract contributes an `ApiRegistry` containing all concrete Kotlin `object` declarations extending
`Api`. Registration is automatic by default; generation-time include and exclude filters may narrow the resulting
catalog later. Generated registries retain source declaration order internally, while a catalog orders APIs by ID to
remain deterministic across multiple artifacts and rejects duplicate API IDs.

Registry providers are generated during Kotlin compilation and exposed through a shared runtime discovery mechanism.
Maven, Gradle, and command-line adapters consume the same providers.

For Kotlin/JVM, `plugin-compiler` inspects resolved compiler IR and selects every concrete public named class or object
whose type extends `Api`. Objects are referenced directly; classes must expose a public no-argument constructor and
are instantiated once per generated registry. The provider retains these instances in source declaration order and
is exposed through the standard service-provider mechanism. Abstract API base classes are ignored. Unsupported API
visibility or construction fails compilation. The plugin does not execute endpoint expressions, discover APIs
reflectively, or create a second contract representation. Compilations without a concrete API type do not publish a
registry.

Incremental Kotlin compilation is rejected initially because a partial IR view cannot safely produce a complete
registry. Supporting incremental aggregation remains required future work.

### Maven activation

`tapik-plugin-maven` is the single user-facing Tapik build-plugin artifact. A Maven project adds that artifact as a
dependency of Kotlin's Maven plugin and enables the `tapik` compiler plugin, then uses the same artifact for one or
more `generate` executions. Its Maven-specific compiler adapter only activates the host-neutral compiler plugin; it
does not own discovery or generation behavior and does not introduce compiler options.

Generated registries travel with compiled contract artifacts. A consuming Maven project discovers registries from
its own output and its compile classpath, combines every API deterministically, and applies the configured target to
all of them by default. Consuming a contract dependency therefore behaves like compiling its API definitions in the
current project.

## Compiled contract types

Targets that generate typed source use a compiled view pairing each runtime `Api` and endpoint value with the
endpoint property's actual Kotlin return type. The type is read from Kotlin class metadata, including classifiers,
generic projections, nullability, type-alias abbreviations, outer types, flexible upper bounds, and definitely
non-null types. Tapik does not infer model types from schemas, execute reflective endpoint discovery, or serialize a
second endpoint metadata model.

Runtime endpoint order remains authoritative. Declared properties from the concrete API and its API base classes are
matched to those endpoint values by their delegated property names. Missing, incompatible, or unreadable Kotlin
metadata fails generation with the API and endpoint location in the diagnostic.

## OpenAPI target

The OpenAPI target accepts all APIs in one execution and generates one document artifact per API. Its host-neutral
configuration requires a document version and supports pretty JSON, component naming, and a relative output template.
The `{api}` placeholder expands to the API ID, and the default template is `{api}.openapi.json`.

## Maven adapter

`dev.akif:tapik-plugin-maven` is a thin host adapter exposing one `generate` goal. Each Maven execution selects
one target and supplies its own target configuration. The goal runs in `process-classes` by default and resolves the
project compile and runtime classpath so same-module documentation targets can consume compiled API objects.

The adapter loads every compiler-generated API registry visible to the project classpath. Conflicting API IDs fail
generation.

`outputDirectory` defaults to `${project.build.directory}/generated/tapik`. Target artifact paths are resolved below
that directory and written as UTF-8. Maven configuration is translated into the host-neutral configuration model
before target selection; Maven types do not cross into `plugin-core` or target modules.
