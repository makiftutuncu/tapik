package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.Default
import dev.akif.tapik.Fixed
import dev.akif.tapik.Method
import dev.akif.tapik.Optional
import dev.akif.tapik.Required
import dev.akif.tapik.plugin.core.CompiledApi

internal class WebMvcGenerator(
    private val packageName: String,
    private val serverName: String
) {
    fun generate(compiled: CompiledApi): String {
        val model = webMvcApiModel(compiled, packageName, serverName)
        return buildString {
            appendLine("package ${model.packageName}")
            appendLine()
            appendLine("import dev.akif.tapik.spring.toSpringMediaType")
            appendLine()
            appendLine("public interface ${model.serverName} {")
            appendLine("    public val ${model.apiProperty}: ${model.apiType}")
            model.endpoints.forEach { endpoint ->
                appendLine()
                appendResponses(endpoint)
                appendLine()
                appendHandler(endpoint)
                appendLine()
                appendMapping(endpoint)
            }
            if (model.endpoints.isNotEmpty()) {
                appendLine()
                appendHelpers()
            }
            append('}')
        }
    }
}

private fun StringBuilder.appendHandler(endpoint: WebMvcEndpointModel) {
    endpoint.summary?.let { summary -> appendLine("    /** ${summary.replace("*/", "*&#47;")} */") }
    if (endpoint.handlerParameters.isEmpty()) {
        appendLine("    public fun ${endpoint.handlerName}(): ${endpoint.responseName}")
        return
    }
    appendLine("    public fun ${endpoint.handlerName}(")
    endpoint.handlerParameters.forEachIndexed { index, parameter ->
        val default = parameter.defaultExpression?.let { expression -> " = $expression" }.orEmpty()
        val suffix = if (index == endpoint.handlerParameters.lastIndex) "" else ","
        appendLine("        ${parameter.name}: ${parameter.type}$default$suffix")
    }
    appendLine("    ): ${endpoint.responseName}")
}

private fun StringBuilder.appendMapping(endpoint: WebMvcEndpointModel) {
    appendLine("    ${endpoint.mappingAnnotation()}")
    val parameters = endpoint.mappingParameters()
    if (parameters.isEmpty()) {
        appendLine(
            "    public fun ${endpoint.mappingName}(): org.springframework.http.ResponseEntity<kotlin.ByteArray> {"
        )
    } else {
        appendLine("    public fun ${endpoint.mappingName}(")
        parameters.forEachIndexed { index, parameter ->
            val suffix = if (index == parameters.lastIndex) "" else ","
            appendLine("        ${parameter.declaration}$suffix")
        }
        appendLine("    ): org.springframework.http.ResponseEntity<kotlin.ByteArray> {")
    }
    endpoint.paths.forEach { parameter -> appendDecodedParameter(endpoint, parameter, "path") }
    endpoint.queries.forEach { parameter -> appendDecodedParameter(endpoint, parameter, "query") }
    endpoint.headers.forEach { parameter -> appendDecodedParameter(endpoint, parameter, "header") }
    endpoint.body?.let { body -> appendDecodedBody(endpoint, body) }
    appendLine("        val response =")
    if (endpoint.handlerParameters.isEmpty()) {
        appendLine("            ${endpoint.handlerName}()")
    } else {
        appendLine("            ${endpoint.handlerName}(")
        endpoint.handlerParameters.forEachIndexed { index, parameter ->
            val suffix = if (index == endpoint.handlerParameters.lastIndex) "" else ","
            appendLine("                ${parameter.name} = ${parameter.name}$suffix")
        }
        appendLine("            )")
    }
    appendLine()
    appendLine("        return when (response) {")
    endpoint.outputs.forEach { output -> appendEncodedOutput(endpoint, output) }
    appendLine("        }")
    appendLine("    }")
}

private data class MappingParameter(
    val declaration: String,
    val required: Boolean
)

private fun WebMvcEndpointModel.mappingParameters(): List<MappingParameter> =
    buildList {
        paths.forEach { parameter ->
            add(
                MappingParameter(
                    "@org.springframework.web.bind.annotation.PathVariable(name = ${parameter.wireName.kotlinString()}) ${parameter.rawName}: kotlin.String",
                    required = true
                )
            )
        }
        queries.forEach { parameter ->
            val required = parameter.presence === Required
            val rawType = if (parameter.repeated) "kotlin.collections.List<kotlin.String>" else "kotlin.String"
            add(
                MappingParameter(
                    "@org.springframework.web.bind.annotation.RequestParam(name = ${parameter.wireName.kotlinString()}, required = $required) ${parameter.rawName}: $rawType${if (required) "" else "? = null"}",
                    required
                )
            )
        }
        headers.forEach { parameter ->
            val required = parameter.presence === Required || parameter.presence is Fixed<*>
            add(
                MappingParameter(
                    "@org.springframework.web.bind.annotation.RequestHeader(name = ${parameter.wireName.kotlinString()}, required = $required) ${parameter.rawName}: kotlin.String${if (required) "" else "? = null"}",
                    required
                )
            )
        }
        body?.let { body ->
            add(
                MappingParameter(
                    "@org.springframework.web.bind.annotation.RequestBody(required = ${!body.optional}) ${body.rawName}: kotlin.ByteArray${if (body.optional) "? = null" else ""}",
                    required = !body.optional
                )
            )
            add(
                MappingParameter(
                    "@org.springframework.web.bind.annotation.RequestHeader(name = org.springframework.http.HttpHeaders.CONTENT_TYPE, required = ${!body.optional}) ${body.contentTypeName}: kotlin.String${if (body.optional) "? = null" else ""}",
                    required = !body.optional
                )
            )
        }
        acceptParameter?.let { name ->
            add(
                MappingParameter(
                    "@org.springframework.web.bind.annotation.RequestHeader(name = org.springframework.http.HttpHeaders.ACCEPT, required = false) $name: kotlin.String? = null",
                    required = false
                )
            )
        }
    }.sortedByDescending(MappingParameter::required)

private fun WebMvcEndpointModel.mappingAnnotation(): String {
    val attributes = mutableListOf("path = [${pathTemplate.kotlinString()}]")
    val consumes = body?.alternatives?.map(WebMvcBodyAlternative::mediaType).orEmpty().distinct()
    if (consumes.isNotEmpty() && body?.optional == false) {
        attributes += "consumes = ${consumes.stringArray()}"
    }
    val produces = outputs.flatMap { output -> output.bodies.map(WebMvcOutputBody::mediaType) }.distinct()
    if (produces.isNotEmpty()) attributes += "produces = ${produces.stringArray()}"
    val annotation =
        when (method) {
            Method.GET -> "GetMapping"
            Method.POST -> "PostMapping"
            Method.PUT -> "PutMapping"
            Method.PATCH -> "PatchMapping"
            Method.DELETE -> "DeleteMapping"
            Method.HEAD, Method.OPTIONS, Method.TRACE -> "RequestMapping"
            Method.CONNECT, Method.QUERY -> error("Unsupported Spring WebMVC method $method")
        }
    if (annotation == "RequestMapping") {
        attributes.add(0, "method = [org.springframework.web.bind.annotation.RequestMethod.$method]")
    }
    return "@org.springframework.web.bind.annotation.$annotation(${attributes.joinToString()})"
}

private fun List<String>.stringArray(): String = joinToString(prefix = "[", postfix = "]") { it.kotlinString() }

private fun StringBuilder.appendDecodedParameter(
    endpoint: WebMvcEndpointModel,
    parameter: WebMvcWireParameter,
    kind: String
) {
    val location = "${endpoint.id} $kind ${parameter.wireName}".kotlinString()
    val decode =
        if (parameter.repeated) {
            "decodeStrings(${parameter.definitionAccess}.format, ${parameter.rawName}, $location)"
        } else {
            "decodeString(${parameter.definitionAccess}.format, ${parameter.rawName}, $location)"
        }
    when (val presence = parameter.presence) {
        Required -> appendLine("        val ${parameter.name} = $decode")
        Optional ->
            appendLine(
                "        val ${parameter.name} = ${parameter.rawName}?.let { raw -> ${decode.replace(parameter.rawName, "raw")} }"
            )
        is Default<*> ->
            appendLine(
                "        val ${parameter.name} = ${parameter.rawName}?.let { raw -> ${decode.replace(parameter.rawName, "raw")} } ?: ${parameter.definitionAccess}.presence.value"
            )
        is Fixed<*> -> {
            appendLine("        val ${parameter.name} = $decode")
            appendLine("        if (${parameter.name} != ${parameter.definitionAccess}.presence.value) {")
            appendLine(
                "            badRequest(${"${endpoint.id} $kind ${parameter.wireName} does not match its fixed value".kotlinString()})"
            )
            appendLine("        }")
        }
    }
}

private fun StringBuilder.appendDecodedBody(
    endpoint: WebMvcEndpointModel,
    body: WebMvcRequestBody
) {
    val location = "${endpoint.id} body".kotlinString()
    if (body.optional) {
        appendLine("        val ${body.name} =")
        appendLine("            if (${body.rawName} == null) {")
        appendLine("                null")
        appendLine("            } else {")
        appendBodySelection(endpoint, body, location, indentation = "                ")
        appendLine("            }")
    } else {
        appendLine("        val ${body.name} =")
        appendBodySelection(endpoint, body, location, indentation = "            ")
    }
}

private fun StringBuilder.appendBodySelection(
    endpoint: WebMvcEndpointModel,
    body: WebMvcRequestBody,
    location: String,
    indentation: String
) {
    appendLine("${indentation}when {")
    body.alternatives.forEach { alternative ->
        appendLine(
            "$indentation    mediaTypeCompatible(${body.contentTypeName}, ${alternative.definitionAccess}.mediaType) -> decodeBody(${alternative.definitionAccess}.format, ${body.rawName}, $location)"
        )
    }
    appendLine(
        "$indentation    else -> unsupportedMediaType(${body.contentTypeName}, ${endpoint.id.kotlinString()})"
    )
    appendLine("$indentation}")
}

private fun StringBuilder.appendEncodedOutput(
    endpoint: WebMvcEndpointModel,
    output: WebMvcOutput
) {
    appendLine("            is ${endpoint.responseName}.${output.variantName} -> {")
    if (output.headers.isEmpty()) {
        appendLine("                val headers = kotlin.collections.emptyMap<kotlin.String, kotlin.collections.List<kotlin.String>>()")
    } else {
        appendLine("                val headers = buildMap {")
        output.headers.forEach { header ->
            val value =
                when (header.presence) {
                    is Fixed<*> -> "${header.definitionAccess}.presence.value"
                    is Default<*> ->
                        "response.${requireNotNull(header.name)} ?: ${header.definitionAccess}.presence.value"
                    Required, Optional -> "response.${requireNotNull(header.name)}"
                }
            if (header.presence === Optional) {
                appendLine(
                    "                    $value?.let { header -> put(${header.wireName.kotlinString()}, listOf(${header.definitionAccess}.format.encode(header))) }"
                )
            } else {
                appendLine(
                    "                    put(${header.wireName.kotlinString()}, listOf(${header.definitionAccess}.format.encode($value)))"
                )
            }
        }
        appendLine("                }")
    }
    appendEncodedBody(endpoint, output)
    appendLine("                responseEntity(${output.statusCode}, headers, encodedBody)")
    appendLine("            }")
}

private fun StringBuilder.appendEncodedBody(
    endpoint: WebMvcEndpointModel,
    output: WebMvcOutput
) {
    when {
        output.bodies.isEmpty() -> appendLine("                val encodedBody: kotlin.Pair<dev.akif.tapik.MediaType, kotlin.ByteArray>? = null")
        output.allowsNoBody -> {
            appendLine("                val encodedBody =")
            appendLine("                    response.body?.let { body ->")
            appendBodyEncoding(endpoint, output, "body", "                        ")
            appendLine("                    }")
        }
        else -> {
            appendLine("                val encodedBody =")
            appendBodyEncoding(endpoint, output, "response.body", "                    ")
        }
    }
}

private fun StringBuilder.appendBodyEncoding(
    endpoint: WebMvcEndpointModel,
    output: WebMvcOutput,
    bodyExpression: String,
    indentation: String
) {
    if (output.bodies.size == 1) {
        val body = output.bodies.single()
        appendLine(
            "$indentation${body.definitionAccess}.mediaType to ${body.definitionAccess}.format.encode($bodyExpression)"
        )
        return
    }
    val accept = requireNotNull(endpoint.acceptParameter)
    appendLine("${indentation}when {")
    output.bodies.forEach { body ->
        appendLine(
            "$indentation    accepts($accept, ${body.definitionAccess}.mediaType) -> ${body.definitionAccess}.mediaType to ${body.definitionAccess}.format.encode($bodyExpression)"
        )
    }
    appendLine("$indentation    else -> notAcceptable($accept, ${endpoint.id.kotlinString()})")
    appendLine("$indentation}")
}

private fun StringBuilder.appendHelpers() {
    appendLine("    private fun <Value : kotlin.Any> decodeString(")
    appendLine("        format: dev.akif.tapik.StringFormat<Value>,")
    appendLine("        raw: kotlin.String,")
    appendLine("        location: kotlin.String")
    appendLine("    ): Value = decode(format.decode(raw), location)")
    appendLine()
    appendLine("    private fun <Value : kotlin.Any> decodeStrings(")
    appendLine("        format: dev.akif.tapik.Format<Value, kotlin.collections.List<kotlin.String>>,")
    appendLine("        raw: kotlin.collections.List<kotlin.String>,")
    appendLine("        location: kotlin.String")
    appendLine("    ): Value = decode(format.decode(raw), location)")
    appendLine()
    appendLine("    private fun <Value : kotlin.Any> decodeBody(")
    appendLine("        format: dev.akif.tapik.ByteArrayFormat<Value>,")
    appendLine("        raw: kotlin.ByteArray,")
    appendLine("        location: kotlin.String")
    appendLine("    ): Value = decode(format.decode(raw), location)")
    appendLine()
    appendLine("    private fun <Value : kotlin.Any> decode(")
    appendLine("        result: dev.akif.tapik.DecodeResult<Value>,")
    appendLine("        location: kotlin.String")
    appendLine("    ): Value =")
    appendLine("        when (result) {")
    appendLine("            is dev.akif.tapik.DecodeResult.Success -> result.value")
    appendLine("            is dev.akif.tapik.DecodeResult.Failure ->")
    appendLine("                badRequest(\"Cannot decode \$location: \" + result.errors.joinToString { it.message })")
    appendLine("        }")
    appendLine()
    appendLine("    private fun mediaTypeCompatible(actual: kotlin.String?, expected: dev.akif.tapik.MediaType): kotlin.Boolean =")
    appendLine("        try {")
    appendLine("            actual != null && org.springframework.http.MediaType.parseMediaType(actual).isCompatibleWith(expected.toSpringMediaType())")
    appendLine("        } catch (_: org.springframework.http.InvalidMediaTypeException) {")
    appendLine("            false")
    appendLine("        }")
    appendLine()
    appendLine("    private fun accepts(accept: kotlin.String?, offered: dev.akif.tapik.MediaType): kotlin.Boolean =")
    appendLine("        if (accept.isNullOrBlank()) {")
    appendLine("            true")
    appendLine("        } else {")
    appendLine("            try {")
    appendLine("                org.springframework.http.MediaType.parseMediaTypes(accept).any { requested ->")
    appendLine("                    requested.isCompatibleWith(offered.toSpringMediaType())")
    appendLine("                }")
    appendLine("            } catch (_: org.springframework.http.InvalidMediaTypeException) {")
    appendLine("                false")
    appendLine("            }")
    appendLine("        }")
    appendLine()
    appendLine("    private fun responseEntity(")
    appendLine("        status: kotlin.Int,")
    appendLine("        headers: kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>,")
    appendLine("        body: kotlin.Pair<dev.akif.tapik.MediaType, kotlin.ByteArray>?")
    appendLine("    ): org.springframework.http.ResponseEntity<kotlin.ByteArray> {")
    appendLine("        val springHeaders = org.springframework.http.HttpHeaders()")
    appendLine("        headers.forEach { (name, values) -> springHeaders.addAll(name, values) }")
    appendLine("        body?.let { (mediaType, _) -> springHeaders.contentType = mediaType.toSpringMediaType() }")
    appendLine("        return org.springframework.http.ResponseEntity(body?.second, springHeaders, status)")
    appendLine("    }")
    appendLine()
    appendLine("    private fun badRequest(message: kotlin.String): kotlin.Nothing =")
    appendLine("        throw org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, message)")
    appendLine()
    appendLine("    private fun unsupportedMediaType(actual: kotlin.String?, endpointId: kotlin.String): kotlin.Nothing =")
    appendLine("        throw org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE, \"Unsupported Content-Type \$actual for \$endpointId\")")
    appendLine()
    appendLine("    private fun notAcceptable(accept: kotlin.String?, endpointId: kotlin.String): kotlin.Nothing =")
    appendLine("        throw org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_ACCEPTABLE, \"No response body matches Accept \$accept for \$endpointId\")")
}
