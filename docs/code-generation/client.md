# Spring RestClient Clients

The `spring-restclient` generator produces Kotlin interfaces that call HTTP endpoints through the Spring Framework `RestClient` API. Each generated interface groups endpoints by the source file that defined them and extends `dev.akif.tapik.spring.restclient.RestClientBasedClient`.

## Generated Structure

- **Location**: `build/generated/sources/tapik/main/kotlin/<package>/<SourceFile>Client.kt`
- **Interface name**: `<SourceFile>Client`
- **Base interface**: `RestClientBasedClient`, which exposes:
  - `val restClient: RestClient`
  - `val interpreter: RestClientInterpreter` (lazy wrapper around `restClient`)
- **Method signature**: one function per endpoint property, using descriptive parameter names derived from the DSL.

Example excerpt:

```kotlin
interface ProductEndpointsClient : RestClientBasedClient {
    /**
     * Fetch product details.
     */
    fun getProduct(
        productId: UUID,
        locale: String = "en-US",
        xRequestId: UUID
    ): Response0<ProductView> {
        val responseEntity = interpreter.send(
            method = ProductEndpoints.getProduct.method,
            uri = ProductEndpoints.getProduct.toURI(productId, locale),
            inputHeaders = ProductEndpoints.getProduct.input.encodeInputHeaders(xRequestId),
            inputBodyContentType = ProductEndpoints.getProduct.input.body.mediaType,
            inputBody = ByteArray(0),
            outputs = ProductEndpoints.getProduct.outputs.toList()
        )

        val status = responseEntity.statusCode.toStatus()
        val bodyBytes = responseEntity.body ?: ByteArray(0)

        val decodedBody = ProductEndpoints.getProduct.outputs.item1.body.codec.decode(bodyBytes)
            .getOrElse { error(it.joinToString(": ")) }

        return Response0(status, decodedBody)
    }
}
```

!!! note
    Names and types are inferred from your endpoint declarations: path/query parameters become function parameters, headers with codecs translate into arguments, and bodies map to encoded values.

## Return Types

| Endpoint outputs | Generated return type |
| --- | --- |
| Single body without headers | `Response0<T>` |
| Single body with headers | `ResponseN<T, Header1, …>` (where `N` matches the header arity) |
| No body, headers only | `ResponseWithoutBodyN<Header1, …>` |
| Multiple outputs | `OneOfK<…>` wrapping the corresponding `ResponseX` type |

You can pattern match on `OneOfK` (provided by tapik’s `core` module) to branch on status-based responses.

## Header and Body Handling

- Headers are encoded via the `Input.encodeInputHeaders` helpers from `core`; responses use the `decodeHeadersX` utilities to validate and decode header values.
- Bodies use the codec attached to the `Body` definition (for example, Jackson-based JSON codecs). Any `CodecFailure` surfaces as an `IllegalStateException` in generated code so mismatches fail fast.
- `RestClientInterpreter` validates response status codes and media types before the generator attempts to decode bodies, surfacing detailed `RestClientResponseException` messages when mismatches occur.

## Instantiating Clients

Create a Spring-managed implementation by delegating to the generated interface:

```kotlin
@Component
class HttpProductClient(
    override val restClient: RestClient
) : ProductEndpointsClient
```

`RestClientBasedClient` provides `interpreter` automatically, so implementations only supply the `RestClient`. You may inject a configured `RestClient.Builder` or reuse the application-wide client.

## Testing Tips

1. Drive endpoint behaviour through unit tests on the DSL before running code generation.
2. Use Spring's `MockRestServiceServer` to verify generated clients: intercept outgoing requests, assert the expected path/headers/body, and feed back fixtures that satisfy your endpoint outputs.
3. If you customise codecs, add integration tests to ensure `RestClientInterpreter` can successfully decode the wire format.

## Customisation Hooks

- Provide your own `RestClient` (set timeouts, interceptors, authentication) while reusing generated code.
- Override functions in the generated interface by writing extension functions or wrapping the generated interface inside a higher-level service.
- Add new generators for other HTTP client stacks by following the steps in [Code Generation Overview](index.md#extending-tapik); the Spring client serves as a realistic template.
