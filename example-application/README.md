# Runnable Maven library example

This application demonstrates the recommended compiled-contract workflow using two flat Maven modules:

```text
example-contract                         example-application
Library APIs and models, Jackson    -->  depends on the compiled contract
Tapik compiler registry                  generates OpenAPI, RestClient, and WebMVC
same-module OpenAPI generation            implements generated servers and BooksClient
                                          runs with Spring Boot
```

The contract module enables Tapik's compiler plugin and selects `tapik-format-jackson`, matching a typical Spring Boot
application without requiring serializable annotations on its models. Its OpenAPI execution uses the plugin's default
`process-classes` phase and therefore demonstrates same-module generation. The application does not enable the Tapik
compiler plugin because it declares no APIs of its own. Its target executions run during
`generate-sources`, when the compiled contract dependency is already available and generated client/server types can
still be used by ordinary application source.

Build and test the complete example from the repository root:

```shell
./mvnw -pl example-application -am clean verify
```

The build verifies these generated artifacts:

- `example-application/target/generated/tapik/Books.openapi.yml`
- `example-application/target/generated-sources/tapik-restclient/com/example/library/generated/BooksClient.kt`
- `example-application/target/generated-sources/tapik-webmvc/com/example/library/generated/BooksServer.kt`

The contract's same-module document is generated at `example-contract/target/generated/tapik/Books.openapi.yml`.

The Spring Boot plugin also produces an executable application JAR. After the build, run it with:

```shell
java -jar example-application/target/tapik-example-application-0.6.0.jar
```

Then request the generated WebMVC endpoint:

```shell
curl http://localhost:8080/books
```

`BooksHandler` implements the generated server interface. Tapik's generated controller is registered by Spring Boot
auto-configuration and delegates the decoded request to that handler. `BooksRestClient` implements the generated client
interface with a `RestClientTransport`; set `library.books.base-url` to choose its remote server. The example test sends
a real generated-client exchange through Spring's mock HTTP server and verifies Jackson decoding into `Book` values.
The application selects Boot's dedicated WebMVC and RestClient starters so both generated integrations are
auto-configurable at runtime.
