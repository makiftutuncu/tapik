# Validation specification

tapik provides feedback at the earliest layer capable of expressing a rule.

## Kotlin compilation failures

- More than eight elements in a fixed-arity contract structure
- A second request `.input(...)`
- Optional path variables
- Output construction in an invalid order
- Multiple real bodies with different declared Kotlin types
- Replacing an existing endpoint component through an append-only operation

## Contract-construction failures

- Duplicate path or query parameter names within their respective locations
- Duplicate header names, compared case-insensitively
- Duplicate media types within one body collection
- More than one `noBody` alternative
- Overlapping output status matchers over the complete `100..599` domain
- Duplicate qualified endpoint IDs

Matcher overlap is checked over the complete valid HTTP status domain. Custom predicates must be pure and
deterministic; a predicate that throws produces a contextual construction failure.

## Target compatibility failures

- A required codec or schema cannot be derived
- A runtime-only status predicate lacks a representation understood by the target
- A serialization shape cannot be represented faithfully
- A target cannot map an otherwise valid tapik feature

A target requests schemas only when it needs them. An unavailable schema does not invalidate a contract for a target
that does not consume schemas.
