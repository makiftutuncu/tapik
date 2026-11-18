# tapik Gradle example

This build reuses the API and application sources from the Maven example. The `contract` project defines endpoints and
generates OpenAPI; the `application` project uses the compiled API to generate documentation, a Spring client, and a
Spring server.

From the tapik repository root, run:

```shell
./plugin-gradle/gradlew -p example-gradle clean check
```

During tapik development, prepare the isolated repository described in the
[development guide](../docs/modules/ROOT/pages/development.adoc), then pass its path as `-PtapikRepository` and the
matching version as `-PtapikVersion`.

For a short walkthrough, see the [Gradle quickstart](../docs/modules/ROOT/pages/gradle-quickstart.adoc). For complete
configuration, see the [Gradle reference](../docs/modules/ROOT/pages/reference/gradle.adoc),
[generation model](../docs/modules/ROOT/pages/generation/index.adoc), and
[development guide](../docs/modules/ROOT/pages/development.adoc).
