# tapik

**Type-safe APIs in Kotlin**

Tapik defines HTTP APIs as ordinary Kotlin values whose inferred types retain the complete contract structure. A
single contract can be interpreted as OpenAPI documentation, a Spring RestClient client, or a Spring WebMVC server.
Kotlin remains the source of truth; Tapik does not import OpenAPI documents or discover endpoints by scanning packages.

> [!IMPORTANT]
> Tapik 0.6.0 is an experimental rewrite. Its API and generated source are not yet compatibility-stable.

## Requirements

- Java 25
- Kotlin 2.4.10 or a compatible Kotlin 2.4 release
- Maven 3.9+, supplied by the included Maven Wrapper when developing Tapik itself

## Define an API

An API may be a public class with a public no-argument constructor or a public object. Endpoint definitions are public
delegated properties; their API type and property name form stable IDs such as `Books.list`.

```kotlin
import dev.akif.tapik.*
import dev.akif.tapik.format.kotlinx.jsonBody
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val isbn: String,
    val title: String
)

object Books : Api() {
    private val requestId = header.string("X-Request-Id")

    val list by
        get(
            uri = root / "books" + query.int("page").optional(default = 1),
            summary = "List books"
        )
            .header(requestId)
            .output(Status.Ok with jsonBody<List<Book>>())
}
```

The DSL retains path variables, queries, headers, request bodies, output alternatives, presence modes, formats, and
their declaration order in the endpoint's Kotlin type. Invalid modifier order, duplicate definitions, incompatible
body alternatives, arity beyond eight, and incomplete endpoint declarations fail during compilation or construction.

Useful forms include:

```kotlin
val bookUri = root / "books" / path.uuid("bookId")

query.string("tag").repeated()
header.string("X-Source").fixed("catalog")

.input(bodiesOf(jsonBody<CreateBook>(), noBody))
.output(Status.Ok with bodiesOf(jsonBody<Book>(), xmlBookBody))
.output(Status.Created with jsonBody<Book>() with headersOf(location))
```

See the [DSL specification](specification/dsl.md) for the complete currently implemented grammar.

## Maven setup

Import the Tapik BOM once so every Tapik dependency uses the same release:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>dev.akif</groupId>
            <artifactId>tapik-bom</artifactId>
            <version>0.6.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Then add the dependency-free core DSL and explicitly choose the format integrations used by the contract. This
example selects Kotlin serialization for `jsonBody`; projects using another integration omit
`tapik-format-kotlinx`:

```xml
<dependencies>
    <dependency>
        <groupId>dev.akif</groupId>
        <artifactId>tapik-core</artifactId>
    </dependency>
    <dependency>
        <groupId>dev.akif</groupId>
        <artifactId>tapik-format-kotlinx</artifactId>
    </dependency>
</dependencies>
```

For Jackson 3 contracts, select the Jackson integration instead; it includes a Kotlin-enabled default `ObjectMapper`
and accepts an application mapper when custom Jackson behavior is required:

```xml
<dependency>
    <groupId>dev.akif</groupId>
    <artifactId>tapik-format-jackson</artifactId>
</dependency>
```

The BOM manages ordinary Tapik project dependencies. Maven still requires explicit versions for build plugins and
for dependencies nested inside a plugin declaration, as shown below.

Enable Tapik in Kotlin's Maven compiler plugin. The Kotlin serialization entries are needed only by contracts that
explicitly select `tapik-format-kotlinx` and use `@Serializable` models:

```xml
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>2.4.10</version>
    <extensions>true</extensions>
    <configuration>
        <compilerPlugins>
            <plugin>tapik</plugin>
            <plugin>kotlinx-serialization</plugin>
        </compilerPlugins>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>dev.akif</groupId>
            <artifactId>tapik-plugin-maven</artifactId>
            <version>0.6.0</version>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-serialization</artifactId>
            <version>2.4.10</version>
        </dependency>
    </dependencies>
</plugin>
```

The compiler plugin generates an explicit API registry in each contract artifact. API expressions are not executed by
the compiler, and downstream generation does not reflect over arbitrary project classes.

All `dev.akif:tapik-*` dependencies in a generation project must use the same version as `tapik-plugin-maven`. Version
skew fails before target or registry loading.

## Generate OpenAPI

Add one execution of the same Maven plugin:

```xml
<plugin>
    <groupId>dev.akif</groupId>
    <artifactId>tapik-plugin-maven</artifactId>
    <version>0.6.0</version>
    <executions>
        <execution>
            <id>generate-openapi</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <target>openapi</target>
                <targetConfiguration>
                    <version>0.6.0</version>
                </targetConfiguration>
            </configuration>
        </execution>
    </executions>
</plugin>
```

The goal runs in `process-classes` by default, so it can discover APIs compiled in the same module. It generates one
document per API under `target/generated/tapik`, such as `Books.openapi.json`.

OpenAPI target configuration:

| Setting | Required | Default | Meaning |
| --- | --- | --- | --- |
| `version` | yes | — | Document `info.version` |
| `pretty` | no | `true` | Pretty or compact JSON |
| `componentNaming` | no | `simple` | `simple` or `qualified` schema names |
| `output` | no | `{api}.openapi.json` | Relative output template |

## Generate Spring clients and servers

Generated Spring source currently works best from a separate consumer module that depends on a compiled contract
artifact. This avoids a Maven lifecycle cycle: `generate-sources` needs the contract registry, while that registry is
created when the contract is compiled.

Add the runtime needed by the selected generated target and the contract dependency:

```xml
<dependency>
    <groupId>dev.akif</groupId>
    <artifactId>tapik-target-spring-restclient</artifactId>
</dependency>
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library-contract</artifactId>
    <version>${library.version}</version>
</dependency>
```

Then run the target during `generate-sources`:

```xml
<execution>
    <id>generate-restclient</id>
    <phase>generate-sources</phase>
    <goals>
        <goal>generate</goal>
    </goals>
    <configuration>
        <target>spring-restclient</target>
        <outputDirectory>${project.build.directory}/generated-sources/tapik-restclient</outputDirectory>
        <targetConfiguration>
            <packageName>com.example.library.client</packageName>
            <clientSuffix>Client</clientSuffix>
        </targetConfiguration>
    </configuration>
</execution>
```

Use `spring-webmvc` with `tapik-target-spring-webmvc` for Spring Boot server generation. Its optional suffix settings are
`serverSuffix` and `controllerSuffix`, defaulting to `Server` and `GeneratedController`; both Spring targets default to
package `dev.akif.tapik.generated`. Source output directories are added to Maven's Kotlin compile roots automatically,
and generated runtime registration resources are packaged by Maven.

The RestClient target generates composable client interfaces backed by `RestClientTransport`. The WebMVC target
generates public interfaces containing only typed handler methods and response types. It also generates internal Spring
adapters that Boot registers automatically when exactly one matching handler bean is available, including a primary
bean among multiple candidates. A user supplies an ordinary `@Bean` or component implementing the generated interface;
no `@RestController` annotation or per-API adapter configuration is required. One bean may implement several generated
API interfaces. Automatic plain Spring registration is not supported yet.

The pure handler/generated-controller boundary is intended to extend to future server stacks: application handlers can
remain framework-free while each target supplies its own generated transport adapter. WebMVC currently generates both
parts together; a shared cross-framework server-contract target has not been extracted yet.

Generation includes every API registry visible in the project output and compile/runtime dependencies by default.
Include and exclude filters are planned but not implemented.

## Generation failures

Tapik intentionally fails the build rather than silently weakening a contract. Common failure stages are:

- Kotlin compilation: an API is not public or constructible, an endpoint property is not public, or a DSL shape is
  invalid.
- Registry loading: contract artifacts use incompatible Tapik versions or contain conflicting API IDs.
- Target validation: the selected target cannot represent an endpoint feature or its configuration is invalid.
- Generated-source compilation: generated code and its required target runtime dependency are not aligned.

Diagnostics identify the API and endpoint whenever that information is available. A non-clean build is supported:
compiler registries and generated artifacts are synchronized with their current owners instead of requiring `clean`
to remove ghost contracts or sources.

## Custom generation targets

Generation targets implement the host-neutral contracts in `tapik-common-plugin` and contribute a
`GenerationTargetRegistry` through Java's service-provider mechanism. Maven loads target providers from its plugin
realm, so a custom target artifact belongs in the dependencies of the `tapik-plugin-maven` plugin declaration. Adding
it only as an ordinary project dependency does not make the target executable; Tapik reports that placement error and
lists the targets visible to the plugin.

See the [generation specification](specification/generation.md), [OpenAPI specification](specification/openapi.md), and
[Spring specification](specification/spring.md) for detailed target behavior and limitations.

## Rewrite migration status

The 0.6 rewrite replaces the previous implementation rather than preserving its build integration or binary API:

- Maven is the only supported build host today; the previous Gradle integration is not available.
- API registries are generated at Kotlin compile time instead of scanning packages or reflecting over endpoints.
- Kotlin contract values are authoritative; OpenAPI is generated output and cannot be consumed as an input contract.
- Existing endpoint definitions and generated-build configuration require manual migration.

## Build Tapik

```shell
./mvnw clean verify
```

The build requires Java 25 and runs every production, fixture, and cross-artifact integration module. Product and
architecture decisions live under [`specification`](specification/README.md).
