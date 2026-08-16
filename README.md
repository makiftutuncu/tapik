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

## Maven usage

Define APIs as public Kotlin classes or objects. Tapik derives the API ID from the type name and the endpoint ID from
the delegated property name.

```kotlin
object Books : Api() {
    val list by get(root / "books")
}
```

Add the core DSL as a project dependency:

```xml
<dependency>
    <groupId>dev.akif</groupId>
    <artifactId>tapik-core</artifactId>
    <version>0.6.0</version>
</dependency>
```

Then add `dev.akif:tapik-plugin-maven` to Kotlin's compiler-plugin dependencies and enable `tapik`. Use the same
artifact for each generation execution:

```xml
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>2.4.10</version>
    <extensions>true</extensions>
    <configuration>
        <compilerPlugins>
            <plugin>tapik</plugin>
        </compilerPlugins>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>dev.akif</groupId>
            <artifactId>tapik-plugin-maven</artifactId>
            <version>0.6.0</version>
        </dependency>
    </dependencies>
</plugin>
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

All public concrete APIs in the project are included by default. This execution writes one document per API under
`target/generated/tapik`.

## Build

```shell
./mvnw verify
```
