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

## Modules

The rewrite starts with one `dev.akif:tapik-core` artifact. New artifacts are introduced only when a specification
needs an independently consumable boundary. Maven module folders are flat beneath the repository root, omit the
`tapik-` artifact prefix, and, except for `core`, begin with exactly one role prefix: `plugin-`, `format-`, `common-`,
or `test-`. Artifacts add `tapik-` before the complete module folder name. The Kotlin serialization integration is
therefore `dev.akif:tapik-format-kotlinx` with packages under `dev.akif.tapik.format.kotlinx`.

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
| `plugin-openapi` | `dev.akif:tapik-plugin-openapi` |
| `plugin-spring-restclient` | `dev.akif:tapik-plugin-spring-restclient` |
| `plugin-spring-webmvc` | `dev.akif:tapik-plugin-spring-webmvc` |
| `test-fixtures` | `dev.akif:tapik-test-fixtures` |
| `test-maven-contract` | `dev.akif:tapik-test-maven-contract` |
| `test-maven-integration` | `dev.akif:tapik-test-maven-integration` |

`core` remains dependency-free. `format-kotlinx` depends on Kotlin serialization and converts its serializers into
core codecs and schemas without leaking Kotlin serialization types into endpoint contracts.

`core` exposes the minimal API registry provider contract needed by compiler-generated code. The `common-plugin` module
defines host-neutral registry loading, target configuration, target execution, and generated artifact contracts.
Target modules and adapters for Maven, Gradle, or command-line use depend on `common-plugin`; it must not depend on any
build-tool API.

Shared Spring integration code lives in `common-spring` under `dev.akif.tapik.common.spring`. The
`plugin-spring-restclient` and `plugin-spring-webmvc` packages are respectively
`dev.akif.tapik.plugin.spring.restclient` and `dev.akif.tapik.plugin.spring.webmvc`.

Other module packages follow the same folder-name hierarchy: `common-plugin` uses `dev.akif.tapik.common.plugin`,
`plugin-compiler` uses `dev.akif.tapik.plugin.compiler`, `plugin-openapi` uses `dev.akif.tapik.plugin.openapi`, and
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

The `plugin-openapi` module interprets compiled `Api` values directly. It does not scan the classpath or copy contracts
into a neutral metadata model. Its public document model represents the OpenAPI output itself, and deterministic JSON
is the first rendering format.
