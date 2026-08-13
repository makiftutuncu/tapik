# DSL specification

## API and endpoint identity

Endpoints are declared only inside an `Api` and only through property delegation. The API ID and property name form
the endpoint ID. `Books.list` and `Authors.list` are distinct meaningful IDs.

```kotlin
object BooksApi : Api(id = "Books") {
    val list by get(root / "books")
}
```

Delegation registers endpoints in declaration order without package scanning. An implementation should use Kotlin's
`provideDelegate` convention so registration occurs during API initialization rather than on first property access.

## Endpoint structure

The conceptual endpoint type is:

```kotlin
Endpoint<
    P : Paths,
    Q : Queries,
    H : Headers,
    I : RequestInput,
    O : Outputs
>
```

All ordered heterogeneous collections use fixed-arity types from zero through eight. Concrete element types are
preserved exactly. Reaching a ninth element is a compilation failure.

## URI

URI fragments are reusable and composed with `/` for path segments and `+` for query parameters.

```kotlin
val book = root / "books" / path.uuid("bookId")
val pagedBooks = root / "books" + query.int("page").optional(default = 1)
```

Leading slashes are normalized. Declaration order is preserved. Path variables are always required. Wildcard paths
will use `path.remaining("path")` when introduced.

Queries preserve one of three presence modes in their types:

```kotlin
query.string("term")                    // required
query.string("term").optional()         // optional without a default
query.int("page").optional(default = 1) // optional with a default
```

Convenience builders such as `path.uuid`, `query.int`, and `header.uuid` delegate to the same underlying typed
constructors.

## Formats

One generic format combines a codec and schema:

```kotlin
Format<T, Representation>
```

String and byte-array formats may be public type aliases. Parameters use string formats; bodies use byte-array
formats. A body owns its media type separately so one format can serve default, vendor-specific, and problem media
types.

Value-class formats favor discoverable transformation syntax:

```kotlin
val bookIdFormat =
    string.uuid
        .transform(decode = ::BookId, encode = BookId::value)
        .named("BookId")
```

Kotlin serialization body builders are enabled by:

```kotlin
import dev.akif.tapik.format.kotlinx.jsonBody
```

They use a cached global default provider. An API may configure a default provider, and an individual body may
override it with `using`. Multiple named providers are not required.

## Request headers and input

Request headers belong directly to an endpoint and append in declaration order:

```kotlin
.header(authorization)
.headers(headersOf(requestId, traceId))
```

Headers retain required, optional, defaulted, or fixed presence in separate header-presence types. Fixed queries may
also be supported. Header names compare case-insensitively for uniqueness.

Request input has distinct states:

```kotlin
NoInput
Input<B : Bodies>
```

Only an endpoint with `NoInput` offers `.input(...)`, so a second input is a compilation failure. Bodies within one
input are alternative media representations of exactly one logical Kotlin type:

```kotlin
.input(jsonBody<CreateBook>())
.input(bodiesOf(jsonBody<UpdateBook>(), noBody))
```

`NoBody` is the type and `noBody` is its DSL value. It may appear at most once, in any position, and counts toward the
arity limit. Multiple real bodies must use the same declared Kotlin type and distinct media types.

## Outputs

`Status` is a validated value class, not an enum. Named constants cover standard statuses without excluding extension
codes. A status becomes an exact matcher when combined with a body.

The output grammar is status matcher, body or bodies, then optional headers:

```kotlin
.output(Status.Ok with jsonBody<Book>())

.output(
    Status.Ok with bodiesOf(
        jsonBody<Book>(),
        anotherBodyRepresentation<Book>()
    )
)

.output(
    Status.Created with jsonBody<Book>() with headersOf(location)
)
```

Output headers append and preserve declaration order. Multiple `.output(...)` calls append distinct response
alternatives. Matcher kind, concrete headers, and concrete body formats remain in the output's generic type.

An endpoint with no explicit outputs means an empty `200` response. Adding the first explicit output removes that
implicit response. Bodyless explicit responses use `Status.NoContent with noBody`; no status-only special case exists.

Exact, set, range, default, and described predicate matchers may exist in Tapik. Neutral matcher capabilities should
be represented in types where useful; targets provide tailored diagnostics for unsupported matchers.

## Documentation and tags

Documentation follows the concepts and fields supported by OpenAPI. API and endpoint constructor arguments coexist
with individual immutable modifiers. Repeated documentation modifiers use last-value-wins semantics.

```kotlin
val create by post(
    uri = root / "books",
    summary = "Create a book"
)
    .description("Adds a book to the library.")
    .document(summary = "Create a book", description = "Adds a book.")
```

Inputs and outputs can carry their applicable summaries and descriptions. `.tag(value)` appends; `.tags(set)`
replaces. Tags intentionally use set semantics rather than declaration-order semantics.

## Design fixture

The initial library fixture will grow to cover books, authors, and rentals. Its first endpoints are:

```text
GET  /books
GET  /books/{bookId}
POST /books
```

It includes a transformed `BookId` format, a defaulted page query, a required request ID header, Kotlin-serialization
JSON inputs and outputs, multiple status alternatives, and a response `Location` header.
