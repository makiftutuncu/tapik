# Repository Guidelines

## Intent

tapik defines type-safe HTTP APIs as ordinary Kotlin values. Preserve complete structural information in inferred
types and favor compile-time feedback. OpenAPI is a generated target, never the source of truth.

## Development

- Use Java 25, the Maven Wrapper, Kotlin 2.4+, and Kotest.
- Run `./mvnw verify` for the complete build.
- Write or update the relevant file under `specification/` before changing public behavior.
- Follow red-green-refactor: add a failing Kotest specification, implement the smallest coherent behavior, then
  refactor with the suite green.
- Keep public packages under `dev.akif.tapik`; integration packages use `dev.akif.tapik.format.<integration>`.
- Always write the project name as lowercase `tapik` in public prose, KDoc, and diagnostics.
- Preserve declaration order in all ordered contract structures. Tags intentionally use set semantics.
- Public APIs require KDoc once they move beyond an exploratory spike.

## Architecture constraints

- Do not discover endpoints by scanning packages or reflecting over arbitrary user classes.
- Do not duplicate endpoint definitions into a parallel metadata model.
- Keep contract definitions as immutable, arbitrarily composable Kotlin values.
- Keep target-specific compatibility checks in targets while exposing neutral capabilities in core types where useful.
- Do not add another module until a specification requires an independently consumable artifact.
