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
