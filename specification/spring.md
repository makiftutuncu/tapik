# Spring integration specification

## Shared Spring boundary

`tapik-common-spring` contains only conversions needed by more than one Spring integration. It maps Tapik methods,
statuses, media types, and headers to and from Spring's HTTP model without interpreting endpoints. All standard Tapik
methods, including `CONNECT` and `QUERY`, remain representable even when Spring does not expose named constants for
them.

## RestClient transport

`tapik-target-spring-restclient` executes already-resolved requests with Spring `RestClient`. The transport accepts a
Tapik method, a Spring URI builder function, encoded headers, and an optional encoded body. It returns the raw Tapik
status, headers, media type, and bytes for every HTTP response, including error statuses.

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
sealed response type. Output alternatives become response variants in declaration order. Exact variants imply their
status, while set, range, and custom variants carry the actual response `Status` selected by the runtime matcher.

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

Generated clients contain only API-specific request construction, output matching, and response construction. Stable
response body and header decoding and fixed-header conformance live once in `tapik-target-spring-restclient`; generated
sources call that runtime support instead of copying private helper functions into every client interface.

An endpoint with one encoded request-body representation exposes its logical Kotlin value directly, preserving the
short call shape for the common case. An endpoint with multiple encoded representations instead exposes a generated
sealed request-body type whose variants are named from the declared media types and each carry that same logical value.
Callers therefore select the representation explicitly and exhaustively, and generated request encoding uses the
format and media type belonging to the selected variant. Variant names are allocated deterministically in body
declaration order. When `noBody` is also declared, the sealed request-body parameter is nullable and defaults to
`null`; `null` sends no body.

Multiple output body representations remain distinguishable by response media type. Unsupported compiled shapes or
endpoint capabilities fail generation with the API and endpoint ID.

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

The `spring-webmvc` target generates one public Kotlin handler interface and one internal Spring adapter for each
selected API. The handler interface exposes a uniquely named property for the concrete API value, a typed abstract
handler method for every endpoint, and nested sealed response types. It contains no Spring mapping annotations, raw
wire parameters, `ResponseEntity` values, or adapter methods.

The internal adapter is a Spring `@RestController` with constructor injection of the handler interface. Its public
mapping methods use the same allocated endpoint names as the handler methods because the types have separate
namespaces; no `Http` suffix is added. The adapter calls the handler, and the handler's API property supplies every
runtime endpoint value used for decoding and encoding. Injecting the handler by interface keeps ordinary Spring proxies
compatible, while the internal adapter keeps its framework methods out of the consumer's Kotlin API.

This separation is also the server-integration boundary. User implementations depend on the pure typed handler
contract, while wire adaptation and framework annotations belong to generated target-specific controllers. Future
server targets should preserve that boundary and provide their own adapters without requiring application handlers to
adopt framework types or annotations. The WebMVC target currently emits both types in one source artifact; extracting a
shared server-contract generator is deferred until another server target needs to consume the same generated contract.

Generated Spring adapter methods receive wire values rather than asking Spring to convert contract values. They decode
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
output. Exact response variants imply their status. Set, range, and custom response variants require handlers to
supply a `Status`; the generated adapter validates it with the endpoint's matcher before returning it. Headers come
from the selected response variant and endpoint definition.

A defaulted output header is exposed as a nullable response field defaulting to `null`. When a handler leaves that
field unset, the mapping method encodes the default carried by the endpoint definition.

Generated adapters contain only API-specific Spring mappings and handler adaptation. Stable request decoding, request
media-type matching, HTTP error construction, and `ResponseEntity` construction live once in
`tapik-target-spring-webmvc`; generated adapters call that runtime support instead of repeating private helpers in every
controller.

Spring WebMVC derives `Content-Type` from the selected response body representation and owns `Content-Length` for the
encoded response bytes. Generation fails when an output that can carry a body also declares `Content-Type`, or when any
output declares `Content-Length`; header names are compared case-insensitively. A bodyless output may declare
`Content-Type`, which is emitted as an ordinary contract header.

`packageName` selects the generated package and defaults to `dev.akif.tapik.generated`. `serverSuffix` selects the
handler-interface suffix and defaults to `Server`. `controllerSuffix` selects the internal generated-controller suffix
and defaults to `GeneratedController`, producing pairs such as `BooksServer` and `BooksGeneratedController`. Users may
name their implementation independently; a `Handler` suffix, such as `BooksHandler`, is the conventional arrangement.

WebMVC uses the same deterministic declaration-name allocation as RestClient. Handler methods share one interface
namespace, adapter mappings share a separate adapter namespace, and nested response types retain their own namespace.
Top-level handler and adapter names and source artifact paths follow the same numeric disambiguation rule.

### Adapter registration

The runtime WebMVC artifact provides `@EnableTapikWebMvc` for plain Spring applications. Adding it to one application
configuration registers only adapters listed by generated Tapik WebMVC registration resources, so it neither scans
arbitrary packages nor reflects over API classes. It registers Tapik adapters only; configuring Spring MVC itself
remains the application's responsibility.

Spring Boot applications need no annotation or user configuration. The runtime artifact contributes one public Boot
auto-configuration entry that applies `@EnableTapikWebMvc` and therefore uses the same generated descriptors and
selection behavior as plain Spring. The Boot dependency is optional for the runtime artifact so plain Spring consumers
do not receive Boot transitively.

Each generated descriptor names both its pure handler interface and generated adapter. After application bean
definitions are available, Tapik registers an adapter only when Spring can select a single candidate handler bean. No
handler leaves that API unregistered; one candidate registers the adapter; multiple candidates register it only when
exactly one candidate is primary. Qualifiers alone do not select a generated adapter because the adapter declares no
generated qualifier. A single user bean may implement several generated handler interfaces and receives one adapter
for each interface. If an adapter's generated package is inside the application's component-scan tree, Tapik replaces
the implicitly scanned definition with its canonical descriptor-based definition so each mapping is registered once
and the same handler-selection rules still apply.

The target emits one source and one uniquely named registration resource per API. Resource identity includes the
generated adapter's qualified name, and registration order is canonical by that name. The Maven host packages generated
runtime resources in addition to compiling generated source. Users provide only ordinary Spring beans implementing the
handler interfaces; they do not write per-API adapter configuration.

The Maven integration fixture consumes an API from a separate compiled contract artifact, generates its WebMVC
handler, adapter, and registration resource during `generate-sources`, and compiles an implementation of the generated
interface. Boot and plain Spring application contexts discover the same adapters and serve typed responses through
Spring's mock MVC runtime without per-API adapter configuration.

## Target runtime conformance

The Maven integration fixture is the shared black-box conformance boundary for generated Spring clients and servers.
One compiled contract dependency supplies both generated targets so their protocol behavior is exercised from the same
endpoint values rather than from target-specific copies. Generated-source snapshots remain useful structural tests,
but they do not replace requests and responses executed through Spring's mock HTTP transports.

The conformance contract's create operation verifies an encoded JSON request body, a successful body response with a
required `Location` header, and a bodyless error alternative. RestClient tests assert the complete request and decode
both response alternatives. MockMVC tests assert successful response encoding, malformed-body rejection as `400 Bad
Request`, unsupported request media rejection as `415 Unsupported Media Type`, required response-header encoding, and
the bodyless error response.

The list operation verifies optional and defaulted query inputs, a fixed request header, and a fixed response header.
RestClient must supply defaulted and fixed values when callers omit them. WebMVC must supply defaults to implementations,
reject malformed scalar values and mismatched fixed values as `400 Bad Request`, and emit fixed response headers.
