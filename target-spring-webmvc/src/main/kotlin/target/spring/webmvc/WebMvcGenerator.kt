package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.Default
import dev.akif.tapik.Fixed
import dev.akif.tapik.Method
import dev.akif.tapik.Optional
import dev.akif.tapik.Required
import dev.akif.tapik.common.plugin.CompiledApi
import dev.akif.tapik.common.plugin.kotlinString
import dev.akif.tapik.common.plugin.optimizeKotlinImports

internal class WebMvcGenerator(
    private val packageName: String,
    private val serverName: String,
    private val controllerName: String
) {
    fun generate(compiled: CompiledApi): String {
        val model = webMvcApiModel(compiled, packageName, serverName, controllerName)
        return buildString {
            appendLine("package ${model.packageName}")
            appendLine()
            if (model.endpoints.any { endpoint -> endpoint.outputs.any { output -> output.bodies.isNotEmpty() } }) {
                appendLine("import dev.akif.tapik.common.spring.selectResponseMediaType")
                appendLine("import dev.akif.tapik.target.spring.webmvc.webMvcNotAcceptable")
            }
            if (model.endpoints.any { endpoint -> endpoint.paths.isNotEmpty() || endpoint.queries.isNotEmpty() || endpoint.headers.isNotEmpty() || endpoint.body != null }) {
                appendLine("import dev.akif.tapik.target.spring.webmvc.decodeRequest")
            }
            if (model.endpoints.any { endpoint -> endpoint.body != null }) {
                appendLine("import dev.akif.tapik.target.spring.webmvc.matchesRequestMediaType")
                appendLine("import dev.akif.tapik.target.spring.webmvc.webMvcUnsupportedMediaType")
            }
            if (
                model.endpoints.any { endpoint ->
                    endpoint.queries.any { query -> query.repeated && query.presence === Required } ||
                        endpoint.queries.any { query -> query.presence is Fixed<*> } ||
                        endpoint.headers.any { header -> header.presence is Fixed<*> }
                }
            ) {
                appendLine("import dev.akif.tapik.target.spring.webmvc.webMvcBadRequest")
            }
            if (model.endpoints.isNotEmpty()) {
                appendLine("import dev.akif.tapik.target.spring.webmvc.webMvcResponse")
            }
            appendLine()
            appendLine("public interface ${model.serverName} {")
            appendLine("    public val ${model.apiProperty}: ${model.apiType}")
            model.endpoints.forEach { endpoint ->
                appendLine()
                appendResponses(endpoint)
                appendLine()
                appendHandler(endpoint)
            }
            appendLine("}")
            appendLine()
            appendLine("@org.springframework.web.bind.annotation.RestController")
            appendLine("internal class ${model.controllerName}(")
            appendLine("    private val handler: ${model.serverName}")
            appendLine(") {")
            appendLine("    private val ${model.apiProperty}: ${model.apiType}")
            appendLine("        get() = handler.${model.apiProperty}")
            model.endpoints.forEach { endpoint ->
                appendLine()
                appendMapping(endpoint, model.serverName)
            }
            append('}')
        }.optimizeKotlinImports(model.packageName)
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

private fun StringBuilder.appendMapping(
    endpoint: WebMvcEndpointModel,
    serverName: String
) {
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
    endpoint.repeatedQueriesParameter?.let { queryParameters ->
        endpoint.queries.filter(WebMvcWireParameter::repeated).forEach { parameter ->
            val access = "$queryParameters[${parameter.wireName.kotlinString()}]"
            val value =
                if (parameter.presence === Required) {
                    "$access ?: webMvcBadRequest(${"${endpoint.id} query ${parameter.wireName} is required".kotlinString()})"
                } else {
                    access
                }
            appendLine("        val ${parameter.rawName} = $value")
        }
    }
    endpoint.queries.forEach { parameter -> appendDecodedParameter(endpoint, parameter, "query") }
    endpoint.headers.forEach { parameter -> appendDecodedParameter(endpoint, parameter, "header") }
    endpoint.body?.let { body -> appendDecodedBody(endpoint, body) }
    appendLine("        val response =")
    if (endpoint.handlerParameters.isEmpty()) {
        appendLine("            handler.${endpoint.handlerName}()")
    } else {
        appendLine("            handler.${endpoint.handlerName}(")
        endpoint.handlerParameters.forEachIndexed { index, parameter ->
            val suffix = if (index == endpoint.handlerParameters.lastIndex) "" else ","
            appendLine("                ${parameter.name} = ${parameter.name}$suffix")
        }
        appendLine("            )")
    }
    appendLine()
    appendLine("        return when (response) {")
    endpoint.outputs.forEach { output -> appendEncodedOutput(endpoint, output, serverName) }
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
        queries.filterNot(WebMvcWireParameter::repeated).forEach { parameter ->
            val required = parameter.presence === Required
            add(
                MappingParameter(
                    "@org.springframework.web.bind.annotation.RequestParam(name = ${parameter.wireName.kotlinString()}, required = $required) ${parameter.rawName}: kotlin.String${if (required) "" else "? = null"}",
                    required
                )
            )
        }
        repeatedQueriesParameter?.let { name ->
            add(
                MappingParameter(
                    "@org.springframework.web.bind.annotation.RequestParam $name: org.springframework.util.MultiValueMap<kotlin.String, kotlin.String>",
                    required = false
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
    val allowsBodylessResponse = outputs.any { output -> output.bodies.isEmpty() || output.allowsNoBody }
    if (produces.isNotEmpty() && !allowsBodylessResponse) attributes += "produces = ${produces.stringArray()}"
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
            "decodeRequest(${parameter.definitionAccess}.format, ${parameter.rawName}, $location)"
        } else {
            "decodeRequest(${parameter.definitionAccess}.format, ${parameter.rawName}, $location)"
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
                "            webMvcBadRequest(${"${endpoint.id} $kind ${parameter.wireName} does not match its fixed value".kotlinString()})"
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
            "$indentation    matchesRequestMediaType(${body.contentTypeName}, ${alternative.definitionAccess}.mediaType) -> decodeRequest(${alternative.definitionAccess}.format, ${body.rawName}, $location)"
        )
    }
    appendLine(
        "$indentation    else -> webMvcUnsupportedMediaType(${body.contentTypeName}, ${endpoint.id.kotlinString()})"
    )
    appendLine("$indentation}")
}

private fun StringBuilder.appendEncodedOutput(
    endpoint: WebMvcEndpointModel,
    output: WebMvcOutput,
    serverName: String
) {
    appendLine("            is $serverName.${endpoint.responseName}.${output.variantName} -> {")
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
    appendLine("                webMvcResponse(${output.statusCode}, headers, encodedBody)")
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
    val accept = requireNotNull(endpoint.acceptParameter)
    val offered = output.bodies.joinToString { body -> "${body.definitionAccess}.mediaType" }
    appendLine(
        "${indentation}when (selectResponseMediaType($accept, kotlin.collections.listOf($offered))) {"
    )
    output.bodies.forEach { body ->
        appendLine(
            "$indentation    ${body.definitionAccess}.mediaType -> ${body.definitionAccess}.mediaType to ${body.definitionAccess}.format.encode($bodyExpression)"
        )
    }
    appendLine("$indentation    else -> webMvcNotAcceptable($accept, ${endpoint.id.kotlinString()})")
    appendLine("$indentation}")
}
