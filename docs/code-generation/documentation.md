# Markdown Documentation

Tapik ships a Markdown generator (`markdown-docs`) that produces a lightweight API reference from your endpoint definitions. This is useful for sharing REST contracts with stakeholders who do not read the Kotlin source.

## Output Layout

- **Location**: `build/generated/API.md`
- **Grouping**: Endpoints are grouped by package and Kotlin source file. Each group starts with an `#` heading for the file, followed by `##` sections per endpoint.
- **Content**:
  - HTTP method and resolved path.
  - Description and details from the DSL.
  - Tables for parameters, headers, and outputs.
  - Body type descriptions including codec names and media types.

Example excerpt:

```markdown
# ProductEndpoints.kt

## `getProduct`: Fetch product details

`GET /products/{productId}`

Returns localized information when the locale query parameter is supplied.

### URI Parameters
| Name | Type | Position | Required | Default Value |
| --- | --- | --- | --- | --- |
| `productId` | UUID | In Path | true |  |
| `locale` | String | In Query | false | en-US |

### Input Headers
| Name | Type | Value(s) | Required |
| --- | --- | --- | --- |
| `X-Request-Id` | UUID | — | true |

### Outputs
| Description | Headers | Body |
| --- | --- | --- |
| Status: 200 OK | — | `ProductView` named "product" as `application/json` |
| Status: 404 NOT_FOUND | — | `ProblemDetails` named "problem" as `application/json` |
```

## Keeping Documentation Fresh

- The report updates every time `tapikGenerate` runs; check it into version control if you track API changes alongside code.
- Because the generator reads descriptions and details from your DSL, updating KDoc or strings in endpoint declarations immediately reflects in the Markdown file.
- Unknown codecs or missing media types show up verbatim; add KDoc to the DSL elements if you need richer context.

## Customising or Extending

If you need additional formats (HTML, OpenAPI, etc.), use the Markdown generator as a blueprint:

1. Depend on `dev.akif.tapik:common-plugin` to reuse metadata types.
2. Implement `TapikGenerator` and register a new `id`.
3. Traverse `HttpEndpointMetadata` to produce your desired output. You can start from the source at `common-plugin/src/main/kotlin/plugin/MarkdownDocumentationGenerator.kt`.

Remember to add your generator to the Gradle DSL via a nested extension or by manually specifying the `enabledGeneratorIds` set.
