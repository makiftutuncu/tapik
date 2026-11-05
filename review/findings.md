# Gradle plugin review findings

Reviewed on 2026-10-30 against the current `rewrite` branch. The review covered the Gradle specification, public DSL,
compiler-plugin attachment, both generation modes, worker/classloader isolation, caching, output ownership, Maven
parity, documentation, examples, CI, and release rehearsal.

## Overall assessment

The Gradle adapter is substantial and coherent. It preserves the compiler-generated registry and service-loaded target
architecture instead of reintroducing scanning or a Gradle-specific endpoint model. The important generation path is
already shared correctly through `ApiCatalog`, `GenerationTargetCatalog`, `ApiSelection`, `GenerationEngine`,
`TargetConfiguration`, `GenerationResult`, and `ArtifactWriter`. The single output-owning task, kind-based output
routing, isolated contract classloader, aggregate-before-write behavior, typed built-in DSL, structural custom-target
configuration, configuration-cache support, publication metadata, and external-consumer rehearsal are all strong.

The current Maven and Gradle examples generate byte-identical corresponding OpenAPI documents, Kotlin sources, and
Spring WebMVC registration resources. The differences in root layout and ownership manifests are intentional host
concerns. However, the issues below leave several documented contracts unenforced and create lifecycle behavior that
does not yet match Maven in less common but valid builds.

## High priority

### 1. Same-project outputs do not carry their producer dependencies

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/SameProjectWiring.kt:88-103`

`generatedClasses` and `generatedResources` are added to `main.output` as plain directories. The plugin then manually
adds dependencies only to `JavaExec`, `Test`, and the task literally named `jar`. A task consuming
`sourceSets.main.output` or `sourceSets.main.runtimeClasspath` therefore sees the late output directories but does not
inherit `tapikGenerate` or `compileTapikGeneratedKotlin` as producers. `bootJar`, `shadowJar`, distribution/start-script
tasks, custom archives, publication tasks, and custom runtime consumers can package missing or stale generated classes
and resources. Maven does not have this hole because late output is synchronized into the ordinary main output before
later lifecycle phases.

Model late classes and resources as produced file collections or a dedicated variant whose build dependencies travel
with every consumer. Avoid wiring only known task types or names, and take care not to introduce the existing
`classes -> tapikGenerate -> classes` cycle. Add functional coverage with at least one generic main-runtime consumer and
one archive task other than the standard `jar`.

### 2. Version alignment silently excludes project and composite-build components

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/GradleTapikVersions.kt:13-23`

The specification requires every resolved external or project `dev.akif:tapik-*` component to match the plugin
version, but the implementation filters resolved IDs to `ModuleComponentIdentifier`. Gradle project dependencies and
project substitutions use project component IDs, so incompatible local tapik modules bypass validation and reach
classloading. Maven checks reactor dependencies through the project's resolved artifacts, making this a real host
parity gap.

Read coordinates and versions from resolved component metadata for both module and project components, retaining the
configuration and component identity in the diagnostic. Cover a mismatched multi-project dependency and a composite
substitution in TestKit.

### 3. Environment validation can be skipped by up-to-date and build-cache reuse

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/TapikGenerateTask.kt:35-48,88-92`

Project type, Gradle version, Java version, and Kotlin version are all `@Internal`, and validation occurs only inside
the task action. Gradle does not execute that action for `UP-TO-DATE` or `FROM-CACHE`, so an output produced in a
supported Kotlin/JVM environment can be accepted after changing to an unsupported Kotlin plugin or project type when
the declared generation inputs remain unchanged. This contradicts the documented compatibility check and makes cache
behavior decide whether invalid configuration is rejected.

Make compatibility-affecting values task inputs, or perform validation through a lifecycle hook that still runs before
cached/up-to-date task completion. Add a functional test that first creates valid output, changes only the Kotlin
version or project type, and verifies that the next invocation fails rather than reusing task state.

### 4. Registry state collides between JVM targets with the same compilation name

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/TapikGradlePlugin.kt:81-95`

The compiler plugin is intentionally applicable to every JVM compilation, including JVM compilations exposed by a
multiplatform project even though generation itself rejects that project type. Its registry index is keyed only by
`kotlinCompilation.name`. Two JVM targets normally both have `main` and `test` compilations, so they share
`.gradle/tapik/compiler/main/registry.index`. Each compilation reconciles that shared file against its own source roots,
overwriting the other target's incremental state; a later partial compilation can consequently regenerate an
incomplete registry.

Include the Kotlin target identity as well as compilation identity in the state path and add a compiler-attachment test
with two JVM targets that performs alternating non-clean changes.

## Medium priority

### 5. Gradle rejects a selection that the host-neutral model and Maven accept

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/TapikExecution.kt:44-54`

`ApiSelection` defines exclusions as applying after inclusions, so exclusion wins when an ID occurs in both sets. Maven
passes that case directly to the shared model. Gradle rejects the overlap before the shared model sees it. This is both
a host parity defect and an internal specification contradiction: `specification/generation.md:14-17` and
`specification/gradle.md:48-52` describe exclusion-wins semantics, while `specification/gradle.md:103-107` describes the
adapter rejection.

Remove the Gradle-only overlap check and its contradictory specification/test expectations. Let `ApiSelection` remain
the single owner of selection semantics.

### 6. Declared execution order is preserved in results, but not in invocation

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/TapikGenerateTask.kt:94-123`

All work items are submitted to one Worker API queue before one final `await`. Gradle may execute those items
concurrently and does not promise FIFO invocation. Zipping result files afterward restores aggregation and reporting
order, but it does not satisfy the public contract that targets are invoked in declaration order. Custom targets can
observe the difference through shared services, external inputs, diagnostics, or other side effects.

Submit one isolated work item containing the ordered execution snapshots and invoke them sequentially inside that
worker, or await each item before submitting the next. The former also avoids rebuilding both service catalogs and the
contract classloader for every execution. Test invocation order with a target whose calls are deliberately delayed;
checking only result order is insufficient.

### 7. Same-project classpaths omit compile-only dependencies that Maven includes

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/SameProjectWiring.kt:21-31,69-73`

Both contract loading and late generated-source compilation use main runtime dependencies. Gradle's runtime classpath
does not contain `compileOnly` dependencies, while Maven's `generationClasspath()` deliberately combines compile and
runtime classpaths. A valid library project using compile-only API dependencies can compile its contract normally but
fail when tapik instantiates that contract or compiles generated source referring to those APIs. The late compilation
should have at least the same compile visibility as ordinary main source.

Build the generation/late-compilation classpath from the appropriate main compile and runtime classpaths, deduplicated
without losing Gradle task dependencies, and include every classpath actually used in tapik version validation. Add a
same-project fixture whose generated source requires a compile-only dependency.

### 8. Gradle duplicates target-owned defaults and can drift from Maven

Locations:

- `plugin-gradle/src/main/kotlin/plugin/gradle/OpenApiExecution.kt:50-54`
- `plugin-gradle/src/main/kotlin/plugin/gradle/SpringRestClientExecution.kt:18-21`
- `plugin-gradle/src/main/kotlin/plugin/gradle/SpringWebMvcExecution.kt:22-26`
- `plugin-gradle/src/main/kotlin/plugin/gradle/GradleExecutionConfiguration.kt:5-30`

Optional built-in settings receive Gradle conventions and are always serialized into target configuration. The same
defaults already belong to and are applied by the target modules. Maven omits absent values and therefore follows the
target's current defaults, whereas Gradle freezes a second copy in the adapter. A future target-default change would
silently produce different Maven and Gradle output even though both use the same `GenerationEngine` and target.

Keep the typed Gradle properties and enums, but serialize optional values only when users set them; let each target own
its defaults and validation. Add an automated cross-host parity fixture for default and explicit built-in
configuration, rather than relying on two independently successful examples.

### 9. Maven and Gradle do not share the same service-provider isolation boundary

Locations:

- `plugin-maven/src/main/kotlin/plugin/maven/ProjectApis.kt:15-19`
- `plugin-maven/src/main/kotlin/plugin/maven/MavenTargets.kt:13-19`
- `plugin-gradle/src/main/kotlin/plugin/gradle/GradleGenerationLoader.kt:26-43,107-118`

Gradle correctly hides `ApiRegistry` and `GenerationTargetRegistry` service resources from the contract
classloader's parent. Maven uses an ordinary parent-first project `URLClassLoader`. Consequently an `ApiRegistry`
accidentally packaged in a Maven plugin dependency can be discovered as a project API, and the catalog inspected as
the Maven "project" target side also includes parent plugin-realm providers. The two hosts therefore share catalog
classes but not catalog visibility semantics.

Extract the child-only service-resource classloader/session behavior into `common-plugin` and use it from both host
adapters while retaining host-specific classpath labels. Add parity tests with service descriptors deliberately placed
on the wrong side of each boundary.

### 10. Compiler option paths make Kotlin compilation cache keys location-dependent

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/TapikGradlePlugin.kt:81-95`

The registry index and every source root are passed as ordinary `SubpluginOption` values containing absolute paths.
Kotlin's Gradle plugin treats ordinary options as task inputs and explicitly provides file/internal option types for
path-valued options. The current options therefore change when an otherwise identical project moves, preventing Kotlin
compile cache relocation even though the underlying sources are already declared inputs. The existing relocation test
uses a precompiled contract JAR and exercises `tapikGenerate`, so it cannot catch this compiler-task behavior.

Use path-aware/internal compiler options as appropriate and register the registry index as compilation-local state so
Gradle understands its lifecycle. Add a relocated contract-compilation cache test, followed by a partial source change
that verifies the complete registry remains intact.

### 11. Unknown-target diagnosis suppresses malformed contract-side providers

Location: `plugin-gradle/src/main/kotlin/plugin/gradle/GradleGenerationLoader.kt:69-88`

The misplaced-target probe wraps the entire contract-side target catalog load in `runCatching` and converts every
failure to an empty catalog. A malformed service entry, linkage failure, or duplicate target ID on the wrong classpath
is therefore hidden behind an ordinary "unknown target" message, discarding the contextual error and cause that
`loadTargets` just created.

Let catalog failures propagate; an absent provider already produces an empty catalog without throwing. Add failure
coverage for malformed and duplicate target providers on `tapikContractClasspath`.

## Low priority

### 12. Successful generation can be reported as failed while formatting log paths

Locations:

- `plugin-gradle/src/main/kotlin/plugin/gradle/TapikGenerateTask.kt:125-135`
- `plugin-maven/src/main/kotlin/plugin/maven/GenerateMojo.kt:93-100`

Gradle writes and commits the complete output tree before calling `Path.relativize` for logging. Relativizing paths on
different filesystem roots throws on platforms such as Windows when `outputRoot` is on another drive, so the task fails
after successful mutation. Maven already uses a safe fallback to the absolute path.

Reuse the Maven fallback behavior in a neutral path-display helper or apply the same guarded relativization in Gradle,
and test an output location that cannot be relativized where the platform supports it.

## Verification performed

- `./scripts/verify-gradle.sh` passed with Gradle 9.8.0. It installed the Maven reactor into an isolated repository,
  ran the complete standalone Gradle `check`, published and validated the implementation and marker publications, and
  built a clean external consumer through the published marker.
- The standalone Gradle build completed with configuration cache enabled; its test/check phase took 7m44s. The external
  consumer completed in 1m30s.
- Corresponding Maven and Gradle example OpenAPI documents, generated Kotlin sources, and WebMVC registration resources
  were compared directly and matched byte-for-byte. Maven's extra filtered OpenAPI execution and each host's ownership
  manifest/layout were excluded because they are intentionally not equivalent inputs.
- The Gradle problems report contained only the known `Configuration.setVisible` deprecation emitted by Dokka, not by
  tapik production code.
