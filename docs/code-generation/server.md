# Spring WebMVC Controllers

The `spring-webmvc` generator turns Tapik endpoints into Spring MVC controller contracts. Implement the generated interfaces to keep routing, validation, and response typing aligned with the DSL.

## Generated Structure

- **Location**: `build/generated/sources/tapik/main/kotlin/<package>/<SourceFile>Controller.kt`
- **Interface name**: `<SourceFile>Controller`
- **Annotations**: Each method is decorated with the appropriate `@GetMapping`, `@PostMapping`, … annotations derived from the endpoint metadata, including `path`, `consumes`, and `produces` attributes.
- **Parameters**: Path variables receive `@SpringPathVariable`, query parameters use `@RequestParam`, headers use `@RequestHeader`, and bodies use `@RequestBody`. Default values defined in the DSL are carried into the annotation attributes.
- **Return type**: Methods return Tapik `ResponseX` or `ResponseWithoutBodyX` types. When multiple outputs exist, the return type is `OneOfK<...>` containing those responses.

Example excerpt:

```kotlin
interface ProductEndpointsController {
    /**
     * Fetch product details.
     */
    @GetMapping(
        path = ["/products/{productId}"],
        produces = ["application/json"]
    )
    fun getProduct(
        @SpringPathVariable("productId") productId: UUID,
        @RequestParam(name = "locale", required = false, defaultValue = "en-US") locale: String,
        @RequestHeader(name = "X-Request-Id") xRequestId: UUID
    ): Response0<ProductView>
}
```

## Implementing Controllers

Wire an implementation in your Spring application:

```kotlin
@Component
class ProductHandler : ProductEndpointsController {
    override fun getProduct(
        productId: UUID,
        locale: String,
        xRequestId: UUID
    ): Response0<ProductView> {
        // fetch domain model and map it into the Tapik response type
        val product = service.lookup(productId, locale)
        return Response0(Status.OK, product)
    }
}
```

The return value captures the HTTP status, headers, and body exactly as you modelled them in the DSL. Use helper builders from `dev.akif.tapik` (for example, `Response0`, `ResponseWithoutBody1`, `OneOf2.Option1`) to produce the response variant that matches the status you intend to emit.

## Rendering Spring Responses

Create a lightweight HTTP adapter that converts the Tapik response to a Spring `ResponseEntity` using the extensions in `TapikResponseEntity.kt`:

```kotlin
@RestController
class ProductController(
    private val handler: ProductHandler
) {
    @GetMapping("/products/{productId}")
    fun getProduct(
        productId: UUID,
        locale: String,
        xRequestId: UUID
    ): ResponseEntity<Any?> =
        ProductEndpoints.getProduct.toResponseEntity(
            handler.getProduct(productId, locale, xRequestId)
        )
}
```

- `toResponseEntity(Response<*>)` handles single-output endpoints.
- `toResponseEntity(OneOf)` handles multi-output unions.
- Status and media-type conversions rely on `Status.toHttpStatus()` and `MediaType.toSpringMediaType()` helpers bundled with the generator.

## Status and Header Validation

`toResponseEntity` enforces that your implementation returns the declared number of headers and selects the matching output variant for `OneOf` unions. If a mismatch occurs (for example, returning `Option2` while only one output is defined), the helper throws an error pointing at the offending endpoint.

## Tips for Server Implementations

1. **Reuse DSL metadata**: The generated interface references the `HttpEndpoint` instances, so you can lean on `.outputs` or `.parameters` inside your implementation for additional logic (for example, building links).
2. **Combine with Spring validation**: Annotate generated parameters with additional validation annotations (e.g., `@Valid`, `@NotBlank`) in partial classes or wrapper controllers if you need extra constraints.
3. **Return domain models**: The response builders accept your domain types. Keep encoding logic inside the DSL via codecs so controller implementations stay small.
4. **Unit-test with Tapik types**: Write tests against the generated interface contracts to verify that every response path returns the appropriate `Response*` wrapper. Pair with the `toResponseEntity` helper for end-to-end tests.

## Customising Routing

The generator chooses method-level annotations based on the HTTP verb. For uncommon verbs (such as `OPTIONS` or `TRACE`), it emits `@RequestMapping(method = [RequestMethod.VERB], path = [...])`. If you need additional attributes (for example, security annotations or custom `@CrossOrigin` settings), declare them in your concrete controller implementation—interfaces are kept lean on purpose.
