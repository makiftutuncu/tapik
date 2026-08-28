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

## Schema components

Every named Tapik schema becomes an OpenAPI schema component and every reference follows the same component-naming
policy. The default `OpenApiComponentNaming.Simple` policy keeps the final dot-separated part of a schema name.
`OpenApiComponentNaming.Qualified` keeps the provided name unchanged, and callers may supply their own policy.

Schemas that resolve to the same component name and the same OpenAPI shape share one component. Different shapes
resolving to the same name fail generation instead of allowing one definition to overwrite another.

## Rendering

`OpenApiDocument.toJson()` produces pretty, deterministic JSON. Passing `pretty = false` produces compact JSON with
the same content and ordering. `OpenApiDocument.toYaml()` produces deterministic block-style YAML containing the same
document structure and scalar values. The complete library fixture JSON document is maintained as a golden test
resource so any observable document change requires an explicit expected-document update; focused renderer tests keep
the JSON and YAML representations semantically aligned.

The generation target accepts `format` values `yaml` and `json`, defaulting to `yaml`. YAML artifacts use media type
`application/yaml` and default to `{api}.openapi.yml`; JSON artifacts use `application/json` and default to
`{api}.openapi.json`. An explicit `output` template replaces either format-specific default without changing the
selected renderer.
