# tapik Gradle example

This build reuses the API and application sources from the Maven example. The `contract` project defines endpoints and
generates OpenAPI; the `application` project uses the compiled API to generate documentation, a Spring client, and a
Spring server.

From this directory, run the example with the wrapper in `plugin-gradle`:

```shell
../plugin-gradle/gradlew -p . check
```

During tapik development, prepare the isolated repository described in the
[development guide](../docs/modules/ROOT/pages/development.adoc), then pass its path as `-PtapikRepository` and the
matching version as `-PtapikVersion`.

For the shared guides, see the [Gradle reference](../docs/modules/ROOT/pages/reference/gradle.adoc),
[generation model](../docs/modules/ROOT/pages/generation/index.adoc), and
[development guide](../docs/modules/ROOT/pages/development.adoc).
