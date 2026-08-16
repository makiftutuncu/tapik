# Generation specification

## Boundaries

Generation is independent of Maven, Gradle, command-line parsing, and a particular target. A target receives all APIs
selected for one execution, a host-neutral configuration tree, and returns generated artifacts without writing them.
Host adapters select lifecycle phases or tasks, translate their configuration syntax, declare inputs and outputs,
and materialize the returned artifacts.

Every target has a unique non-blank ID. Selecting an unknown target, registering duplicate target IDs, supplying no
APIs, or supplying APIs with duplicate IDs fails before target execution. Target-specific configuration is parsed and
validated by the target so every host observes the same behavior.

Generated artifact paths are relative and normalized. A generation result cannot contain the same path twice.

## API registries

A compiled contract contributes an `ApiRegistry` containing all concrete Kotlin `object` declarations extending
`Api`. Registration is automatic by default; generation-time include and exclude filters may narrow the resulting
catalog later. Generated registries retain source declaration order internally, while a catalog orders APIs by ID to
remain deterministic across multiple artifacts and rejects duplicate API IDs.

Registry providers are generated during Kotlin compilation and exposed through a shared runtime discovery mechanism.
Maven, Gradle, and command-line adapters consume the same providers. Explicit API class configuration remains a
temporary fallback and does not form part of target implementations.

## OpenAPI target

The OpenAPI target accepts all APIs in one execution and generates one document artifact per API. Its host-neutral
configuration requires a document version and supports pretty JSON, component naming, and a relative output template.
The `{api}` placeholder expands to the API ID, and the default template is `{api}.openapi.json`.
