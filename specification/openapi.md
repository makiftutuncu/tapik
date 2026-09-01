# OpenAPI specification

## Interpretation

`OpenApi.from(api, ...)` interprets the ready endpoints of an explicitly supplied, compiled `Api` value. It returns a
public OpenAPI 3.2 document model without scanning the classpath, using reflection, or copying endpoints into a
target-neutral metadata model.

Endpoint, parameter, response, media-type, and schema discovery order is preserved. Request headers and response
headers are translated literally from the Tapik contract; the interpreter does not silently discard names that
OpenAPI tooling might treat specially.

Repeated query parameters use an array schema with `style: form` and `explode: true`, representing one query name
with repeated wire values. Scalar query parameters omit these fields and use the OpenAPI defaults.

Default and fixed parameter values are encoded through their format before becoming JSON Schema `default` and
`const` values. The encoded representation is interpreted according to the format's schema, so transformed and
value-class formats, custom enum codecs, nullable schemas, and repeated formats retain their actual wire values and
JSON types.

Exact status matchers produce one response-code entry. Status sets produce one entry per concrete status in their
declared order. A range covering one complete status-code hundred uses OpenAPI's `nXX` response key; other ranges are
expanded into concrete response-code entries. Described custom predicates fail generation with the endpoint ID and
matcher description because executable Kotlin predicates cannot be represented faithfully in an OpenAPI document.

OpenAPI path templates cannot distinguish a single path variable from Tapik's required remaining-segments wildcard.
An endpoint containing `RemainingPath` therefore fails generation with its endpoint ID and wildcard name instead of
silently publishing a weaker ordinary path parameter.

Path, query, and request-header parameters expose their own descriptions and deprecation flags. Response headers use
the same header documentation. Request-body descriptions are emitted on the Request Body Object rather than its media
representations. An explicit response-alternative description replaces the standard HTTP status description; an
undocumented response retains that standard fallback.

## Schema components

Every named Tapik schema becomes an OpenAPI schema component and every reference follows the same component-naming
policy. The default `OpenApiComponentNaming.Simple` policy keeps the final dot-separated part of a schema name.
`OpenApiComponentNaming.Qualified` keeps the provided name unchanged, and callers may supply their own policy.

Schemas that resolve to the same component name and the same OpenAPI shape share one component. Different shapes
resolving to the same name fail generation instead of allowing one definition to overwrite another.

Tapik union schemas become JSON Schema `oneOf` arrays in declaration order. Their discriminators become OpenAPI
Discriminator Objects with `propertyName`, optional `mapping`, and optional `defaultMapping`. Explicit and default
mapping references pass through the configured component-naming policy and participate in unresolved-reference
validation.

OpenAPI requires discriminator alternatives to be addressable schemas. Generation therefore rejects a discriminated
union containing an inline alternative, or a discriminator mapping whose target is not one of the union alternatives.
An undiscriminated union may freely contain inline or referenced alternatives.

## Rendering

`OpenApiDocument.toJson()` produces pretty, deterministic JSON. Passing `pretty = false` produces compact JSON with
the same content and ordering. `OpenApiDocument.toYaml()` produces deterministic block-style YAML containing the same
document structure and scalar values. The complete Books fixture JSON document is maintained as a golden test resource
so any observable document change requires an explicit expected-document update. The Authors and Rentals fixtures are
also converted by the target suite, while focused renderer tests keep the JSON and YAML representations semantically
aligned.

The generation target accepts `format` values `yaml` and `json`, defaulting to `yaml`. YAML artifacts use media type
`application/yaml` and default to `{api}.openapi.yml`; JSON artifacts use `application/json` and default to
`{api}.openapi.json`. An explicit `output` template replaces either format-specific default without changing the
selected renderer.
