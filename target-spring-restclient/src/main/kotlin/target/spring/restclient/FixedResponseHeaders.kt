package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.StringFormat

/**
 * Requires [response] to contain exactly one [name] header equal to the encoded [expected] value.
 *
 * Header-name comparison is case-insensitive.
 */
fun <Value : Any> requireFixedResponseHeader(
    response: RestClientResponse,
    name: String,
    format: StringFormat<Value>,
    expected: Value,
    endpointId: String
) {
    val actual =
        response.headers.entries
            .filter { (headerName, _) -> headerName.equals(name, ignoreCase = true) }
            .flatMap { (_, values) -> values }
    val encoded = format.encode(expected)
    check(actual == listOf(encoded)) {
        "Unexpected fixed response header $name for $endpointId: expected exactly [$encoded], got $actual"
    }
}
