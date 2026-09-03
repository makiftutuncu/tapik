# Documentation specification

## Purpose

`tapik.akif.dev` is tapik's user documentation, not only a project landing page or generated Kotlin reference. It must
help a prospective user understand the product, take a working first step, learn the DSL and generation workflow, and
diagnose problems without reading tapik's implementation or internal specifications.

The documentation has three complementary surfaces:

- a concise, unversioned landing page introducing tapik and directing readers to the appropriate documentation;
- versioned Antora documentation for user journeys, concepts, guides, configuration, and troubleshooting;
- a generated Dokka reference for tapik's public Kotlin API.

Dokka complements the narrative documentation. It does not replace quickstarts, explanations, integration guides, or
reference pages that connect multiple public types into a working workflow.

The project name is always written as lowercase `tapik` in public prose. This includes website pages, repository
documentation, KDoc, generated plugin descriptions, and user-facing diagnostics; Kotlin identifiers keep their normal
declaration spelling.

## Site and URL structure

The public site preserves the established Antora component path and uses these stable locations:

| Location | Content |
| --- | --- |
| `/` | Unversioned tapik landing page |
| `/docs/` | Latest stable narrative documentation |
| `/docs/api/` | Latest stable unified Kotlin API reference |
| `/docs/<version>/` | Narrative documentation for an older release |
| `/docs/<version>/api/` | Immutable Kotlin API reference for that release |

The latest stable version may omit its version segment, but its API output must also be archived under its explicit
version before a later release replaces it. Existing public documentation URLs should remain valid where practical.
Documentation and API reference navigation must link back to each other, and the landing page must link to the real
quickstart and documentation rather than using the repository README as the primary manual.

Historical release tags used `tapik` as their Antora component name. Tagged website builds normalize that legacy name
to `docs` before content classification, without modifying the tags, so every release belongs to one version lineage
and appears in the same version selector under `/docs/`. The publishing process preserves already deployed `/tapik/`
paths for compatibility, but newly rendered historical pages use the canonical `/docs/<version>/` locations. Releases
before 0.6 did not archive a versioned API reference, so their header links to the latest API reference instead of a
nonexistent historical path.

## Documentation toolchain

Antora is the narrative documentation generator. It retains the existing AsciiDoc content model, search, navigation,
breadcrumbs, page table of contents, pagination, edit links, and Git-reference-based versioning. Its UI must use pinned,
reproducible inputs and may be customized to carry the landing page's visual language across the documentation portal.

Antora Collector imports example source from elsewhere in each documentation version's repository worktree. Kotlin,
XML, and command examples shown in documentation should therefore come from tested `example-contract` and
`example-application` files or from smaller compiling documentation fixtures rather than copied code blocks.
Substantial executable Kotlin and Maven fragments use tagged or complete Collector imports. Documentation validation
rejects multiline Kotlin or XML source blocks that duplicate executable source instead of importing it; short type
signatures and other non-executable notation may remain inline.

Dokka generates the Kotlin API reference. Because its Maven plugin does not aggregate Maven modules natively, the
website build may present all production source roots as one documentation module with a complete installed-artifact
classpath. The reference must include every production package and exclude examples, fixtures, and test sources.
Public declarations should link to their matching repository source at the documented release when supported.

The unified reference is generated at `/docs/api/` inside the same staged site Antora uses. Antora navigation links to
the reference, and every Dokka HTML page provides a version-relative link back to the matching narrative documentation
root. Local and CI preview workflows fail when the unified reference has not been built, rather than silently
publishing an Antora site with a dead API link. Navigation integration is idempotent so rebuilding Antora does not
duplicate links or assets.

The Maven `release` profile continues generating standard Dokka HTML independently for each published module and
packaging it in that module's `javadoc` classifier. Website aggregation must not change or replace those Maven Central
artifacts. A documentation-only Gradle build is not part of the architecture.

## Information architecture

The narrative documentation is organized around user goals rather than tapik's internal modules:

1. Start here: product overview, choose a goal, quickstart, and runnable example.
2. Define contracts: APIs, endpoints, URIs, inputs, outputs, formats, schemas, documentation, tags, and composition.
3. Generate documentation: shared documentation-generation concepts followed by OpenAPI and future documentation
   integrations.
4. Generate clients: shared generated-client concepts followed by Spring RestClient and future client integrations.
5. Implement servers: shared generated-server concepts followed by Spring WebMVC and future server integrations.
6. Reference: generation model, registry, artifacts and BOM, complete Maven configuration, lifecycle phases, target options, generated locations,
   naming, diagnostics, and troubleshooting.
7. Understand tapik: type-level safety, compile-time boundaries, Kotlin as the source of truth, and target architecture.
8. Contribute: repository layout, build workflow, documentation workflow, release process, and migration guidance.

The 0.5 documentation on `main` is an information-architecture reference only. Pages must be rewritten and verified
against the 0.6 Maven plugin, compiler-generated registry, DSL, formats, targets, generated code, and runnable examples.

## Quickstart requirements

The primary quickstart must lead with the shortest working OpenAPI workflow and explain both supported Maven lifecycle
modes using the runnable examples as executable source:

- In same-module mode, the contract module applies the tapik Kotlin compiler plugin and runs generation after its
  classes exist. The tapik Maven plugin's default `process-classes` phase is the safe lifecycle binding; invoking only
  `compile` cannot generate from the registry produced by that same compilation.
- In compiled-contract mode, a consumer depends on an already compiled contract artifact. Generation may therefore
  run in `generate-sources`, before the consumer's Kotlin compilation, so generated client and server source can be
  compiled as ordinary application source.

The guide must give exact Maven commands, lifecycle expectations, and generated OpenAPI and Kotlin source locations.
Its Kotlin and Maven fragments must be collected from the tested `example-contract` and `example-application` source
files rather than maintained as independent copies in the documentation.

## Core DSL guide requirements

The contract guide is divided into focused pages for API and endpoint identity, URI parameters, request inputs,
response outputs, formats and schemas, and documentation and composition. It teaches the public construction grammar
before exposing the underlying generic types, while still explaining which structural information those types retain
and which invalid states Kotlin prevents.

Examples must compile in the normal Maven reactor. Pages should collect complete or tagged fragments from the shared
library fixture and its documentation-specific test sources so endpoint syntax, inferred types, ordering, defaults,
presence, media alternatives, status matching, and composition cannot silently diverge from the implementation.

## Generation reference requirements

Generation documentation must separate tapik's host-neutral pipeline from Maven's responsibility as one build-tool
host. It explains how compiler-generated registries, exact API selection, target discovery, target-owned validation,
generated artifacts, and host materialization fit together without implying that a target depends on Maven.

The compiler registry reference states which `Api` classes and objects are eligible, which endpoint and inclusion
properties must be public, what compilation produces, and how incremental compilation synchronizes registry entries.
It also states what registry generation deliberately does not do: execute endpoint expressions, scan packages,
reflect over arbitrary user classes, or duplicate contracts into a metadata model.

The Maven reference is the canonical catalogue for compiler activation and the `generate` goal. It documents every
host parameter, the same-module and compiled-contract lifecycle matrix, exact include/exclude behavior, built-in target
IDs and options, naming defaults, output locations, generated source/resource registration, and the meaning of direct
goal invocation. Configuration fragments come from reactor-built example POMs wherever a runnable example exists.

Failure behavior is documented by stage, including compiler eligibility diagnostics, version alignment, registry and
target discovery, API selection, target compatibility, conflicting output ownership, and late Kotlin compilation.
Successful executions log every generated path. Regeneration removes stale paths owned by the same execution while
preserving other executions' and user-owned files, and a failed execution leaves its last successful artifacts intact.

## Integration guide requirements

Each integration page is a task-oriented path from a compiled tapik contract to a usable result. OpenAPI documents the
same-module and compiled-contract choices, generated document ownership, schema inputs, per-API output, configuration,
and validation. Source integrations lead with compiled-contract generation during `generate-sources` so application
source can compile against the generated types.

The Spring RestClient guide distinguishes the generated client interface and wire implementation from the user-owned
Spring component, API instance, transport, base URL, authentication, and other runtime policy. The Spring WebMVC guide
distinguishes the generated pure server interface and internal controller from user-owned handler behavior, and
explains both Spring Boot auto-configuration and explicit plain Spring registration, including handler bean selection.

Runnable configuration and application snippets are collected from the reactor-built example modules. Every guide
links to the shared Maven and target-option references rather than silently redefining lifecycle, selection, naming,
output synchronization, or diagnostic rules.

## Preview and publication

Pull requests build the current worktree documentation and unified API reference as a validation preview alongside
the tagged documentation history. This exercises the version selector and canonical historical paths, but does not
publish the worktree as the latest stable manual. Local development must provide one documented preview workflow that
rebuilds the landing page, Antora content, imported examples, and Dokka output.

Stable documentation is published from a release ref after the release build succeeds. Publishing updates the
unversioned latest URLs, archives that release's narrative and API output under its explicit version, and preserves
older immutable versions. A push to `main` alone must not replace the latest stable documentation with unreleased
content.

A documentation release has one version identity. Its Git tag is `v` followed by the root Maven project version and
must equal the version declared by `docs/antora.yml`; publication fails before deployment when these values differ.
The release build renders tagged Antora history, the current release at both `/docs/` and `/docs/<version>/`, and the
integrated Dokka reference at both matching `api/` locations. Navigation from a versioned narrative page selects that
version's API reference, and navigation from either API copy returns to the narrative root beside it.

Only a published, non-prerelease GitHub release, or an explicit manual retry naming the latest stable release tag, may
deploy the public site. Release publication checks out that exact tag with complete tag history. Deployment may
replace the unversioned landing page and latest documentation, but it preserves previously published explicit-version
API directories; rebuilding a later release must not mutate an older version's generated reference. Pull requests and
pushes to `main` run the worktree preview as validation and never deploy its output.

## Quality requirements

Documentation validation must fail for unresolved Antora cross-references, broken internal links, missing expected
landing, guide, or API entry points, and source examples that can no longer be collected. The runnable examples remain
part of the normal reactor and are the executable proof behind the primary user workflows.

Rendered smoke tests cover navigation from the landing page to the quickstart and API reference, navigation back from
the reference, versioned paths, search availability, and usable desktop and mobile layouts. Documentation coverage is
grown with public behavior: stable public APIs require KDoc, and supported user workflows require narrative guidance.

Every Antora build treats warnings as failures, including unresolved cross-references, missing include files, and
missing source snippet tags. The staged-site check parses generated HTML and validates same-site page links, fragment
anchors, stylesheets, scripts, and images without making external HTTP requests. It requires the landing page, current
narrative source pages, unified API entry point, and supported historical entry points; obsolete staged pages must not
mask missing output from the current build.

Desktop and mobile Chromium smoke tests run against a loopback server serving only the staged site. They exercise the
landing-to-quickstart journey, API-reference round trip, version switching, search results, and responsive navigation,
and reject horizontal page overflow or failed local resource requests. Browser failures retain screenshots and traces
for diagnosis. CI runs these checks before accepting a preview or publishing a release; local users can run the static
checks without installing Chromium and opt into the same browser smoke suite separately.
