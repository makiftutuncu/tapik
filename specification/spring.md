# Spring integration specification

## Shared Spring boundary

`tapik-spring` contains only conversions needed by more than one Spring integration. It maps Tapik methods, statuses,
media types, and headers to and from Spring's HTTP model without interpreting endpoints. All standard Tapik methods,
including `CONNECT` and `QUERY`, remain representable even when Spring does not expose named constants for them.

## RestClient transport

`tapik-spring-restclient` executes already-resolved requests with Spring `RestClient`. The transport accepts a Tapik
method, a Spring URI builder function, encoded headers, and an optional encoded body. It returns the raw Tapik status,
headers, media type, and bytes for every HTTP response, including error statuses.

Encoded request and response values snapshot byte arrays on construction and return a copy when their bytes are read.
Response header maps and their value lists are structural snapshots.

Request bodies and responses compare encoded byte arrays by content and derive hash codes from that content. Equal
transport values therefore remain equal when their bytes come from different array instances.

The transport does not match outputs or decode values. Generated clients use the endpoint value and its compiled
Kotlin type to perform those operations and fail generation when an endpoint feature is unsupported.

## RestClient generation target

The `spring-restclient` target generates one Kotlin interface for each selected API. Interfaces share a
`restClientTransport` property so one implementation may compose multiple API clients, and each interface exposes a
uniquely named property for its concrete API value. Every endpoint becomes a default interface method and a nested
sealed response type. Exact-status output alternatives become response variants in declaration order.

Generated response data classes containing byte-array fields compare those fields by content and derive their hash
codes from the byte content.

Fixed output headers are contract checks rather than public response fields. For each selected output, a generated
client encodes the fixed value through its header format and requires the transport response to contain exactly one
value with that encoding. A missing header, a different value, or repeated values fail response decoding.

For the selected status output, generated clients match a received `Content-Type` to body representations using parsed
compatible media types in declaration order. Parameters such as a response charset do not make an otherwise compatible
representation fail. A response without `Content-Type` represents `noBody` only when its bytes are empty and that
alternative is declared. Bytes without `Content-Type`, a `Content-Type` on a bodyless-only output, a missing body
alternative, or an incompatible media type fail response decoding. Empty bytes with a compatible `Content-Type` are
passed to the selected format because an empty encoded value may be valid for that format.

Generated method inputs are derived from the endpoint's compiled Kotlin type. Required path variables, queries,
headers, and request bodies precede optional or defaulted values. Runtime endpoint values supply names, formats,
defaults, media types, matchers, and codecs. Generated code performs URI construction, request encoding, output
matching, and response decoding without a parallel endpoint model.

The initial target supports at most one encoded request-body representation. Multiple output body representations
remain distinguishable by response media type. Unsupported compiled shapes or endpoint capabilities fail generation
with the API and endpoint ID.

`packageName` selects the generated package and defaults to `dev.akif.tapik.generated`. `clientSuffix` selects the
interface-name suffix and defaults to `Client`. Each source artifact follows the package path and uses the concrete API
type name plus that suffix.

Generated declaration names are allocated deterministically in API, endpoint, and output declaration order. A
normalized name collision keeps the first name unchanged and appends `2`, `3`, and so on to later declarations.
Endpoint functions and nested response types use separate namespaces. API types with the same simple name are
disambiguated together with their artifact paths. Original endpoint properties are accessed with their actual Kotlin
names, including backtick-escaped names.

The Maven integration fixture consumes an API from a separate compiled contract artifact, generates its RestClient
client during `generate-sources`, compiles the generated source, and executes a typed request and response through
Spring's mock HTTP server.

## WebMVC generation target

The `spring-webmvc` target generates one Kotlin server interface for each selected API. The interface exposes a
uniquely named property for the concrete API value, a typed abstract handler method for every endpoint, nested sealed
response types, and Spring-mapped default methods that adapt HTTP requests to those handlers. Users decide how server
implementations become Spring controllers; annotating an implementation with `@RestController` is sufficient.

Generated Spring mapping methods receive wire values rather than asking Spring to convert contract values. They decode
path variables, query parameters, headers, and bodies through the formats attached to the endpoint. Decode failures and
fixed-header mismatches produce `400 Bad Request`. `CONNECT` and `QUERY` fail generation because Spring WebMVC cannot
map them.

Repeated query parameters preserve raw query occurrences independently of Spring conversion rules. A request containing
`?tag=a,b` decodes from the single value `"a,b"`, while `?tag=a&tag=b` decodes from the two values `"a"` and `"b"`.

Request body alternatives are selected by a compatible request `Content-Type` in declaration order. Every encoded
alternative remains supported; an unmatched content type produces `415 Unsupported Media Type`. When `noBody` is an
alternative, an absent request body is decoded as `null`.

Generated mapping methods encode handler responses through the selected output definition and return Spring
`ResponseEntity<ByteArray>`. Every selected output containing a body is negotiated against the request's `Accept`
values, including outputs with only one representation. The compatible representation with the highest effective
quality is selected; a more specific media range wins ties, followed by body declaration order. A media range with
quality zero excludes the representation when it is the most specific match. Non-quality parameters on an `Accept`
range must match parameters offered by the representation. Missing or wildcard `Accept` values select the first
representation, while an invalid header or no compatible representation produces `406 Not Acceptable`.

An output selected without a body is not constrained by `Accept`. When any endpoint output can be bodyless, its Spring
mapping omits an aggregate `produces` condition so routing cannot reject a request before the handler selects that
output. Statuses and headers come from the selected response variant and endpoint definition.

A defaulted output header is exposed as a nullable response field defaulting to `null`. When a handler leaves that
field unset, the mapping method encodes the default carried by the endpoint definition.

Spring WebMVC derives `Content-Type` from the selected response body representation and owns `Content-Length` for the
encoded response bytes. Generation fails when an output that can carry a body also declares `Content-Type`, or when any
output declares `Content-Length`; header names are compared case-insensitively. A bodyless output may declare
`Content-Type`, which is emitted as an ordinary contract header.

`packageName` selects the generated package and defaults to `dev.akif.tapik.generated`. `serverSuffix` selects the
interface-name suffix and defaults to `Server`. Generated Spring mapping method names append `Http` to the corresponding
typed handler method name.

WebMVC uses the same deterministic declaration-name allocation as RestClient. Handler and Spring mapping functions
share one generated interface namespace, so a mapping name cannot collide with another endpoint's handler. Nested
response types, top-level server types, and artifact paths follow the same numeric disambiguation rule.

The Maven integration fixture consumes an API from a separate compiled contract artifact, generates its WebMVC
server during `generate-sources`, compiles an implementation of the generated interface, and serves a typed response
through Spring's mock MVC runtime.
