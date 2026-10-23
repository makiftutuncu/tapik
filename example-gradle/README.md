# tapik Gradle example

This multi-project build uses the same library contract and application Kotlin sources as the Maven example. The
`contract` project generates OpenAPI after compiling its own APIs, while `application` generates documentation and
Spring sources from the compiled contract project.

From a released checkout, run the build with the Gradle wrapper available in `plugin-gradle`:

```shell
../plugin-gradle/gradlew -p . check
```

During tapik development, first prepare the isolated repository described in the development guide, then pass it as
`-PtapikRepository=/absolute/path/to/repository` together with the matching `-PtapikVersion`.
