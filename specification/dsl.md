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

`Method` is a simple enum containing `GET`, `HEAD`, `POST`, `PUT`, `PATCH`, `DELETE`, `CONNECT`, `OPTIONS`, `TRACE`,
and `QUERY`. The method is part of the endpoint value; it does not need its own generic type parameter.

All ordered heterogeneous collections use fixed-arity types from zero through eight. Concrete element types are
preserved exactly. Reaching a ninth element is a compilation failure.

The shared tuple algebra consists of `Tuple0` through `Tuple8`. `Tuple0` is the empty value; each non-empty tuple
stores its elements in `_1` through `_8`, exposes them in declaration order through `values`, and appends one value
with `and`. `Tuple8` has no append operation. Every tuple has structural value semantics.

## URI

URI fragments are reusable and composed with `/` for path segments and `+` for query parameters.

```kotlin
val book = root / "books" / path.uuid("bookId")
val pagedBooks = root / "books" + query.int("page").optional(default = 1)
```

URI construction always starts with the `root` value. A string passed to `/` is an already-encoded path fragment and
may contain multiple segments. Leading and trailing `/` characters are normalized away at composition boundaries;
each remaining segment is appended to the URI's path list without encoding or decoding. A fragment with no segments,
or with an empty segment caused by repeated internal separators, is rejected. The empty path list represents `/`, and
trailing slashes do not distinguish otherwise equal URIs.

Static fragments do not consume typed tuple arity. Declaration order is preserved. Path variables are always
required. `Paths` and `Queries` alias tuples of `PathVariable` and query definitions respectively; their concrete
arity aliases are `Paths0` through `Paths8` and `Queries0` through `Queries8`. Domain APIs use these aliases instead
of exposing the underlying tuple types. Wildcard paths will use `path.remaining("path")` when introduced.

`Uri.segments` is a list of `PathSegment` values. A literal fragment contributes one literal segment per `/`-separated
part; a `PathVariable<Value>` is both a path segment and the exact value stored in the URI's `Paths` tuple. Variable
names are non-blank URI-template names and unique within a URI. `Uri.toString()` joins literal segments and variable
names such as `{bookId}` into the complete path template.

Generic and convenient path-variable builders coexist:

```kotlin
path<BookId>(name = "bookId", format = bookIdFormat)
path.string("slug")
path.uuid("bookId")
```

`path` is an alias of `PathVariable.Companion`, where all built-in builders live. The companion implements a shared
named-defaults interface that query parameters, headers, and similar named values can also implement without
duplicating the common factory shape. This keeps generic construction and discoverable built-ins on the domain type.

Queries preserve one of three presence modes in their types:

```kotlin
query.string("term")                    // required
query.string("term").optional()         // optional without a default
query.int("page").optional(default = 1) // optional with a default
query.uuid("authorId").repeated()        // required list
```

The scalar representation is `QueryParameter<Value, Presence>`, using the shared `Required`, `Optional`, and
`Default<Value>` presence types. Generic construction is available as `query<Value>(name, format)`, while `query`
aliases `QueryParameter.Companion` for the same built-in factory surface as `path`. Query names are non-blank,
contain no query delimiters or whitespace and are unique among endpoint definitions. A repeated query is one endpoint
definition backed by `RepeatedQueryParameter<Element, Presence>`. Its format transforms from `Format<Element,
Representation>` to `Format<List<Element>, List<Representation>>`, making its decoded endpoint value a list without
adding a generic parameter to `QueryParameter`. Repeated wire occurrences do not add definitions or consume tuple
arity. Calling `.repeated()` is available only on scalar required or optional parameters, so repetition cannot be
applied twice or after a scalar default has been assigned.

Appending with `+` preserves declaration order and the exact value and presence types in `Queries0` through
`Queries8`. `Uri.toString()` renders query templates after the path, such as `/books?term={term}&page={page}`.
Once the first query parameter is appended, path fragments and path variables can no longer be appended.

Convenience builders such as `path.uuid`, `query.int`, and `header.uuid` delegate to the same underlying typed
constructors.

## Formats

One generic format combines a codec and schema:

```kotlin
Format<Value, Representation>
```

Both types are non-null. A codec uses Tapik's dependency-free `DecodeResult`, whose failure contains one or more
`DecodeError` values. A decode error has a message and may retain a cause and a string location. Schemas are untyped;
the enclosing `Format<Value, Representation>` establishes their relationship to the Kotlin type.

Every format can be lifted to repeated values. `Format<Value, Representation>.repeated()` returns
`Format<List<Value>, List<Representation>>`, decodes every representation while accumulating failures, encodes each
value independently, and wraps the original schema in an array schema.

String and byte-array formats may be public type aliases. Parameters use string formats; bodies use byte-array
formats. A body owns its media type separately so one format can serve default, vendor-specific, and problem media
types.

The cached string defaults live on `Format.Companion`; `format` is an ergonomic alias of that companion.
`FormatDefaults<Representation>` describes the common scalar-format shape for any representation, and
`StringFormats` implements it for `String`. Defaults are named after their Kotlin types: `boolean`, `byte`,
`short`, `int`, `long`, `float`, `double`, `bigInteger`, `bigDecimal`, `string`, `uuid`, `localDate`, `localTime`,
`localDateTime`, `offsetTime`, `offsetDateTime`, `instant`, `duration`, and `period`. Their scalar schemas use the
corresponding OpenAPI format when one exists. A failed parse produces a structured decode failure and retains the
parsing exception as its cause.

Value-class formats favor discoverable transformation syntax:

```kotlin
val bookIdFormat =
    format.uuid
        .transform(decode = ::BookId, encode = BookId::value)
        .named("BookId")
```

`transform` is safe by default: exceptions from its decoding transformation become decode failures with the original
exception as their cause. `transformOrThrow` provides the explicit propagating variant. Both reuse the original wire
representation and schema. `named` immutably assigns the schema name.

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
