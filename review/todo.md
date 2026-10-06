# tapik rewrite release-hardening TODO

The implementation review found no remaining code-level correctness blocker. These tasks close specification drift and complete the validation needed before treating the rewrite as release-ready. Documentation-only corrections do not require production changes, but all affected checks must remain green.

## P1 — Restore the artifact-ownership contract

- [x] Specify shared generated-artifact ownership consistently.
  - Update `specification/generation.md` so source artifacts may be co-owned only when every execution declares the same nonblank sharing key.
  - State that an explicitly shared source remains until its final owner releases it, while ordinary cross-execution path collisions still fail before changing output or ownership state.
  - Update the late compiled-output rules to permit a new owner only for byte-identical output and to permit an existing owner to refresh a shared class during staggered regeneration.
  - Preserve the guarantees for unowned application files, incompatible shared output, stale cleanup, failure atomicity, and deterministic ownership state.
  - Cross-reference the Spring endpoint-types use case without making the host-neutral generation specification Spring-specific.

- [x] Align public Maven documentation and KDoc with shared ownership.
  - Update `docs/modules/ROOT/pages/reference/maven.adoc` to describe exclusive artifacts, explicitly shared artifacts, and their cleanup behavior.
  - Update `ArtifactWriter.write` KDoc so its collision contract includes the compatible-sharing-key exception.
  - Audit nearby ownership and synchronization prose for obsolete claims that two executions can never own the same path.
  - Ensure the narrative remains consistent with `GeneratedArtifact.sharingKey`, `ArtifactOwnership`, `GeneratedOutputSynchronizer`, and `specification/spring.md`.

## P2 — Bring foundational specifications into the present

- [x] Update the product specification to describe the implemented Maven adapter.
  - Replace the statement that a later Maven plugin will wrap OpenAPI generation with the current host-neutral target and Maven-adapter architecture.
  - Keep future Gradle and command-line hosts framed as consumers of the same registry and generation boundaries.

- [x] Document the current compile-failure testing approach.
  - Replace the deferred-harness note in `specification/development.md` with the compiler-based approach now used by DSL and compiler-plugin specifications.
  - State which compile-success and compile-failure behaviors belong in this harness and retain ordinary Kotest coverage for runtime behavior.

## P3 — Complete release validation

- [x] Run the complete documentation validation pipeline.
  - Install reactor artifacts needed by the standalone site build.
  - Build the website and unified Dokka API reference through `site/pom.xml`.
  - Run `npm ci`, `npm run docs:preview`, and the Playwright desktop/mobile smoke checks used by CI.
  - Resolve broken cross-references, stale snippets, navigation failures, or KDoc rendering problems before completion.

- [x] Run the unsigned release and external-consumer rehearsal.
  - Execute `./mvnw -Prelease -Dgpg.skip=true -Dcentral.skipPublishing=true -Dtapik.release.rehearsal=true clean install`.
  - Verify production POMs, binaries, source archives, and API documentation archives in the isolated staged repository.
  - Verify that the external consumer rebuilds compiler registries, OpenAPI, shared Spring types, generated client/server sources, and Spring runtime behavior without relying on the repository reactor.
  - Confirm that no `test-` or `example-` artifact is staged for publication.

- [x] Decide and document the 0.6 compatibility posture.
  - Review the remaining intentional limitations: Maven-only hosting, initial target set, late same-module source visibility, and serializer shapes requiring explicit schemas.
  - Decide whether public DSL and generated-source identities are ready to be treated as compatibility contracts.
  - Retain the experimental warning if further breaking iteration is expected; otherwise replace it with an explicit stability and migration policy.

## Completion

- [x] `./mvnw clean verify` passes after the documentation corrections.
- [x] `git diff --check` passes for files changed while completing this checklist.
- [x] No specification, KDoc, or user guide contradicts shared artifact ownership.
- [ ] Documentation validation and the opt-in release rehearsal pass from clean generated state.
