# tapik specification

These documents describe tapik's intended behavior and design. Update the relevant specification before changing
public behavior, then use tests to check the implementation against it.

- [Product](product.md): purpose, users, promises, targets, and non-goals
- [DSL](dsl.md): agreed authoring experience and type-level model
- [HTTP media types](media-types.md): media type syntax, identity, common values, and target behavior
- [OpenAPI](openapi.md): OpenAPI interpretation, naming, and rendering behavior
- [Spring integrations](spring.md): RestClient and WebMVC generation and runtime behavior
- [Generation](generation.md): host-neutral target execution and API registries
- [Gradle](gradle.md): Gradle host identity, project scope, and execution DSL
- [Validation](validation.md): compile-time, construction-time, and target compatibility failures
- [Structural snapshots](snapshots.md): collection and byte-array ownership guarantees
- [Development](development.md): build requirements and the specification/test-driven workflow
- [Documentation](documentation.md): website structure, narrative guides, API reference, and publishing model
