# tapik

**Type-safe APIs in Kotlin**

tapik defines HTTP APIs as ordinary Kotlin values whose inferred types retain the complete contract structure. A
single contract can be interpreted as OpenAPI documentation, a Spring RestClient client, or a Spring WebMVC server.
Kotlin remains the source of truth; tapik does not import OpenAPI documents or discover endpoints by scanning packages.

> [!IMPORTANT]
> tapik 0.6.0 is an experimental rewrite. Its API and generated source are not yet compatibility-stable.

## Requirements

- Java 25
- Kotlin 2.4.10 or a compatible Kotlin 2.4 release
- Maven 3.9+, supplied by the included Maven Wrapper when developing tapik itself

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

APIs can be composed explicitly without changing the included endpoint values or IDs:

```kotlin
object Library : Api() {
    val authors by including(Authors)
    val books by including(Books)
}
```

`Library.authors.list` is the same typed endpoint value as `Authors.list`, still identified as `Authors.list`.
Inclusions may be nested, and `Library.endpoints` flattens them in the position where each inclusion property is
declared. Inclusion properties must be public and should use inferred concrete types, as shown above, so generated
sources can follow paths such as `libraryApi.authors.list`. The standalone `Authors` and `Books` APIs remain selectable
for generation; use `includeApis` when an execution should generate only `Library`.

See the [DSL specification](specification/dsl.md) for the complete currently implemented grammar.

## Maven setup

Define the versions once:

```xml
<properties>
    <java.version>25</java.version>
    <kotlin.version>2.4.10</kotlin.version>
    <tapik.version>0.6.0</tapik.version>
</properties>
```

Import the tapik BOM once so every tapik dependency uses the same release:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>dev.akif</groupId>
            <artifactId>tapik-bom</artifactId>
            <version>${tapik.version}</version>
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

The BOM manages ordinary tapik project dependencies. Maven still requires explicit versions for build plugins and
for dependencies nested inside a plugin declaration, as shown below.

In every module that declares `Api` values, enable tapik in Kotlin's Maven compiler plugin. A module that only consumes
a compiled tapik contract does not need the `tapik` compiler plugin. The Kotlin serialization entries are needed only
by contracts that explicitly select `tapik-format-kotlinx` and use `@Serializable` models:

```xml
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>${kotlin.version}</version>
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
            <version>${tapik.version}</version>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-serialization</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
    </dependencies>
</plugin>
```

The compiler plugin generates an explicit API registry in each contract artifact. API expressions are not executed by
the compiler, and downstream generation does not reflect over arbitrary project classes.

All `dev.akif:tapik-*` dependencies in a generation project must use the same version as `tapik-plugin-maven`. Version
skew fails before target or registry loading.

## Choose a generation mode

The `generate` goal reads compiled API registries; it never scans Kotlin source. The location of the API definitions
therefore determines the Maven phase and whether the current module can use the generated types:

| API definitions read from | Generation phase | Current main source can use generated types | Minimum lifecycle command |
| --- | --- | --- | --- |
| A compiled contract dependency | `generate-sources` | yes | `./mvnw compile` |
| The current module | `process-classes` (default) | no | `./mvnw process-classes` |

Use a **compiled contract dependency** for a client or server application. Put the API definitions and their model
types in a contract module, compile that module, and let the application generate sources from that dependency during
`generate-sources`. The generated types then exist before the application's ordinary Kotlin compilation, so application
source can implement `UsersServer` or invoke `UsersClient`.

Use **same-module generation** for OpenAPI or when the generated source should be packaged with the contract for a
downstream module. The execution runs during `process-classes`, after the current module has compiled and published its
registry. tapik compiles the generated Kotlin separately into `target/classes`, but it does not and cannot recompile the
module's earlier main source.

> [!WARNING]
> A clean build cannot compile `src/main/kotlin` in the same module when it imports a type that tapik generates during
> `process-classes`. For example, a module containing both `object Users : Api()` and
> `class UsersHandler : UsersServer` must be split into a contract module and an application module. A non-clean build
> may appear to work because an old `UsersServer.class` remains in `target`; do not rely on that state.

### Maven phases and commands

| Command | Compiled-contract execution at `generate-sources` | Same-module execution at `process-classes` |
| --- | --- | --- |
| `./mvnw generate-sources` | generates targets | does not run |
| `./mvnw compile` | generates targets, then compiles the project | compiles the registry, but does not generate targets |
| `./mvnw process-classes` | generates targets and compiles the project | generates targets |
| `./mvnw package`, `verify`, or `install` | runs | runs |

The default phase belongs to a lifecycle-bound plugin execution. Invoking `./mvnw tapik:generate` directly does not
first compile the current project and does not reliably select an execution's configuration. Prefer the lifecycle
commands in the table. To start a Spring Boot application after a clean build, use
`./mvnw process-classes spring-boot:run`; `spring-boot:run` alone should not be treated as a replacement for the Maven
lifecycle that generates sources.

Each generated file is logged at `INFO`, for example:

```text
[INFO] --- tapik:0.6.0:generate (generate-webmvc) @ application ---
[INFO] Generated target/generated-sources/tapik-webmvc/com/example/generated/UsersServer.kt
```

If the Kotlin registry appears but no target files or tapik generation log appears, the selected command most likely
stopped at `compile` while the execution was using the default `process-classes` phase.

## Generate OpenAPI

Add one execution of the same Maven plugin:

```xml
<plugin>
    <groupId>dev.akif</groupId>
    <artifactId>tapik-plugin-maven</artifactId>
    <version>${tapik.version}</version>
    <executions>
        <execution>
            <id>generate-openapi</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <target>openapi</target>
                <targetConfiguration>
                    <version>${project.version}</version>
                </targetConfiguration>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This example is same-module generation: no `<phase>` is specified, so the goal uses its default `process-classes`
phase and can read APIs compiled from the current module. Run `./mvnw process-classes` or any later lifecycle phase; a
plain `./mvnw compile` does not invoke this execution. It generates one YAML document per API under
`target/generated/tapik`, such as `Books.openapi.yml`. Set `format` to `json` when JSON is preferred.

OpenAPI can instead be generated from a compiled contract dependency by adding `<phase>generate-sources</phase>` to
the execution. In either mode every discovered API is selected by default. An execution can narrow generation by exact
API ID:

```xml
<includeApis>
    <includeApi>Books</includeApi>
    <includeApi>Authors</includeApi>
</includeApis>
<excludeApis>
    <excludeApi>Authors</excludeApi>
</excludeApis>
```

Exclusions are applied after inclusions. Unknown API IDs fail the build.

OpenAPI target configuration:

| Setting | Required | Default | Meaning |
| --- | --- | --- | --- |
| `version` | yes | — | Document `info.version` |
| `format` | no | `yaml` | `yaml` or `json` document rendering |
| `pretty` | no | `true` | Pretty or compact JSON |
| `componentNaming` | no | `simple` | `simple` or `qualified` schema names |
| `output` | no | format-specific | `{api}.openapi.yml` or `{api}.openapi.json` |

## Generate Spring clients and servers

For an application that implements or invokes generated Spring types, use two modules:

```text
library-contract                  library-server
API values and models      -->    depends on library-contract
tapik compiler registry           generates during generate-sources
                                  implements generated server interfaces
```

Build the contract module with the compiler-plugin setup shown above. It needs no `generate` execution unless it also
wants same-module OpenAPI or wants to package generated types. In the application module, add the contract dependency
and the runtime for the selected target:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library-contract</artifactId>
    <version>${library.version}</version>
</dependency>
<dependency>
    <groupId>dev.akif</groupId>
    <artifactId>tapik-target-spring-webmvc</artifactId>
</dependency>
```

Then generate the WebMVC server before the application's Kotlin compilation:

```xml
<plugin>
    <groupId>dev.akif</groupId>
    <artifactId>tapik-plugin-maven</artifactId>
    <version>${tapik.version}</version>
    <executions>
        <execution>
            <id>generate-webmvc</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <target>spring-webmvc</target>
                <outputDirectory>${project.build.directory}/generated-sources/tapik-webmvc</outputDirectory>
                <targetConfiguration>
                    <packageName>com.example.library.generated</packageName>
                </targetConfiguration>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Keep the application's ordinary `kotlin-maven-plugin` setup so Maven compiles Kotlin and the generated source root.
Do not enable its `tapik` compiler plugin unless this application module also declares its own `Api` values.

The application can now implement the generated interface from ordinary main source:

```kotlin
@Component
class BooksHandler : BooksServer {
    override val booksApi: Books = Books

    override fun list(): BooksServer.ListResponse =
        BooksServer.ListResponse.Ok(emptyList())
}
```

Run `./mvnw clean verify` from the multi-module root, or `./mvnw -pl library-server -am clean verify` to build the
application and its contract dependency. Running `./mvnw compile` in the application is also enough to generate and
compile its server types once the contract artifact is available.

A complete runnable version of this arrangement lives in
[`example-contract`](example-contract) and
[`example-application`](example-application). Its Jackson-backed contract generates OpenAPI,
a RestClient interface, and a WebMVC server interface in every reactor build. The application implements and exercises
both generated Spring interfaces. See the
[example instructions](example-application/README.md) for its layout, generated artifacts, and run
command.

For a RestClient, replace the runtime dependency with `tapik-target-spring-restclient` and configure an otherwise
identical execution as follows:

```xml
<target>spring-restclient</target>
<outputDirectory>${project.build.directory}/generated-sources/tapik-restclient</outputDirectory>
<targetConfiguration>
    <packageName>com.example.library.client</packageName>
    <clientSuffix>Client</clientSuffix>
</targetConfiguration>
```

Spring targets may also use same-module generation by omitting `<phase>generate-sources</phase>`. In that mode the
generated source and registration resources are compiled and packaged with the current artifact during
`process-classes`; only downstream modules can refer to those generated types from their main source.

Use `spring-webmvc` with `tapik-target-spring-webmvc` for Spring Boot server generation. Its optional suffix settings are
`serverSuffix` and `controllerSuffix`, defaulting to `Server` and `GeneratedController`; both Spring targets default to
package `dev.akif.tapik.generated`. Source output directories are added to Maven's Kotlin compile roots automatically.
Generated runtime registration resources are also copied into the main output when generation happens after Maven's
normal resources phase.

The RestClient target generates composable client interfaces backed by `RestClientTransport`. The WebMVC target
generates public interfaces containing only typed handler methods and response types. It also generates internal Spring
adapters that Boot registers automatically when exactly one matching handler bean is available, including a primary
bean among multiple candidates. A user supplies an ordinary `@Bean` or component implementing the generated interface;
no `@RestController` annotation or per-API adapter configuration is required. One bean may implement several generated
API interfaces. Plain Spring applications add `@EnableTapikWebMvc` to one configuration class; this registers the same
generated adapters without component scanning but does not configure Spring MVC itself.

The pure handler/generated-controller boundary is intended to extend to future server stacks: application handlers can
remain framework-free while each target supplies its own generated transport adapter. WebMVC currently generates both
parts together; a shared cross-framework server-contract target has not been extracted yet.

Generation includes every API registry visible in the project output and compile/runtime dependencies by default.
Use the `includeApis` and `excludeApis` execution settings shown in the OpenAPI section to select exact API IDs for any
target.

## Maven troubleshooting

- **`Generation requires at least one API` during `generate-sources`:** the execution is trying to read APIs from the
  current module before that module has compiled. Remove the explicit phase to use `process-classes`, or move the APIs
  to a compiled contract dependency.
- **Registry files exist but target files do not:** `compile` ran the Kotlin compiler plugin, but Maven never reached a
  same-module execution's `process-classes` phase. Run `./mvnw process-classes` or a later phase and look for the
  `Generated ...` log lines.
- **A generated server or client is unresolved from current main source:** same-module generation is too late for that
  source set. Use a separate compiled contract module and generate in the consumer during `generate-sources`.
- **An IDE build behaves differently from Maven:** delegate the build to Maven or run the appropriate lifecycle command
  before importing generated sources. Confirm the setup with a clean Maven build; stale files under `target` can hide
  an invalid lifecycle arrangement.
- **A target is unknown:** built-in targets come from `tapik-plugin-maven`; custom targets must be plugin dependencies,
  not only ordinary project dependencies.

## Generation failures

tapik intentionally fails the build rather than silently weakening a contract. Common failure stages are:

- Kotlin compilation: an API is not public or constructible, an endpoint property is not public, or a DSL shape is
  invalid.
- Registry loading: contract artifacts use incompatible tapik versions or contain conflicting API IDs.
- Target validation: the selected target cannot represent an endpoint feature or its configuration is invalid.
- Generated-source compilation: generated code and its required target runtime dependency are not aligned.

Diagnostics identify the API and endpoint whenever that information is available. A non-clean build is supported:
compiler registries and generated artifacts are synchronized with their current owners instead of requiring `clean`
to remove ghost contracts or sources.

## Custom generation targets

Generation targets implement the host-neutral contracts in `tapik-common-plugin` and contribute a
`GenerationTargetRegistry` through Java's service-provider mechanism. Maven loads target providers from its plugin
realm, so a custom target artifact belongs in the dependencies of the `tapik-plugin-maven` plugin declaration. Adding
it only as an ordinary project dependency does not make the target executable; tapik reports that placement error and
lists the targets visible to the plugin.

See the [generation specification](specification/generation.md), [OpenAPI specification](specification/openapi.md), and
[Spring specification](specification/spring.md) for detailed target behavior and limitations.

## Rewrite migration status

The 0.6 rewrite replaces the previous implementation rather than preserving its build integration or binary API:

- Maven is the only supported build host today; the previous Gradle integration is not available.
- API registries are generated at Kotlin compile time instead of scanning packages or reflecting over endpoints.
- Kotlin contract values are authoritative; OpenAPI is generated output and cannot be consumed as an input contract.
- Existing endpoint definitions and generated-build configuration require manual migration.

Follow the [migration guide](docs/modules/ROOT/pages/migration.adoc) when moving an older project to 0.6.

## Build tapik

```shell
./mvnw clean verify
```

The build requires Java 25 and runs every production, fixture, and runnable example module. Product and architecture
decisions live under [`specification`](specification/README.md).

Before publication, run the unsigned packaging and external-consumer rehearsal:

```shell
./mvnw -Prelease -Dgpg.skip=true -Dcentral.skipPublishing=true -Dtapik.release.rehearsal=true clean install
```

This installs locally but does not publish. The [release procedure](docs/modules/ROOT/pages/releasing.adoc) explains
the isolated consumer, retained diagnostics, signing, and the separate publication steps.

Build the landing page and unified API reference after installing the reactor artifacts locally, then generate the
Antora documentation portal. The documentation toolchain requires Node.js 20 or newer:

```shell
./mvnw -DskipTests install
./mvnw -f site/pom.xml package
npm ci
npm run docs:preview
```

The complete preview is written to `site/target/site`: the landing page is at `/`, narrative documentation at
`/docs/`, and unified Kotlin API reference at `/docs/api/`. Stable documentation is published from release refs, not
from unreleased `main` content. The public site lives at [tapik.akif.dev](https://tapik.akif.dev).

The preview command also validates source snippets, Antora warnings, required pages, and generated internal links.
Run the desktop and mobile browser checks after building the preview:

```shell
npx playwright install chromium
npm run docs:smoke
```

Browser reports and failure traces are written under `site/target/`. CI runs these checks before publishing a release.
