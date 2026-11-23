# tapik Gradle example

This build reuses the API and application sources from the Maven example. Its API project is named `contract`; it
defines endpoints and generates OpenAPI. The `application` project uses the compiled API to generate documentation, a
Spring client, and a Spring server.

From the tapik repository root, run:

```shell
./plugin-gradle/gradlew -p example-gradle clean check
```

During tapik development, prepare the isolated repository described in the
[development guide](../docs/modules/ROOT/pages/development.adoc), then pass its path as `-PtapikRepository` and the
matching version as `-PtapikVersion`.

For a short walkthrough, see the [Gradle quickstart](https://tapik.akif.dev/docs/gradle-quickstart.html). For complete
configuration, see the [Gradle reference](https://tapik.akif.dev/docs/reference/gradle.html) and
[generation model](https://tapik.akif.dev/docs/generation/index.html).
