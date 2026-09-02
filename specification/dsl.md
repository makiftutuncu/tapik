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

APIs compose explicitly through the same property-delegation mechanism:

```kotlin
object Library : Api() {
    val authors by including(Authors)
    val health by get(root / "health")
    val books by including(Books)
}
```

`including` is available only inside an `Api`. The delegated property evaluates to the original included API value and
retains its concrete Kotlin type; it does not copy the API or its endpoints. Included endpoints therefore keep their
original identities, so `Library.authors.list` is the same endpoint value with ID `Authors.list` as `Authors.list`.
The included API remains independently usable and requires no migration.

`Api.endpoints` recursively flattens inclusions at their declaration positions. In the example, all author endpoints
precede `Library.health`, which precedes all book endpoints. Nested inclusions apply the same rule at every level, so
declaration order and endpoint identity survive arbitrary nesting. `Api.includedApis` exposes an ordered snapshot of
the API's direct inclusions and their delegated property names; it does not duplicate endpoint definitions.

An API cannot include itself directly or transitively. Each API ID and qualified endpoint ID must be unique within a
composed tree, so including the same API twice or combining distinct APIs with colliding IDs fails while the composed
API initializes. Inclusion properties used by compiled source targets must be public and retain the included API's
concrete type so generated code can follow the delegated property path without reflection.

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
required. `Paths` and `Queries` alias tuples of path values and query definitions respectively; their concrete arity
aliases are `Paths0` through `Paths8` and `Queries0` through `Queries8`. Domain APIs use these aliases instead of
exposing the underlying tuple types.

`Uri.segments` is a list of `PathSegment` values. A literal fragment contributes one literal segment per `/`-separated
part; a `PathVariable<Value>` is both a path segment and the exact value stored in the URI's `Paths` tuple. Variable
names are non-blank URI-template names and unique within a URI. `Uri.toString()` joins literal segments and variable
names such as `{bookId}` into the complete path template.

`path.remaining("path")` defines a required wildcard consuming one or more final path segments as a `List<String>`.
It is represented by the distinct `RemainingPath` type, consumes one path-tuple position, and renders as `{*path}`.
Its format converts between the list and one slash-separated wire value while rejecting empty lists, empty segments,
and segments containing `/`. A remaining path must be the final path definition: Kotlin overload resolution prevents
appending another literal or path variable, while query parameters may still follow it.

Generic and convenient path-variable builders coexist:

```kotlin
path<BookId>(name = "bookId", format = bookIdFormat)
path.string("slug")
path.uuid("bookId")
path.remaining("path")
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

Both types are non-null. A codec uses tapik's dependency-free `DecodeResult`, whose failure contains one or more
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

`UnionSchema` describes a value that must match exactly one of at least two alternatives. Alternative order is
preserved. An optional `SchemaDiscriminator` names the wire property used to select an alternative and may define
explicit value-to-schema mappings plus a default mapping. Discriminator mappings target `ReferenceSchema` values so
component names remain neutral and are interpreted consistently by each target's naming policy.

Union alternatives and discriminator mappings are immutable snapshots. Blank discriminator property names or
discriminating values are rejected while building the schema, before a target runs.

Kotlin serialization body builders are enabled by:

```kotlin
import dev.akif.tapik.format.kotlinx.jsonBody

val defaultJson = jsonBody<Book>(format = Json.Default)
val configuredJson = jsonBody<Book>(format = applicationJson)
```

Every body is attached to its concrete format when built. Body builders take the serialization format directly and
do not consult API or tapik-global configuration. Derived core formats are cached by serialization-format and
serializer identity. Concurrent lookups reuse the same live format instance. Cache keys and values are weakly held, so
the cache does not retain an otherwise unreachable serialization format, serializer, derived format, or its defining
classloader; a collected format may be recreated by a later lookup. `Json.Default` is the default argument for the
Kotlin serialization JSON builder.

Kotlin serialization schema derivation covers primitives, enums, lists, maps, nullable properties, objects, value
classes, sealed hierarchies, and finitely registered open polymorphic hierarchies. Recursive object and polymorphic
references are retained as schema references. Object schema properties retain separate `required` and `deprecated`
flags; derivers set each flag only when their source metadata can express it reliably.

JSON polymorphism is derived from both the serializer and the selected `Json` instance. Sealed alternatives retain
their serializer declaration order. Kotlin serialization does not expose open serializer-module registration order,
so open alternatives are ordered by serialized type name to keep generated schemas deterministic. Each alternative is
a named object schema containing the required discriminator property with its exact serialized type name, and the
enclosing `UnionSchema` maps those values to the alternatives. `@JsonClassDiscriminator` overrides the selected `Json`
instance's global discriminator name just as it does for the codec.

Format construction fails when a polymorphic descriptor has no finite alternatives, uses a default polymorphic
provider, has a non-object alternative, conflicts with the discriminator property, disables discriminators, or uses
array polymorphism. `ClassDiscriminatorMode.ALL_JSON_OBJECTS` also fails until ordinary object schemas can represent
that mode faithfully. Descriptor-only schema derivation uses `Json.Default`; callers needing configured polymorphism
must provide their `Json` instance.

Jackson 3 body builders are provided independently by `dev.akif:tapik-format-jackson`:

```kotlin
import dev.akif.tapik.format.jackson.jsonBody

val defaultJson = jsonBody<Book>()
val configuredJson = jsonBody<Book>(format = applicationObjectMapper)
```

The default Jackson format uses an `ObjectMapper` with the Jackson Kotlin module registered. A supplied mapper controls
wire encoding, decoding, property visibility, JSON names, and property order. Jackson formats are cached by mapper
identity and Kotlin type; custom mappers remain isolated from the default and from one another. Cache entries do not
retain otherwise unreachable mappers, types, formats, or their defining classloaders.

Jackson schema derivation has the same initial structural coverage as Kotlin serialization: primitives, enums, lists,
maps, nullable properties, objects, value classes, recursive references, constructor defaults, and deprecation flags.
The derived object shape follows Jackson's effective serialization properties, including configured names, ignored
properties, and order. Polymorphic and sealed types still fail format construction until Jackson-specific derivation
is implemented against the neutral union algebra and shared conformance cases.

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

`Status` is a validated data class, not an enum. Named constants cover standard statuses without excluding extension
codes. Keeping it as a regular class also lets status-set builders expose Kotlin's natural vararg syntax. A status
becomes an exact matcher when combined with a body. Other matcher values use the same output grammar:

```kotlin
statusesOf(Status.Ok, Status.Created) with noBody
statusesIn(400..499) with noBody
statusMatching("successful extension status") { status -> status.code in 290..299 } with noBody
```

`statusesOf` takes one required status followed by zero or more statuses and produces a `StatusSet`, normalizing
duplicates. `statusesIn` requires a non-empty range contained by the valid HTTP status domain and produces a
`StatusRange`. `statusMatching` requires a non-blank description and produces a `CustomStatus` retaining its runtime
predicate. The description is the stable representation available to diagnostics and targets because predicate code
itself is not portable.

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

Before an output is appended, tapik evaluates it against existing alternatives over every valid HTTP status code. Any
shared match is rejected with the conflicting status. A custom predicate that throws during this validation
produces a contextual construction failure retaining the original cause. Custom predicates are required to be pure and
deterministic. Targets provide tailored diagnostics for matcher kinds they cannot represent.

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

`EndpointDocumentation` owns the optional summary and description without adding generic parameters to `Endpoint`.
Blank present values are invalid. Omitting one value from `.document(...)` retains its current value. Documentation
modifiers are available only on draft endpoints.

Parameter definitions carry `ParameterDocumentation`, containing OpenAPI-aligned `description` and `deprecated`
values. This applies uniformly to path variables, query parameters, and headers; the same header documentation is used
when a header appears on a response. Immutable `.description(...)` and `.deprecated(...)` modifiers retain the
definition's concrete generic type, and presence or repetition modifiers preserve its documentation.

A `BodyInput` carries `RequestBodyDocumentation` because OpenAPI documents the request body as a whole rather than an
individual media representation. Both `.input(body, description = ...)` and `.input(bodies, description = ...)`
initialize it, while `.requestBodyDescription(...)` replaces it without changing the input's generic type. An `Output`
carries `ResponseDocumentation`; `(Status.Created with noBody).description(...)` sets the OpenAPI response description.
When absent, targets may use their standard status description. Blank parameter, request-body, and response
descriptions are invalid. None of these documentation values participate in `Endpoint` generic parameters.

Endpoint builders accept an initial tag set. `.tag(value)` appends; `.tags(set)` replaces. Tags intentionally use set
semantics rather than declaration-order semantics, reject blank values, and are mutable only while the endpoint is a
draft.

## Design fixture

The `test-fixtures` module's `dev.akif.tapik.test.fixtures.library` package defines separate `Books`, `Authors`, and
`Rentals` APIs so operation IDs remain domain-qualified. Its representative endpoints are:

```text
GET  /books
GET  /books/{bookId}
POST /books
GET  /authors
GET  /authors/{authorId}
POST /authors
GET  /rentals
GET  /rentals/{rentalId}
POST /rentals
POST /rentals/{rentalId}/return
```

It includes transformed domain identifier formats, defaulted and repeated queries, an optional `LocalDate` query, a
required request ID header, Kotlin-serialization JSON inputs and outputs, multiple status alternatives, and response
`Location` headers. OpenAPI, RestClient, and WebMVC target tests generate all three APIs while retaining focused golden
coverage for the Books contract.
