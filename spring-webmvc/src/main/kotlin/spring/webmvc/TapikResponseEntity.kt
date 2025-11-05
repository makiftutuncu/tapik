package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.*
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

/**
 * Converts a Tapik [Response] produced by an endpoint with a single output into a Spring [ResponseEntity].
 *
 * @receiver Tapik endpoint that defines the available outputs.
 * @param response endpoint response to render.
 * @return Spring response entity containing the encoded payload, headers, and status.
 */
fun <P : Parameters, I : Input<*, *>, O : Outputs> HttpEndpoint<P, I, O>.toResponseEntity(
    response: Response<*>
): ResponseEntity<Any?> = convertToResponseEntity(outputs.toList(), response)

/**
 * Converts a Tapik `OneOf` response produced by an endpoint with multiple outputs into a Spring [ResponseEntity].
 *
 * @receiver Tapik endpoint that defines the available outputs.
 * @param choice union capturing which output branch was selected.
 * @return Spring response entity containing the encoded payload, headers, and status.
 */
fun <P : Parameters, I : Input<*, *>, O : Outputs> HttpEndpoint<P, I, O>.toResponseEntity(
    choice: OneOf
): ResponseEntity<Any?> = convertToResponseEntity(outputs.toList(), choice)

/**
 * Converts an arbitrary Tapik response representation into a Spring [ResponseEntity].
 *
 * @receiver Tapik endpoint that defines the available outputs.
 * @param result either a concrete [Response] or `OneOf` union emitted by the endpoint implementation.
 * @return Spring response entity containing the encoded payload, headers, and status.
 */
fun <P : Parameters, I : Input<*, *>, O : Outputs> HttpEndpoint<P, I, O>.toResponseEntity(
    result: Any
): ResponseEntity<Any?> = convertToResponseEntity(outputs.toList(), result)

private fun convertToResponseEntity(
    outputs: List<Output<*, *>>,
    result: Any
): ResponseEntity<Any?> {
    val (output, response) = selectOutput(outputs, result)
    return buildResponseEntity(output, response)
}

@Suppress("UNCHECKED_CAST")
private fun selectOutput(
    outputs: List<Output<*, *>>,
    result: Any
): Pair<Output<*, *>, Response<*>> =
    when (result) {
        is Response<*> -> {
            require(outputs.size == 1) {
                "Endpoint declares ${outputs.size} outputs; expected a OneOf result instead of ${result::class.qualifiedName}"
            }
            outputs.first() to result
        }
        is OneOf2.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf2.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf3.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf3.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf3.Option3<*> -> outputs.requireAtLeast(3) to (result.value as Response<*>)
        is OneOf4.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf4.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf4.Option3<*> -> outputs.requireAtLeast(3) to (result.value as Response<*>)
        is OneOf4.Option4<*> -> outputs.requireAtLeast(4) to (result.value as Response<*>)
        is OneOf5.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf5.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf5.Option3<*> -> outputs.requireAtLeast(3) to (result.value as Response<*>)
        is OneOf5.Option4<*> -> outputs.requireAtLeast(4) to (result.value as Response<*>)
        is OneOf5.Option5<*> -> outputs.requireAtLeast(5) to (result.value as Response<*>)
        is OneOf6.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf6.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf6.Option3<*> -> outputs.requireAtLeast(3) to (result.value as Response<*>)
        is OneOf6.Option4<*> -> outputs.requireAtLeast(4) to (result.value as Response<*>)
        is OneOf6.Option5<*> -> outputs.requireAtLeast(5) to (result.value as Response<*>)
        is OneOf6.Option6<*> -> outputs.requireAtLeast(6) to (result.value as Response<*>)
        is OneOf7.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf7.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf7.Option3<*> -> outputs.requireAtLeast(3) to (result.value as Response<*>)
        is OneOf7.Option4<*> -> outputs.requireAtLeast(4) to (result.value as Response<*>)
        is OneOf7.Option5<*> -> outputs.requireAtLeast(5) to (result.value as Response<*>)
        is OneOf7.Option6<*> -> outputs.requireAtLeast(6) to (result.value as Response<*>)
        is OneOf7.Option7<*> -> outputs.requireAtLeast(7) to (result.value as Response<*>)
        is OneOf8.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf8.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf8.Option3<*> -> outputs.requireAtLeast(3) to (result.value as Response<*>)
        is OneOf8.Option4<*> -> outputs.requireAtLeast(4) to (result.value as Response<*>)
        is OneOf8.Option5<*> -> outputs.requireAtLeast(5) to (result.value as Response<*>)
        is OneOf8.Option6<*> -> outputs.requireAtLeast(6) to (result.value as Response<*>)
        is OneOf8.Option7<*> -> outputs.requireAtLeast(7) to (result.value as Response<*>)
        is OneOf8.Option8<*> -> outputs.requireAtLeast(8) to (result.value as Response<*>)
        is OneOf9.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf9.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf9.Option3<*> -> outputs.requireAtLeast(3) to (result.value as Response<*>)
        is OneOf9.Option4<*> -> outputs.requireAtLeast(4) to (result.value as Response<*>)
        is OneOf9.Option5<*> -> outputs.requireAtLeast(5) to (result.value as Response<*>)
        is OneOf9.Option6<*> -> outputs.requireAtLeast(6) to (result.value as Response<*>)
        is OneOf9.Option7<*> -> outputs.requireAtLeast(7) to (result.value as Response<*>)
        is OneOf9.Option8<*> -> outputs.requireAtLeast(8) to (result.value as Response<*>)
        is OneOf9.Option9<*> -> outputs.requireAtLeast(9) to (result.value as Response<*>)
        is OneOf10.Option1<*> -> outputs.requireAtLeast(1) to (result.value as Response<*>)
        is OneOf10.Option2<*> -> outputs.requireAtLeast(2) to (result.value as Response<*>)
        is OneOf10.Option3<*> -> outputs.requireAtLeast(3) to (result.value as Response<*>)
        is OneOf10.Option4<*> -> outputs.requireAtLeast(4) to (result.value as Response<*>)
        is OneOf10.Option5<*> -> outputs.requireAtLeast(5) to (result.value as Response<*>)
        is OneOf10.Option6<*> -> outputs.requireAtLeast(6) to (result.value as Response<*>)
        is OneOf10.Option7<*> -> outputs.requireAtLeast(7) to (result.value as Response<*>)
        is OneOf10.Option8<*> -> outputs.requireAtLeast(8) to (result.value as Response<*>)
        is OneOf10.Option9<*> -> outputs.requireAtLeast(9) to (result.value as Response<*>)
        is OneOf10.Option10<*> -> outputs.requireAtLeast(10) to (result.value as Response<*>)
        else -> error("Unsupported response type: ${result::class.qualifiedName}")
    }

private fun List<Output<*, *>>.requireAtLeast(index: Int): Output<*, *> {
    require(size >= index) {
        "Endpoint declares $size outputs; requested index $index is out of bounds."
    }
    return this[index - 1]
}

private fun buildResponseEntity(
    output: Output<*, *>,
    response: Response<*>
): ResponseEntity<Any?> {
    val headers = HttpHeaders()
    val headerDefinitions = extractHeaderDefinitions(output.headers)
    val headerValues = extractHeaderLists(response)
    headerDefinitions.forEachIndexed { idx, header ->
        val values = headerValues.getOrNull(idx) ?: emptyList()
        val encoded = encodeHeaderValues(header, values)
        if (encoded.isNotEmpty()) {
            headers[header.name] = encoded
        }
    }
    output.body.mediaType
        ?.toSpringMediaType()
        ?.let { headers.contentType = it }
    val body = extractBody(response)
    return ResponseEntity(body, headers, response.status.toHttpStatus())
}

@Suppress("UNCHECKED_CAST")
private fun extractHeaderDefinitions(headers: Headers): List<Header<*>> = headers.toList().map { it as Header<*> }

@Suppress("UNCHECKED_CAST")
private fun encodeHeaderValues(
    header: Header<*>,
    values: List<Any?>
): List<String> {
    val typedHeader = header as Header<Any>
    return values.filterNotNull().map { typedHeader.codec.encode(it) }
}

private fun extractBody(response: Any): Any? =
    when (response) {
        is Response0<*> -> response.body
        is Response1<*, *> -> response.body
        is Response2<*, *, *> -> response.body
        is Response3<*, *, *, *> -> response.body
        is Response4<*, *, *, *, *> -> response.body
        is Response5<*, *, *, *, *, *> -> response.body
        is Response6<*, *, *, *, *, *, *> -> response.body
        is Response7<*, *, *, *, *, *, *, *> -> response.body
        is Response8<*, *, *, *, *, *, *, *, *> -> response.body
        is Response9<*, *, *, *, *, *, *, *, *, *> -> response.body
        is Response10<*, *, *, *, *, *, *, *, *, *, *> -> response.body
        else -> null
    }

@Suppress("UNCHECKED_CAST")
private fun extractHeaderLists(response: Any): List<List<Any?>> =
    when (response) {
        is ResponseWithoutBody0 -> emptyList()
        is ResponseWithoutBody1<*> -> listOf(response.header1.asAnyList())
        is ResponseWithoutBody2<*, *> -> listOf(response.header1.asAnyList(), response.header2.asAnyList())
        is ResponseWithoutBody3<*, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList()
            )
        is ResponseWithoutBody4<*, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList()
            )
        is ResponseWithoutBody5<*, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList()
            )
        is ResponseWithoutBody6<*, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList()
            )
        is ResponseWithoutBody7<*, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList(),
                response.header7.asAnyList()
            )
        is ResponseWithoutBody8<*, *, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList(),
                response.header7.asAnyList(),
                response.header8.asAnyList()
            )
        is ResponseWithoutBody9<*, *, *, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList(),
                response.header7.asAnyList(),
                response.header8.asAnyList(),
                response.header9.asAnyList()
            )
        is ResponseWithoutBody10<*, *, *, *, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList(),
                response.header7.asAnyList(),
                response.header8.asAnyList(),
                response.header9.asAnyList(),
                response.header10.asAnyList()
            )
        is Response1<*, *> -> listOf(response.header1.asAnyList())
        is Response2<*, *, *> -> listOf(response.header1.asAnyList(), response.header2.asAnyList())
        is Response3<*, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList()
            )
        is Response4<*, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList()
            )
        is Response5<*, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList()
            )
        is Response6<*, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList()
            )
        is Response7<*, *, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList(),
                response.header7.asAnyList()
            )
        is Response8<*, *, *, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList(),
                response.header7.asAnyList(),
                response.header8.asAnyList()
            )
        is Response9<*, *, *, *, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList(),
                response.header7.asAnyList(),
                response.header8.asAnyList(),
                response.header9.asAnyList()
            )
        is Response10<*, *, *, *, *, *, *, *, *, *, *> ->
            listOf(
                response.header1.asAnyList(),
                response.header2.asAnyList(),
                response.header3.asAnyList(),
                response.header4.asAnyList(),
                response.header5.asAnyList(),
                response.header6.asAnyList(),
                response.header7.asAnyList(),
                response.header8.asAnyList(),
                response.header9.asAnyList(),
                response.header10.asAnyList()
            )
        else -> emptyList()
    }

@Suppress("UNCHECKED_CAST")
private fun <T> List<T>.asAnyList(): List<Any?> = this as List<Any?>
