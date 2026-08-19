# Spring integration specification

## Shared Spring boundary

`tapik-spring` contains only conversions needed by more than one Spring integration. It maps Tapik methods, statuses,
media types, and headers to and from Spring's HTTP model without interpreting endpoints. All standard Tapik methods,
including `CONNECT` and `QUERY`, remain representable even when Spring does not expose named constants for them.

## RestClient transport

`tapik-spring-restclient` executes already-resolved requests with Spring `RestClient`. The transport accepts a Tapik
method, a Spring URI builder function, encoded headers, and an optional encoded body. It returns the raw Tapik status,
headers, media type, and bytes for every HTTP response, including error statuses.

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
