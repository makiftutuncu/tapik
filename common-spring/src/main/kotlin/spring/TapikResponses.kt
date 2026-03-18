package dev.akif.tapik.spring

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import dev.akif.tapik.TapikResponse
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import org.springframework.http.MediaType as SpringMediaType

/**
 * Converts a generated Tapik response into a Spring [ResponseEntity].
 *
 * @receiver Tapik-managed response instance being converted.
 * @param status HTTP status resolved for the response variant.
 * @param headers encoded HTTP headers grouped by header name.
 * @param mediaType optional content type advertised by the selected endpoint output.
 * @param body optional response body already encoded for the wire.
 * @return Spring [ResponseEntity] carrying the response status, headers, and body.
 */
fun TapikResponse.toResponseEntity(
    status: Status,
    headers: Map<String, List<String>> = emptyMap(),
    mediaType: MediaType? = null,
    body: ByteArray? = null
): ResponseEntity<Any> {
    val httpHeaders = HttpHeaders()
    headers.forEach { (name, values) -> httpHeaders.addAll(name, values) }
    mediaType?.let { httpHeaders.contentType = it.toSpringMediaType() }

    val builder = ResponseEntity.status(status.toHttpStatusCode()).headers(httpHeaders)

    return if (body == null) {
        builder.build()
    } else {
        builder.body(body)
    }
}

/**
 * Shared Spring MVC advice that writes generated Tapik responses through Tapik codecs instead of
 * delegating serialization to Spring's default message converters.
 */
interface TapikResponseBodyAdvice : ResponseBodyAdvice<TapikResponse> {
    /**
     * Returns whether this advice instance should handle the provided Tapik response type.
     *
     * Generated Spring MVC code overrides this so multiple advice beans can coexist while each one
     * only handles the response hierarchies declared in its generated source file.
     *
     * @param responseType declared controller return type.
     * @return `true` when this advice should convert the response.
     */
    fun supportsTapikResponseType(responseType: Class<*>): Boolean

    /**
     * Converts the provided Tapik response into a Spring [ResponseEntity].
     *
     * Generated Spring MVC code overrides this and supplies the response status, headers, media
     * type, and encoded body based on the concrete generated response variant.
     *
     * @param body Tapik response returned by the controller.
     * @return Spring [ResponseEntity] representing [body].
     */
    fun toResponseEntity(body: TapikResponse): ResponseEntity<Any>

    /**
     * Returns whether Spring should invoke this advice for the current controller return type.
     *
     * The advice only applies to Tapik-managed responses whose declared return type is accepted by
     * [supportsTapikResponseType].
     *
     * @param returnType declared controller return type metadata.
     * @param converterType Spring message converter selected for the response.
     * @return `true` when this advice should handle the response.
     */
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean =
        TapikResponse::class.java.isAssignableFrom(returnType.parameterType) &&
            supportsTapikResponseType(returnType.parameterType)

    /**
     * Writes the Tapik-managed response with Tapik-controlled encoding and suppresses Spring's
     * normal body serialization.
     *
     * @param body Tapik response returned by the controller.
     * @param returnType declared controller return type metadata.
     * @param selectedContentType content type selected by Spring.
     * @param selectedConverterType Spring message converter selected for the response.
     * @param request current HTTP request abstraction.
     * @param response current HTTP response abstraction.
     * @return `null` after the encoded response body has been written, or `null` immediately when
     * [body] is `null`.
     */
    override fun beforeBodyWrite(
        body: TapikResponse?,
        returnType: MethodParameter,
        selectedContentType: SpringMediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): TapikResponse? {
        if (body == null) {
            return null
        }

        val responseEntity = toResponseEntity(body)
        response.setStatusCode(responseEntity.statusCode)
        response.headers.putAll(responseEntity.headers)

        return when (val responseBody = responseEntity.body) {
            null -> null
            is ByteArray -> {
                response.body.write(responseBody)
                null
            }

            else ->
                error(
                    "TapikResponse conversion produced unsupported body type: " + responseBody::class.qualifiedName
                )
        }
    }
}
