# Development specification

## Toolchain

- Java 25
- Maven 3.9+ through the included wrapper
- Kotlin 2.4.10 or a later compatible 2.4 release
- Kotest 6.2

The build fails outside the supported Java and Maven ranges.

## Workflow

Every observable behavior follows this sequence:

1. Amend the product or DSL specification.
2. Add the smallest Kotest spec that demonstrates the behavior and observe it fail.
3. Implement the smallest coherent production change that makes it pass.
4. Refactor while the complete reactor remains green.
5. Run `./mvnw verify` before handing off a change.

Compile-failure behavior should eventually use a dedicated Kotlin compilation-test harness. Selecting that harness is
itself deferred until the first compile-failure behavior is implemented.

## Continuous integration

Pull requests targeting `main` and pushes to `main` run a clean `./mvnw verify` on Linux with Temurin Java 25. CI uses
the Maven Wrapper and the Kotlin version declared by the parent build, so it verifies the minimum supported toolchain
without maintaining an independent version configuration. The workflow has read-only repository permissions, includes
every production and `test-` reactor module, and receives no publication credentials.

## Publication

Tapik releases are published to Maven Central under `dev.akif`. The parent and every production module inherit the
project name, description, URL, MIT license, developer, and source-control metadata required by Central. Production
JARs include their Kotlin sources and a per-module `javadoc`-classifier JAR containing Dokka's standard HTML output.
The classifier satisfies repository conventions without switching Dokka to its experimental Javadoc renderer. The
parent POM and every production artifact, including attached source and documentation artifacts, are signed during a
release build.

Publication is opt-in through the Maven `release` profile. Ordinary builds, including CI's `./mvnw verify`, neither
load signing credentials nor contact Central. `./mvnw -Prelease -Dgpg.skip=true verify` is the credential-free local
check for release packaging; it must attach a `sources` JAR and a `javadoc` JAR to every production JAR module. An
authenticated `./mvnw -Prelease clean deploy` creates one reactor-wide Central deployment, publishes it automatically,
and waits for publication to finish.

Modules whose folder begins with `test-` remain installable for reactor and local integration builds, but release
packaging does not attach publication artifacts to them and Central deployment excludes them. The parent POM remains
publishable because published module POMs inherit their shared project metadata and build coordinates from it.

## Modules

The rewrite starts with one `dev.akif:tapik-core` artifact. New artifacts are introduced only when a specification
needs an independently consumable boundary. Maven module folders are flat beneath the repository root, omit the
`tapik-` artifact prefix, and, except for `core`, begin with exactly one role prefix: `common-`, `format-`, `plugin-`,
`target-`, or `test-`. Artifacts add `tapik-` before the complete module folder name. The Kotlin serialization
integration is therefore `dev.akif:tapik-format-kotlinx` with packages under `dev.akif.tapik.format.kotlinx`.

`plugin-` is reserved for integrations invoked by a compiler or build tool. Modules implementing the host-neutral
generation target SPI use `target-`, even when their generated artifacts integrate with a framework. Target IDs used
in generation configuration do not include this module-role prefix.

Kotlin source paths omit the common `dev/akif/tapik` package directories. A declaration in `dev.akif.tapik` lives
directly beneath `src/main/kotlin` or `src/test/kotlin`; subpackage paths begin after that common package.

| Module folder | Maven artifact |
| --- | --- |
| `core` | `dev.akif:tapik-core` |
| `common-plugin` | `dev.akif:tapik-common-plugin` |
| `common-spring` | `dev.akif:tapik-common-spring` |
| `format-kotlinx` | `dev.akif:tapik-format-kotlinx` |
| `plugin-compiler` | `dev.akif:tapik-plugin-compiler` |
| `plugin-maven` | `dev.akif:tapik-plugin-maven` |
| `target-openapi` | `dev.akif:tapik-target-openapi` |
| `target-spring-restclient` | `dev.akif:tapik-target-spring-restclient` |
| `target-spring-webmvc` | `dev.akif:tapik-target-spring-webmvc` |
| `test-fixtures` | `dev.akif:tapik-test-fixtures` |
| `test-maven-contract` | `dev.akif:tapik-test-maven-contract` |
| `test-maven-integration` | `dev.akif:tapik-test-maven-integration` |

`core` remains dependency-free. Format integrations are opt-in contract dependencies: neither core nor generation
targets select a serialization library for the user. A contract may use one integration, combine multiple integrations,
or construct core `Format` values directly; targets consume only the concrete formats already attached to the endpoint
values.

`format-kotlinx` depends on Kotlin serialization and converts its serializers into core codecs and schemas without
leaking Kotlin serialization types into endpoint contracts. Projects that choose another integration do not depend on
`format-kotlinx` or enable the Kotlin serialization compiler plugin. Dependencies used internally to render a target's
own artifact are separate from contract format integrations and do not choose how endpoint values are encoded.

`core` exposes the minimal API registry provider contract needed by compiler-generated code. The `common-plugin` module
defines host-neutral registry loading, target configuration, target execution, and generated artifact contracts.
Target modules and adapters for Maven, Gradle, or command-line use depend on `common-plugin`; it must not depend on any
build-tool API.

Shared Spring integration code lives in `common-spring` under `dev.akif.tapik.common.spring`. The
`target-spring-restclient` and `target-spring-webmvc` packages are respectively
`dev.akif.tapik.target.spring.restclient` and `dev.akif.tapik.target.spring.webmvc`.

Other module packages follow the same folder-name hierarchy: `common-plugin` uses `dev.akif.tapik.common.plugin`,
`plugin-compiler` uses `dev.akif.tapik.plugin.compiler`, `target-openapi` uses `dev.akif.tapik.target.openapi`, and
`plugin-maven` uses `dev.akif.tapik.plugin.maven`. The non-production `test-maven-contract` and
`test-maven-integration` modules use `dev.akif.tapik.test.maven.contract` and
`dev.akif.tapik.test.maven.integration`. Together they verify the complete Maven user workflow against the
reactor-built artifacts, including APIs supplied by a separate contract artifact.

The non-production `test-fixtures` module contains ordinary Tapik definitions grouped by domain package. Its initial
`dev.akif.tapik.test.fixtures.library` package covers books, authors, and rentals. Target modules consume this artifact
in their tests so every interpreter is verified against the same contract instead of maintaining target-specific
fixtures.

Non-production modules remain installable so reactor and local integration builds can resolve them, but they must be
excluded from deployment. This applies to every `test-` module; release deployment publishes only production modules.

The `target-openapi` module interprets compiled `Api` values directly. It does not scan the classpath or copy contracts
into a neutral metadata model. Its public document model represents the OpenAPI output itself, and deterministic JSON
is the first rendering format.
