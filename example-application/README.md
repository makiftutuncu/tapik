# Runnable Maven example

The example has two modules:

- `example-contract` is the API module. It defines library endpoints and generates OpenAPI documentation.
- `example-application` uses the compiled APIs to generate OpenAPI, a Spring client, and a Spring server. It includes an
  implementation of the generated server.

Build and verify both modules from the repository root:

```shell
./mvnw -pl example-application -am clean verify
```

The API module writes OpenAPI under `example-contract/target/generated/tapik/`. The application writes OpenAPI under
`example-application/target/generated/tapik/`, with generated Spring source and registration resources under
`example-application/target/generated-sources/tapik-spring/`.

To run the application after the build:

```shell
java -jar example-application/target/tapik-example-application-0.6.0.jar
```

Then request the books endpoint:

```shell
curl http://localhost:8080/books
```

For the shared guides, see the [Maven quickstart](https://tapik.akif.dev/docs/getting-started.html),
[Maven reference](https://tapik.akif.dev/docs/reference/maven.html),
[Spring RestClient guide](https://tapik.akif.dev/docs/clients/spring-restclient.html), and
[Spring WebMVC guide](https://tapik.akif.dev/docs/servers/spring-webmvc.html).
