# DSL specification

## API and endpoint identity

Endpoints are declared only inside an `Api` and only through property delegation. The API ID and property name form
the endpoint ID. `Books.list` and `Authors.list` are distinct meaningful IDs.

```kotlin
object Books : Api() {
    val list by get(root / "books")
}
```

Delegation registers endpoints in declaration order without package scanning. An implementation should use Kotlin's
`provideDelegate` convention so registration occurs during API initialization rather than on first property access.
`Api.endpoints` exposes an ordered snapshot `List<Endpoint<*, *, *, *, *, Ready>>` for discovery. Mutating a castable
returned list cannot change the API's registered endpoints. This snapshot is necessarily star-projected, while each
delegated endpoint property retains its complete inferred generic type.

## Endpoint structure

The conceptual endpoint type is:

```kotlin
Endpoint<
    P : Paths,
    Q : Queries,
    H : Headers,
    I : Input,
    O : Outputs,
    S : EndpointState
>
```

Endpoint identity has two compile-time states. Standard method builders produce an immutable `Endpoint<..., Draft>`.
Property delegation creates a new `Endpoint<..., Ready>` whose qualified ID combines the API ID and property name.
Only ready endpoints expose `id` and may be consumed by targets. Contract modifiers are available only on draft
endpoints and return new draft values, allowing ordinary Kotlin functions to compose definitions before declaration
without creating divergent ready contracts that share an ID.

Every endpoint initially has `noHeaders`, `noInput`, and `DefaultOutput`. `NoInput` is the type of the `noInput`
value. `DefaultOutput` means one empty `200`
response and is replaced when the first explicit output is added.

`Method` is a simple enum containing `GET`, `HEAD`, `POST`, `PUT`, `PATCH`, `DELETE`, `CONNECT`, `OPTIONS`, `TRACE`,
and `QUERY`. The method is part of the endpoint value; it does not need its own generic type parameter.

All ordered heterogeneous collections use fixed-arity types from zero through eight. Concrete element types are
preserved exactly. Reaching a ninth element is a compilation failure.

The shared tuple algebra consists of `Tuple0` through `Tuple8`. `Tuple0` is the empty value; each non-empty tuple
stores its elements in `_1` through `_8` and exposes them in declaration order through `values`. `NonEmptyTuple`
distinguishes `Tuple1` through `Tuple8` wherever an empty collection is not a valid contract state. Every tuple has
structural value semantics.

Tuple values are publicly readable structural results of the DSL, not a second public construction API. Non-empty
tuple constructors, their generated `copy` functions, and generic tuple appending are internal to core. Public code
constructs domain tuples only through validated factories and endpoint modifiers such as `bodiesOf`, `headersOf`,
`input`, `header`, and `output`.

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

val defaultJson = jsonBody<Book>(format = Json.Default)
val configuredJson = jsonBody<Book>(format = applicationJson)
```

Every body is attached to its concrete format when built. Body builders take the serialization format directly and
do not consult API or Tapik-global configuration. Derived core formats are cached per serialization-format and
serializer pair, so repeated body construction does not recreate them. `Json.Default` is the default argument for
the Kotlin serialization JSON builder.

Kotlin serialization schema derivation initially covers primitives, enums, lists, maps, nullable properties,
objects, and value classes. Recursive object references are retained as schema references. Unsupported descriptor
kinds fail while the format is built. Object schema properties retain separate `required` and `deprecated` flags;
derivers set each flag only when their source metadata can express it reliably.

## Request headers and input

Request headers belong directly to an endpoint and append in declaration order:

```kotlin
.header(authorization)
.headers(headersOf(requestId, traceId))
```

`.headers(...)` is a one-time bulk initializer available only while a draft endpoint has `Headers0` and accepts a
non-empty `Headers1` through `Headers8` value. After any headers have been initialized, `.header(...)` is the only
header modifier and appends one definition at a time. Both modifiers retain exact header types; reaching a ninth
header is a compilation failure. Ready endpoints expose neither modifier.

Headers retain required, optional, defaulted, or fixed presence in separate header-presence types. Fixed queries may
also be supported. Header names compare case-insensitively for uniqueness.

The scalar representation is `Header<Value, Presence>`. Generic and convenient builders coexist on
`Header.Companion`:

```kotlin
header<BookId>(name = "X-Book-Id", format = bookIdFormat)
header.uuid("X-Request-Id")
header.string("X-Source").fixed("tapik")
```

`headersOf` accepts between one and eight headers, preserves their exact types and declaration order in `Headers1`
through `Headers8`, and rejects duplicate names case-insensitively. `Headers0` represents the absence of headers and
`noHeaders` is its named DSL value; calling `headersOf()` with no arguments is intentionally unavailable.

Request input has distinct states:

```kotlin
NoInput
BodyInput<B : Bodies>
```

Only an endpoint with `NoInput` offers `.input(...)`, so a second input is a compilation failure. Bodies within one
input are alternative media representations of exactly one logical Kotlin type:

```kotlin
.input(jsonBody<CreateBook>())
.input(bodiesOf(jsonBody<UpdateBook>(), noBody))
```

`NoBody` is the type and `noBody` is its DSL value. It may appear at most once and counts toward the arity limit. To
keep that rule simple and type-safe, `noBody` must be the final argument to `bodiesOf`. Multiple real
bodies must use the same declared Kotlin type and distinct media types.

`Bodies` is non-empty by construction. `bodiesOf` and the single-body overloads are the public construction boundary;
request and output bulk modifiers validate received body groups before accepting them.

`MediaType` initially wraps the complete media-type string as its own evolvable type. Built-in values cover JSON,
XML, plain text, and arbitrary bytes. A `Body<Value>` combines one media type with a `ByteArrayFormat<Value>`; format
integrations such as Kotlin serialization provide convenient builders including `jsonBody<Value>()`.

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

Output headers preserve declaration order. Multiple `.output(...)` calls append distinct response alternatives.
Matcher kind, concrete headers, and concrete body formats remain in the output's generic type.

`Output` values are created through the status/body `with` grammar; their constructor and generated `copy` function
are internal to core. Attaching output headers accepts only non-empty `Headers1` through `Headers8` values and validates
their names before creating the next output value.

`Outputs` is a non-empty tuple of output alternatives, with `Outputs1` through `Outputs8` retaining every concrete
output type.
`DefaultOutput` is the distinct initial type and exposes its empty `200` response as a singleton at runtime. Adding
the first explicit output replaces that default with `Outputs1`; later calls append in declaration order. A ninth
output is a compilation failure, and duplicate exact statuses are rejected. Bodyless explicit responses use
`Status.NoContent with noBody`; no status-only special case exists.

The initial DSL implements `ExactStatus`, produced by `Status with ...`. Set, range, default, and described predicate
matchers may follow. Neutral matcher capabilities should be represented in types where useful; targets provide
tailored diagnostics for unsupported matchers.

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

`EndpointDocumentation` initially owns the optional summary and description without adding generic parameters to
`Endpoint`. Blank present values are invalid. Omitting one value from `.document(...)` retains its current value.
Documentation modifiers are available only on draft endpoints.

Inputs and outputs can later carry their applicable summaries and descriptions. Endpoint builders accept an initial
tag set. `.tag(value)` appends; `.tags(set)` replaces. Tags intentionally use set semantics rather than
declaration-order semantics, reject blank values, and are mutable only while the endpoint is a draft.

## Design fixture

The `fixtures` module's `dev.akif.tapik.fixtures.library` package covers books, authors, and rentals. Its first
endpoints are:

```text
GET  /books
GET  /books/{bookId}
POST /books
```

It includes a transformed `BookId` format, a defaulted page query, a required request ID header, Kotlin-serialization
JSON inputs and outputs, multiple status alternatives, and a response `Location` header.
