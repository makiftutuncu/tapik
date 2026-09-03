# OpenAPI specification

## Interpretation

`OpenApi.from(api, ...)` interprets the ready endpoints of an explicitly supplied, compiled `Api` value. It returns a
public OpenAPI 3.2 document model without scanning the classpath, using reflection, or copying endpoints into a
target-neutral metadata model.

Endpoint, parameter, response, media-type, and schema discovery order is preserved. Request headers and response
headers are translated literally from the tapik contract; the interpreter does not silently discard names that
OpenAPI tooling might treat specially.

Repeated query parameters use an array schema with `style: form` and `explode: true`, representing one query name
with repeated wire values. Scalar query parameters omit these fields and use the OpenAPI defaults.

Default and fixed parameter values are encoded through their format before becoming JSON Schema `default` and
`const` values. The encoded representation is interpreted according to the format's schema, so transformed and
value-class formats, custom enum codecs, nullable schemas, and repeated formats retain their actual wire values and
JSON types.
JSON and YAML rendering preserve arbitrary-precision integer and decimal values without conversion through `Double`.
Numeric-looking strings remain strings, and non-finite numeric literals are rejected.

Exact status matchers produce one response-code entry. Status sets produce one entry per concrete status in their
declared order. A range covering one complete status-code hundred uses OpenAPI's `nXX` response key; other ranges are
expanded into concrete response-code entries. Described custom predicates fail generation with the endpoint ID and
matcher description because executable Kotlin predicates cannot be represented faithfully in an OpenAPI document.

OpenAPI path templates cannot distinguish a single path variable from tapik's required remaining-segments wildcard.
An endpoint containing `RemainingPath` therefore fails generation with its endpoint ID and wildcard name instead of
silently publishing a weaker ordinary path parameter.

Path, query, and request-header parameters expose their own descriptions and deprecation flags. Response headers use
the same header documentation. Request-body descriptions are emitted on the Request Body Object rather than its media
representations. An explicit response-alternative description replaces the standard HTTP status description; an
undocumented response retains that standard fallback.

## Schema components

Every named tapik schema becomes an OpenAPI schema component and every reference follows the same component-naming
policy. The default `OpenApiComponentNaming.Simple` policy keeps the final dot-separated part of a schema name.
`OpenApiComponentNaming.Qualified` keeps the provided name unchanged, and callers may supply their own policy.

Schemas that resolve to the same component name and the same OpenAPI shape share one component. Different shapes
resolving to the same name fail generation instead of allowing one definition to overwrite another.

A named reference with a distinct component name creates an alias component containing `$ref`; it does not lose its
name. If both names normalize to the same component, the reference addresses that component directly. Alias targets
must resolve, and aliases participate in the same component-conflict checks as structural schemas.

tapik union schemas become JSON Schema `oneOf` arrays in declaration order. Their discriminators become OpenAPI
Discriminator Objects with `propertyName`, optional `mapping`, and optional `defaultMapping`. Explicit and default
mapping references pass through the configured component-naming policy and participate in unresolved-reference
validation.

OpenAPI requires discriminator alternatives to be addressable schemas. Generation therefore rejects a discriminated
union containing an inline alternative, or a discriminator mapping whose target is not one of the union alternatives.
An undiscriminated union may freely contain inline or referenced alternatives.

When a discriminator has no `defaultMapping`, all alternatives must require its property. This check runs after
references are resolved, including aliases and forward references. An optional discriminator needs `defaultMapping`
under OpenAPI 3.2; generation fails rather than emitting a discriminator with undefined missing-property behavior.

## Rendering

`OpenApiDocument.toJson()` produces pretty, deterministic JSON. Passing `pretty = false` produces compact JSON with
the same content and ordering. `OpenApiDocument.toYaml()` produces deterministic block-style YAML containing the same
document structure and scalar values. The complete Books fixture JSON document is maintained as a golden test resource
so any observable document change requires an explicit expected-document update. The Authors and Rentals fixtures are
also converted by the target suite, while focused renderer tests keep the JSON and YAML representations semantically
aligned.

A second complete golden document covers the newer DSL concepts together: documented parameters and responses,
optional/multiple body representations, status sets and ranges, QUERY and CONNECT, every neutral schema kind, named
aliases, recursive references, and discriminator mappings. Both golden documents must match parsed YAML and compact
JSON as well as exact pretty JSON. Focused tests exercise all standard methods and reject unsupported mappings.

The generation target accepts `format` values `yaml` and `json`, defaulting to `yaml`. YAML artifacts use media type
`application/yaml` and default to `{api}.openapi.yml`; JSON artifacts use `application/json` and default to
`{api}.openapi.json`. An explicit `output` template replaces either format-specific default without changing the
selected renderer.

## OpenAPI 3.2 coverage audit (0.6.0)

The baseline is the [OpenAPI 3.2.0 specification](https://spec.openapis.org/oas/v3.2.0.html). This audit covers the
existing Kotlin DSL, not every feature the OpenAPI format can express. The public document model is a supported
subset, not an arbitrary OpenAPI editor or an independent document validator.

| Contract concept | Generated representation | Regression coverage |
| --- | --- | --- |
| Identity, documentation, tags, composition | Info, operation IDs, summaries, descriptions, sorted tag sets | Both goldens; composition test |
| Methods and paths | Fixed method fields including `query`; `additionalOperations.CONNECT`; required template variables | Coverage golden; every `Method` entry; duplicate/template rejection tests |
| Parameter presence and wire formats | `required`, schema `default`/`const`, repeated query arrays with form/explode | Both goldens; transformed-format tests |
| Parameter and header documentation | Description and deprecated flags at their owning level | Coverage golden |
| Request/response bodies | Ordered media maps; request `required` reflects `noBody`; empty responses omit content | Both goldens |
| Status alternatives | Exact/set entries, full-hundred wildcard keys, expanded partial ranges | Coverage golden; custom predicate rejection |
| Scalar, enum, array, object, map, nullable schemas | Corresponding JSON Schema keywords, property requirements and deprecation | Both goldens |
| References and unions | Components, aliases, recursive refs, `oneOf`, discriminators and default mappings | Coverage golden; alias, resolution, naming, discriminator rejection tests |
| Renderers | One shared JSON tree rendered as pretty/compact JSON or block YAML | Both complete goldens; scalar renderer tests |

Deliberate boundaries:

- Servers, security schemes/requirements, callbacks, webhooks, links, examples, rich tag metadata, and specification
  extensions have no DSL representation yet. They are not inferred from strings, headers, or arbitrary user classes.
- Multipart/form encodings, streaming `itemSchema`, and XML-specific schema metadata are not modeled. A format's
  supplied schema is translated as-is; inspecting a codec cannot establish its compatibility with these features.
- OpenAPI response content has no request-style `required` flag. A response with a body and `noBody` alternative
  documents its possible media representations; it does not invent a nullable payload to mean absent bytes.
- Header names remain literal by prior design. OpenAPI consumers may ignore request `Accept`, `Content-Type`, and
  `Authorization` parameters and response `Content-Type` headers; these are not a substitute for security schemes
  or body media definitions.
- Discriminators are selection hints, not extra validation constraints. Schemas/codecs remain responsible for
  making `oneOf` alternatives mutually exclusive and matching their wire values. The target checks references and
  missing-property default mappings, not arbitrary schema satisfiability.

Future DSL additions must update this inventory and the full golden document before being claimed as supported.
