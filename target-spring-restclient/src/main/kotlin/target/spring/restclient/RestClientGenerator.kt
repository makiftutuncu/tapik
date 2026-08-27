package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.Default
import dev.akif.tapik.Fixed
import dev.akif.tapik.Optional
import dev.akif.tapik.Required
import dev.akif.tapik.common.plugin.CompiledApi
import dev.akif.tapik.common.plugin.kotlinString
import dev.akif.tapik.common.plugin.optimizeKotlinImports

internal class RestClientGenerator(
    private val packageName: String,
    private val clientName: String
) {
    fun generate(compiled: CompiledApi): String {
        val model = restClientApiModel(compiled, packageName, clientName)
        return buildString {
            appendLine("package ${model.packageName}")
            appendLine()
            appendLine("import dev.akif.tapik.target.spring.restclient.selectResponseBodyMediaType")
            appendLine()
            appendLine("public interface ${model.clientName} {")
            appendLine("    public val ${model.apiProperty}: ${model.apiType}")
            appendLine()
            appendLine(
                "    public val restClientTransport: dev.akif.tapik.target.spring.restclient.RestClientTransport"
            )
            model.endpoints.forEach { endpoint ->
                appendLine()
                appendResponse(endpoint)
                appendLine()
                appendMethod(endpoint)
            }
            if (model.endpoints.isNotEmpty()) {
                appendLine()
                appendDecodeHelpers(
                    includeFixedHeaders = model.endpoints.any { endpoint ->
                        endpoint.outputs.any { output -> output.fixedHeaders.isNotEmpty() }
                    }
                )
            }
            append('}')
        }.optimizeKotlinImports(model.packageName)
    }
}

private fun StringBuilder.appendMethod(endpoint: RestClientEndpointModel) {
    endpoint.summary?.let { summary ->
        appendLine("    /** ${summary.replace("*/", "*&#47;")} */")
    }
    if (endpoint.parameters.isEmpty()) {
        appendLine("    public fun ${endpoint.methodName}(): ${endpoint.responseName} {")
    } else {
        appendLine("    public fun ${endpoint.methodName}(")
        endpoint.parameters.forEachIndexed { index, parameter ->
            val default = parameter.defaultExpression?.let { expression -> " = $expression" }.orEmpty()
            val suffix = if (index == endpoint.parameters.lastIndex) "" else ","
            appendLine("        ${parameter.name}: ${parameter.type}$default$suffix")
        }
        appendLine("    ): ${endpoint.responseName} {")
    }
    appendLine("        val endpoint = ${endpoint.endpointAccess}")
    appendLine("        val response =")
    appendLine("            restClientTransport.exchange(")
    appendLine("                method = endpoint.method,")
    appendUri(endpoint)
    appendLine(",")
    appendHeaders(endpoint)
    appendLine(",")
    appendBody(endpoint)
    appendLine()
    appendLine("            )")
    appendLine()
    appendLine("        return when {")
    endpoint.outputs.forEach { output -> appendOutput(endpoint, output) }
    appendLine(
        "            else -> kotlin.error(\"Unexpected status \${response.status.code} for ${endpoint.id}\")"
    )
    appendLine("        }")
    appendLine("    }")
}

private fun StringBuilder.appendUri(endpoint: RestClientEndpointModel) {
    appendLine("                uri = { uriBuilder ->")
    appendLine("                    uriBuilder")
    appendLine("                        .path(${endpoint.pathTemplate.kotlinString()})")
    endpoint.queries.forEach { query ->
        val encoded = "${query.definitionAccess}.format.encode(${query.name})"
        when {
            query.optional && query.repeated ->
                appendLine(
                    "                        .apply { ${query.name}?.let { values -> queryParam(${query.wireName.kotlinString()}, *${query.definitionAccess}.format.encode(values).toTypedArray()) } }"
                )
            query.optional ->
                appendLine(
                    "                        .apply { ${query.name}?.let { value -> queryParam(${query.wireName.kotlinString()}, ${query.definitionAccess}.format.encode(value)) } }"
                )
            query.repeated ->
                appendLine(
                    "                        .queryParam(${query.wireName.kotlinString()}, *$encoded.toTypedArray())"
                )
            else ->
                appendLine("                        .queryParam(${query.wireName.kotlinString()}, $encoded)")
        }
    }
    if (endpoint.paths.isEmpty()) {
        append("                        .build()")
    } else {
        appendLine("                        .build(")
        appendLine("                            mapOf(")
        endpoint.paths.forEachIndexed { index, path ->
            val suffix = if (index == endpoint.paths.lastIndex) "" else ","
            appendLine(
                "                                ${path.wireName.kotlinString()} to ${path.definitionAccess}.format.encode(${path.name})$suffix"
            )
        }
        appendLine("                            )")
        append("                        )")
    }
    appendLine()
    append("                }")
}

private fun StringBuilder.appendHeaders(endpoint: RestClientEndpointModel) {
    if (endpoint.headers.isEmpty()) {
        append("                headers = emptyMap()")
        return
    }
    appendLine("                headers = buildMap {")
    endpoint.headers.forEach { header ->
        val valueExpression =
            when (header.presence) {
                is Fixed<*> -> "${header.definitionAccess}.presence.value"
                else -> requireNotNull(header.name)
            }
        if (header.presence === Optional) {
            appendLine(
                "                    $valueExpression?.let { value -> put(${header.wireName.kotlinString()}, listOf(${header.definitionAccess}.format.encode(value))) }"
            )
        } else {
            appendLine(
                "                    put(${header.wireName.kotlinString()}, listOf(${header.definitionAccess}.format.encode($valueExpression)))"
            )
        }
    }
    append("                }")
}

private fun StringBuilder.appendBody(endpoint: RestClientEndpointModel) {
    val body = endpoint.body
    when {
        body == null -> append("                body = null")
        body.optional ->
            append(
                "                body = ${body.parameterName}?.let { value -> dev.akif.tapik.target.spring.restclient.RestClientRequestBody(${body.definitionAccess}.mediaType, ${body.definitionAccess}.format.encode(value)) }"
            )
        else ->
            append(
                "                body = dev.akif.tapik.target.spring.restclient.RestClientRequestBody(${body.definitionAccess}.mediaType, ${body.definitionAccess}.format.encode(${body.parameterName}))"
            )
    }
}

private fun StringBuilder.appendOutput(
    endpoint: RestClientEndpointModel,
    output: RestClientOutput
) {
    appendLine("            ${output.definitionAccess}.matcher.matches(response.status) -> {")
    output.fixedHeaders.forEach { header -> appendFixedHeader(endpoint, header) }
    if (output.bodies.isNotEmpty()) {
        appendDecodedBody(endpoint, output)
    } else if (output.allowsNoBody) {
        appendLine("                ${output.bodyMediaTypeSelection(endpoint)}")
    }
    output.headers.forEach { header -> appendDecodedHeader(endpoint, header) }
    val arguments =
        buildList {
            if (output.bodies.isNotEmpty()) add("decodedBody")
            addAll(output.headers.map(RestClientOutputHeader::name))
        }
    val construction =
        if (arguments.isEmpty()) {
            "${endpoint.responseName}.${output.variantName}"
        } else {
            "${endpoint.responseName}.${output.variantName}(${arguments.joinToString()})"
        }
    appendLine("                $construction")
    appendLine("            }")
}

private fun StringBuilder.appendFixedHeader(
    endpoint: RestClientEndpointModel,
    header: RestClientFixedOutputHeader
) {
    appendLine("                requireFixedHeader(")
    appendLine("                    response = response,")
    appendLine("                    name = ${header.wireName.kotlinString()},")
    appendLine("                    format = ${header.definitionAccess}.format,")
    appendLine("                    expected = ${header.definitionAccess}.presence.value,")
    appendLine("                    endpointId = ${endpoint.id.kotlinString()}")
    appendLine("                )")
}

private fun StringBuilder.appendDecodedBody(
    endpoint: RestClientEndpointModel,
    output: RestClientOutput
) {
    val selection = output.bodyMediaTypeSelection(endpoint)
    if (output.bodies.size == 1) {
        val body = output.bodies.single()
        if (output.allowsNoBody) {
            appendLine("                val decodedBody =")
            appendLine("                    if ($selection == null) {")
            appendLine("                        null")
            appendLine("                    } else {")
            appendLine(
                "                        decodeBody(${body.definitionAccess}.format, response.body, ${endpoint.id.kotlinString()})"
            )
            appendLine("                    }")
        } else {
            appendLine("                $selection")
            appendLine(
                "                val decodedBody = decodeBody(${body.definitionAccess}.format, response.body, ${endpoint.id.kotlinString()})"
            )
        }
        return
    }

    appendLine("                val decodedBody =")
    appendLine("                    when ($selection) {")
    output.bodies.forEach { body ->
        appendLine(
            "                        ${body.definitionAccess}.mediaType -> decodeBody(${body.definitionAccess}.format, response.body, ${endpoint.id.kotlinString()})"
        )
    }
    if (output.allowsNoBody) appendLine("                        null -> null")
    appendLine(
        "                        else -> kotlin.error(\"Unexpected response media type \${response.mediaType} for ${endpoint.id}\")"
    )
    appendLine("                    }")
}

private fun RestClientOutput.bodyMediaTypeSelection(endpoint: RestClientEndpointModel): String {
    val offered =
        if (bodies.isEmpty()) {
            "kotlin.collections.emptyList()"
        } else {
            bodies.joinToString(
                prefix = "kotlin.collections.listOf(",
                postfix = ")"
            ) { body -> "${body.definitionAccess}.mediaType" }
        }
    return "selectResponseBodyMediaType(response = response, offered = $offered, allowsNoBody = $allowsNoBody, endpointId = ${endpoint.id.kotlinString()})"
}

private fun StringBuilder.appendDecodedHeader(
    endpoint: RestClientEndpointModel,
    header: RestClientOutputHeader
) {
    val raw =
        "response.headers.entries.firstOrNull { (name, _) -> name.equals(${header.wireName.kotlinString()}, ignoreCase = true) }?.value?.firstOrNull()"
    when (header.presence) {
        Required ->
            appendLine(
                "                val ${header.name} = decodeHeader(${header.definitionAccess}.format, $raw ?: kotlin.error(\"Missing response header ${header.wireName} for ${endpoint.id}\"), ${endpoint.id.kotlinString()})"
            )
        Optional ->
            appendLine(
                "                val ${header.name} = $raw?.let { value -> decodeHeader(${header.definitionAccess}.format, value, ${endpoint.id.kotlinString()}) }"
            )
        is Default<*> ->
            appendLine(
                "                val ${header.name} = $raw?.let { value -> decodeHeader(${header.definitionAccess}.format, value, ${endpoint.id.kotlinString()}) } ?: ${header.definitionAccess}.presence.value"
            )
        is Fixed<*> -> Unit
    }
}

private fun StringBuilder.appendDecodeHelpers(includeFixedHeaders: Boolean) {
    appendLine("    private fun <Value : kotlin.Any> decodeBody(")
    appendLine("        format: dev.akif.tapik.ByteArrayFormat<Value>,")
    appendLine("        bytes: kotlin.ByteArray,")
    appendLine("        endpointId: kotlin.String")
    appendLine("    ): Value =")
    appendLine("        when (val result = format.decode(bytes)) {")
    appendLine("            is dev.akif.tapik.DecodeResult.Success -> result.value")
    appendLine(
        "            is dev.akif.tapik.DecodeResult.Failure -> kotlin.error(\"Cannot decode response body for \$endpointId: \" + result.errors.joinToString { it.message })"
    )
    appendLine("        }")
    appendLine()
    appendLine("    private fun <Value : kotlin.Any> decodeHeader(")
    appendLine("        format: dev.akif.tapik.StringFormat<Value>,")
    appendLine("        value: kotlin.String,")
    appendLine("        endpointId: kotlin.String")
    appendLine("    ): Value =")
    appendLine("        when (val result = format.decode(value)) {")
    appendLine("            is dev.akif.tapik.DecodeResult.Success -> result.value")
    appendLine(
        "            is dev.akif.tapik.DecodeResult.Failure -> kotlin.error(\"Cannot decode response header for \$endpointId: \" + result.errors.joinToString { it.message })"
    )
    appendLine("        }")
    appendLine()
    if (includeFixedHeaders) appendFixedHeaderHelper()
}

private fun StringBuilder.appendFixedHeaderHelper() {
    appendLine("    private fun <Value : kotlin.Any> requireFixedHeader(")
    appendLine("        response: dev.akif.tapik.target.spring.restclient.RestClientResponse,")
    appendLine("        name: kotlin.String,")
    appendLine("        format: dev.akif.tapik.StringFormat<Value>,")
    appendLine("        expected: Value,")
    appendLine("        endpointId: kotlin.String")
    appendLine("    ) {")
    appendLine("        val actual =")
    appendLine("            response.headers.entries")
    appendLine("                .filter { (headerName, _) -> headerName.equals(name, ignoreCase = true) }")
    appendLine("                .flatMap { (_, values) -> values }")
    appendLine("        val encoded = format.encode(expected)")
    appendLine("        if (actual != listOf(encoded)) {")
    appendLine(
        "            kotlin.error(\"Unexpected fixed response header \$name for \$endpointId: expected exactly [\$encoded], got \$actual\")"
    )
    appendLine("        }")
    appendLine("    }")
    appendLine()
}
