```
          Type-safe APIs in Kotlin

   /$$                         /$$ /$$
  | $$                        |__/| $$
 /$$$$$$    /$$$$$$   /$$$$$$  /$$| $$   /$$
|_  $$_/   |____  $$ /$$__  $$| $$| $$  /$$/
  | $$      /$$$$$$$| $$  \ $$| $$| $$$$$$/
  | $$ /$$ /$$__  $$| $$  | $$| $$| $$_  $$
  |  $$$$/|  $$$$$$$| $$$$$$$/| $$| $$ \  $$
   \___/   \_______/| $$____/ |__/|__/  \__/
                    | $$
                    | $$
                    |__/
```

Define HTTP endpoints as ordinary Kotlin values with tapik. Generate documentation, clients, and server interfaces from
those endpoints. Their structure stays in the Kotlin types, and generation reports when an integration does not support
an endpoint feature.

## Start here

- [Read the documentation](https://tapik.akif.dev/docs/) for the quickstart, DSL guide, integrations, and build-tool
  references.
- [Open the Kotlin API reference](https://tapik.akif.dev/docs/api/).
- See an example project in [Maven](example-application/README.md) or [Gradle](example-gradle/README.md).

## An endpoint in Kotlin

```kotlin
import dev.akif.tapik.*
import dev.akif.tapik.format.jackson.jsonBody

data class Book(val isbn: String, val title: String)

object Books : Api() {
    val list by get(root / "books", summary = "List books")
        .output(Status.Ok with jsonBody<List<Book>>())
}
```

Endpoints are ordinary values. Their inferred types retain paths, inputs, outputs, formats, presence, and declaration
order, so clients, servers, and documentation can use the same endpoints.

## Supported integrations

- **Build hosts:** Maven and Gradle
- **Documentation targets:** OpenAPI
- **Client targets:** Spring RestClient
- **Server targets:** Spring WebMVC
- **Format integrations:** Kotlin serialization and Jackson 3

## Requirements

- Java 25
- Kotlin 2.4
- Maven 3.9+ or Gradle 9.1–9.8

The [quickstart](https://tapik.akif.dev/docs/getting-started.html) shows the shortest working Maven path. For Gradle,
follow the [Gradle guide](https://tapik.akif.dev/docs/reference/gradle.html).

## Build tapik for contributions

```shell
./mvnw verify
```

The repository contains the [behavioral specifications](specification/README.md),
[source-backed documentation](docs/modules/ROOT/pages/index.adoc), and runnable examples. Start with the
[development guide](https://tapik.akif.dev/docs/development.html) before changing public behavior.

## License

tapik is available under the [MIT License](LICENSE).
