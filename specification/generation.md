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

`common-plugin` provides target-neutral Kotlin source helpers for reading compiled type arguments and Tapik tuples,
allocating and sanitizing Kotlin declaration names, and escaping Kotlin string literals. Source-generating targets use
these shared rules rather than maintaining target-specific copies; target-specific model and emission decisions remain
inside each target.

Generated artifact paths are relative and normalized. A generation result cannot contain the same path twice.
Configuration lists and maps and generation-result artifact lists are structural snapshots: later caller mutation and
mutation attempts through exposed collection values cannot change the validated configuration or result.

## API registries

A compiled contract contributes an `ApiRegistry` containing all concrete Kotlin classes and objects extending `Api`.
Registration is automatic by default; generation-time include and exclude filters may narrow the resulting catalog
later. Registry-provider order and the order inside an individual registry are not generation contracts. A catalog
orders APIs canonically by ID across all artifacts and rejects duplicate API IDs. Endpoint order remains property
declaration order within each API.

Registry providers are generated during Kotlin compilation and exposed through a shared runtime discovery mechanism.
Maven, Gradle, and command-line adapters consume the same providers.

For Kotlin/JVM, `plugin-compiler` inspects resolved compiler IR and selects every concrete public named class or object
whose type extends `Api`. Objects are referenced directly. API classes may be top-level or statically nested, must not
be `inner`, must not declare type parameters, and must expose a public constructor that lowers to JVM `()V`. A concrete
subclass may close type parameters inherited from an abstract API base class. Classes are instantiated once per
generated registry. The provider retains these instances and is exposed through the standard service-provider
mechanism; consumers do not rely on their internal order. Abstract API base classes are ignored. Unsupported API
visibility, generic shape, or construction fails compilation with a targeted diagnostic. The plugin does not execute
endpoint expressions, discover APIs reflectively, or create a second contract representation. Compilations without a
concrete API type do not publish a registry.

Every delegated endpoint property reachable from a registered API, including properties declared by API base classes,
must be public. Private, protected, and internal endpoint properties fail contract compilation because generated
targets access them from another package and potentially another module. Kotlin's public-signature visibility rules
likewise reject inaccessible model types exposed by a public endpoint property. Metadata inspection repeats the
endpoint-property check so contracts produced without the current compiler plugin fail before target source emission.

Each compiler output directory has one deterministic compiler-owned registry class and one corresponding entry in the
standard `ApiRegistry` service descriptor. Every compilation synchronizes those artifacts, including non-clean
recompilations: changed APIs replace the generated class, while a compilation with no valid concrete API removes the
generated class and its service entry. Other provider entries in the shared service descriptor are preserved, so a
manually supplied registry can coexist with the compiler-owned registry.

Incremental Kotlin compilation is rejected initially because a partial IR view cannot safely produce a complete
registry. Supporting incremental aggregation remains required future work.

### Maven activation

`tapik-plugin-maven` is the single user-facing Tapik build-plugin artifact. A Maven project adds that artifact as a
dependency of Kotlin's Maven plugin and enables the `tapik` compiler plugin, then uses the same artifact for one or
more `generate` executions. Its Maven-specific compiler adapter only activates the host-neutral compiler plugin; it
does not own discovery or generation behavior and does not introduce compiler options.

The Maven adapter loads API registries from the project's compile/runtime classpath and generation-target registries
from the Maven plugin realm. A custom target artifact must therefore be declared in `tapik-plugin-maven`'s
`<plugin><dependencies>`; adding it as an ordinary project dependency does not make it executable. An unknown-target
diagnostic lists targets visible in the plugin realm, and a target found only on the project classpath is diagnosed as
a misplaced plugin dependency. Generation-target service-provider or linkage failures identify the classpath side that
failed and point to target placement and Tapik version alignment.

Before generation, the Maven adapter compares the Maven plugin version with every resolved `dev.akif:tapik-*` project
dependency. An empty project-side set is tolerated for hosts supplying APIs through the plugin realm, but every
discovered project Tapik version must equal the plugin version. Version skew fails before registry or target loading.

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

Typed source targets derive a neutral source type that keeps emitted spelling separate from the expanded classifier
identity. Type aliases are emitted by their source names while target semantics continue to use their expanded types;
containing types and generic projections are rendered without flattening their structure. Flexible platform types,
definitely-non-null types, and unresolved type parameters are rejected with their contract location until Tapik can
reproduce them without losing information.

## OpenAPI target

The OpenAPI target accepts all APIs in one execution and generates one document artifact per API. Its host-neutral
configuration requires a document version and supports pretty JSON, component naming, and a relative output template.
The `{api}` placeholder expands to the API ID, and the default template is `{api}.openapi.json`.

## Maven adapter

`dev.akif:tapik-plugin-maven` is a thin host adapter exposing one `generate` goal. Each Maven execution selects
one target and supplies its own target configuration. The goal runs in `process-classes` by default and resolves the
project compile and runtime classpath so same-module documentation targets can consume compiled API objects.
Compile-classpath entries retain Maven's order and precede runtime-only entries. Paths are normalized and duplicate
entries are passed to generation only once.

The adapter loads every compiler-generated API registry visible to the project classpath. Conflicting API IDs fail
generation.

`outputDirectory` defaults to `${project.build.directory}/generated/tapik`. Target artifact paths are resolved below
that directory and written as UTF-8. Each Maven execution owns the paths it generated there. A later invocation of the
same execution removes its previously owned paths that are absent from the new result, while paths owned by other
executions and unowned files remain untouched. Two executions cannot own the same path. Ownership is updated only after
target generation succeeds, so a failed generation leaves the last successful result available. Maven configuration is
translated into the host-neutral configuration model before target selection; Maven types do not cross into
`common-plugin` or target modules.

When an execution returns `SOURCE` artifacts, the adapter adds that execution's output directory as a project compile
source root. Source targets may run in `generate-sources` when their APIs come from compiled contract dependencies.
Targets consuming APIs declared in the current project continue to run after those contracts are compiled until a
compiler-integrated source-generation path is implemented.

When an execution returns `RESOURCE` artifacts, the Maven adapter adds exactly those relative paths beneath the
execution output directory as project resources. Source and documentation artifacts in the same directory are not
copied into runtime classes. Generated resources participate in the ordinary Maven resource lifecycle and are packaged
with the consumer artifact.
