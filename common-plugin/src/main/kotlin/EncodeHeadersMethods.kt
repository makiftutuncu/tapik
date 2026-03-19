@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Encodes the input headers into an HTTP header map.
 *
 * Values are consumed in declaration order for required [HeaderInput] definitions. Headers declared
 * as [HeaderValues] contribute their embedded values automatically, which lets generated clients
 * preserve fixed single-value and multi-value headers without exposing them as method parameters.
 *
 * @receiver input contract whose headers should be encoded.
 * @param values runtime values supplied for required input headers, in declaration order.
 * @return encoded HTTP headers grouped by header name.
 * @throws IllegalArgumentException when a required header value is missing or extra values are provided.
 */
fun Input<out Headers, *>.encodeInputHeaders(vararg values: Any): Map<String, List<String>> {
    val remaining = ArrayDeque(values.asList())
    val pairs = headers.toList().flatMap { header -> encodeHeader(header, remaining) }
    require(remaining.isEmpty()) { "Received ${remaining.size} unexpected input header value(s)" }
    return build(pairs)
}

private fun build(pairs: List<Pair<String, String>>): Map<String, List<String>> =
    pairs.groupBy({ it.first }) { it.second }

@Suppress("UNCHECKED_CAST")
private fun encodeHeader(
    header: Header<*>,
    remaining: ArrayDeque<Any>
): List<Pair<String, String>> =
    when (header) {
        is HeaderInput<*> -> {
            val value = requireNotNull(remaining.removeFirstOrNull()) {
                "Missing value for required header '${header.name}'"
            }
            encodeProvidedHeader(header as Header<Any>, value)
        }

        is HeaderValues<*> -> encodeHeaderValues(header as Header<Any>, header.values)
    }

private fun encodeProvidedHeader(
    header: Header<Any>,
    value: Any
): List<Pair<String, String>> =
    if (value is HeaderValues<*>) {
        encodeHeaderValues(header, value.values)
    } else {
        listOf(header.name to header.codec.encode(value))
    }

private fun encodeHeaderValues(
    header: Header<Any>,
    values: List<*>
): List<Pair<String, String>> =
    values.map { value -> header.name to header.codec.encode(value as Any) }
