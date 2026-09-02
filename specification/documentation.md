# Documentation specification

## Purpose

`tapik.akif.dev` is Tapik's user documentation, not only a project landing page or generated Kotlin reference. It must
help a prospective user understand the product, take a working first step, learn the DSL and generation workflow, and
diagnose problems without reading Tapik's implementation or internal specifications.

The documentation has three complementary surfaces:

- a concise, unversioned landing page introducing Tapik and directing readers to the appropriate documentation;
- versioned Antora documentation for user journeys, concepts, guides, configuration, and troubleshooting;
- a generated Dokka reference for Tapik's public Kotlin API.

Dokka complements the narrative documentation. It does not replace quickstarts, explanations, integration guides, or
reference pages that connect multiple public types into a working workflow.

## Site and URL structure

The public site preserves the established Antora component path and uses these stable locations:

| Location | Content |
| --- | --- |
| `/` | Unversioned Tapik landing page |
| `/tapik/` | Latest stable narrative documentation |
| `/tapik/api/` | Latest stable unified Kotlin API reference |
| `/tapik/<version>/` | Narrative documentation for an older release |
| `/tapik/<version>/api/` | Immutable Kotlin API reference for that release |

The latest stable version may omit its version segment, but its API output must also be archived under its explicit
version before a later release replaces it. Existing public documentation URLs should remain valid where practical.
Documentation and API reference navigation must link back to each other, and the landing page must link to the real
quickstart and documentation rather than using the repository README as the primary manual.

## Documentation toolchain

Antora is the narrative documentation generator. It retains the existing AsciiDoc content model, search, navigation,
breadcrumbs, page table of contents, pagination, edit links, and Git-reference-based versioning. Its UI must use pinned,
reproducible inputs and may be customized to carry the landing page's visual language across the documentation portal.

Antora Collector imports example source from elsewhere in each documentation version's repository worktree. Kotlin,
XML, and command examples shown in documentation should therefore come from tested `example-contract` and
`example-application` files or from smaller compiling documentation fixtures rather than copied code blocks.

Dokka generates the Kotlin API reference. Because its Maven plugin does not aggregate Maven modules natively, the
website build may present all production source roots as one documentation module with a complete installed-artifact
classpath. The reference must include every production package and exclude examples, fixtures, and test sources.
Public declarations should link to their matching repository source at the documented release when supported.

The Maven `release` profile continues generating standard Dokka HTML independently for each published module and
packaging it in that module's `javadoc` classifier. Website aggregation must not change or replace those Maven Central
artifacts. A documentation-only Gradle build is not part of the architecture.

## Information architecture

The narrative documentation is organized around user goals rather than Tapik's internal modules:

1. Start here: product overview, choose a goal, quickstart, and runnable example.
2. Define contracts: APIs, endpoints, URIs, inputs, outputs, formats, schemas, documentation, tags, and composition.
3. Generate targets: generation model, same-module and compiled-contract modes, OpenAPI, RestClient, and WebMVC.
4. Reference: artifacts and BOM, complete Maven configuration, lifecycle phases, target options, generated locations,
   naming, diagnostics, and troubleshooting.
5. Understand Tapik: type-level safety, compile-time boundaries, Kotlin as the source of truth, and target architecture.
6. Contribute: repository layout, build workflow, documentation workflow, release process, and migration guidance.

The 0.5 documentation on `main` is an information-architecture reference only. Pages must be rewritten and verified
against the 0.6 Maven plugin, compiler-generated registry, DSL, formats, targets, generated code, and runnable examples.

## Preview and publication

Pull requests build the current worktree documentation and unified API reference as a validation preview. They do not
publish it as the latest stable manual. Local development must provide one documented preview workflow that rebuilds
the landing page, Antora content, imported examples, and Dokka output.

Stable documentation is published from a release ref after the release build succeeds. Publishing updates the
unversioned latest URLs, archives that release's narrative and API output under its explicit version, and preserves
older immutable versions. A push to `main` alone must not replace the latest stable documentation with unreleased
content.

## Quality requirements

Documentation validation must fail for unresolved Antora cross-references, broken internal links, missing expected
landing, guide, or API entry points, and source examples that can no longer be collected. The runnable examples remain
part of the normal reactor and are the executable proof behind the primary user workflows.

Rendered smoke tests cover navigation from the landing page to the quickstart and API reference, navigation back from
the reference, versioned paths, search availability, and usable desktop and mobile layouts. Documentation coverage is
grown with public behavior: stable public APIs require KDoc, and supported user workflows require narrative guidance.
