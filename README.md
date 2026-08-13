# tapik

**Type-safe APIs in Kotlin**

Tapik is being rewritten from first principles. It will let users define HTTP APIs as ordinary, composable Kotlin
values whose inferred types retain the complete contract structure. OpenAPI generation will be the first target,
followed by Spring RestClient clients and Spring Web MVC servers.

The rewrite is specification-driven and test-driven. The current product and DSL decisions live in
[`specification`](specification/README.md).

## Requirements

- Java 25
- Kotlin 2.4.10

The included Maven Wrapper provides the supported Maven version.

## Build

```shell
./mvnw verify
```
