# Validation specification

tapik provides feedback at the earliest layer capable of expressing a rule.

## Kotlin compilation failures

- More than eight elements in a fixed-arity endpoint structure
- A second request `.input(...)`
- Optional path variables
- Output construction in an invalid order
- Multiple real bodies with different declared Kotlin types
- Replacing an existing endpoint component through an append-only operation

## Endpoint-construction failures

- Duplicate path or query parameter names within their respective locations
- Duplicate header names, compared case-insensitively
- Duplicate media types within one body collection
- More than one `noBody` alternative
- Overlapping output status matchers over the complete `100..599` domain
- Duplicate qualified endpoint IDs

Matcher overlap is checked over the complete valid HTTP status domain. Custom predicates must be pure and
deterministic; a predicate that throws produces a contextual construction failure.

## Format-creation failures

- A format integration cannot derive a required schema for its wire type

Schema derivation happens when the format is created, not when a target later reads it. Supplying an explicit schema
lets the caller describe a wire shape the integration cannot derive; the caller is responsible for keeping it aligned
with the codec.

## Target compatibility failures

- A runtime-only status predicate lacks a representation understood by the target
- A serialization shape cannot be represented faithfully
- A schema feature required by the target cannot be represented
- A target cannot map an otherwise valid tapik feature

A target validates schema features only when it needs schema information. A schema form that one target cannot
represent does not invalidate the endpoint for a target that ignores schemas.
